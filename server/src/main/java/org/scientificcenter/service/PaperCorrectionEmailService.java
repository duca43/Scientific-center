package org.scientificcenter.service;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.scientificcenter.dto.CreateScientificPaperDto;
import org.scientificcenter.dto.ReviewPdfDocumentResponseDto;
import org.scientificcenter.model.ScientificPaper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

@Service
@Slf4j
public class PaperCorrectionEmailService implements JavaDelegate {

    private final EmailService emailService;
    private final UserService userService;
    private final ScientificPaperService scientificPaperService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private static final String EMAIL_SUBJECT = "Scientific paper - requested correction";
    private static final String SCIENTIFIC_PAPER_DTO = "scientificPaperDto";
    private final static String CORRECTION_TIME = "correctionTime";
    private final static String CORRECTIONS_DEADLINE = "correctionsDeadline";

    @Autowired
    public PaperCorrectionEmailService(final EmailService emailService, final UserService userService, final ScientificPaperService scientificPaperService, final SimpMessagingTemplate simpMessagingTemplate) {
        this.emailService = emailService;
        this.userService = userService;
        this.scientificPaperService = scientificPaperService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public void execute(final DelegateExecution delegateExecution) throws Exception {
        log.info("Executing service named 'Paper correction notification'");

        final String authorUsername = (String) delegateExecution.getVariable("author");
        final String authorEmail = this.userService.findByUsername(authorUsername).getEmail();
        final String mainEditorUsername = (String) delegateExecution.getVariable("main_editor");
        Long correctionTime = (Long) delegateExecution.getVariable(CORRECTION_TIME);

        final CreateScientificPaperDto createScientificPaperDto = (CreateScientificPaperDto) delegateExecution.getVariable(SCIENTIFIC_PAPER_DTO);
        log.info("Retrieved execution variable scientificPaperDto -> {}", createScientificPaperDto);

        final boolean afterReviewsCorrection = delegateExecution.getVariable("decision") != null;

        if (afterReviewsCorrection) {
            log.info("Updating scientific paper with id '{}': is paper formatted well flag -> false", createScientificPaperDto.getId());

            final ScientificPaper scientificPaper = this.scientificPaperService.findById(createScientificPaperDto.getId());

            scientificPaper.setPdfFormattedWell(false);
            scientificPaper.setRequestedChanges(true);
            this.scientificPaperService.save(scientificPaper);

            final ReviewPdfDocumentResponseDto reviewPdfDocumentResponseDto = ReviewPdfDocumentResponseDto.builder()
                    .scientificPaperId(scientificPaper.getId())
                    .formattedWell(false)
                    .requestedChanges(true)
                    .message("Corrections are requested after scientific paper titled '" + scientificPaper.getTitle() + "' is reviewed. Please upload a new version od PDF document and reply on reviews.")
                    .build();
            this.simpMessagingTemplate.convertAndSend("/scientific_paper/review_pdf_document", reviewPdfDocumentResponseDto);

            correctionTime = (Long) delegateExecution.getVariable(CORRECTIONS_DEADLINE);
        }

        this.sendNotification(authorUsername, authorEmail, createScientificPaperDto.getTitle(), mainEditorUsername, correctionTime, afterReviewsCorrection);
    }

    public void sendNotification(final String username, final String email, final String title, final String mainEditorUsername, final Long correctionTime, final boolean afterReviewsCorrection)
            throws UnsupportedEncodingException, MessagingException {

        final String unit = (correctionTime > 1) ? " hours" : " hour";
        String message = "Hello " + username + ",<br><br>"
                + "You need to correct scientific paper titled <b>" + title + "</b> by uploading a new version of PDF document. ";

        if (afterReviewsCorrection)
            message += "Also, you have to give a reply about changes you have made. ";

        message += "You have " + correctionTime + unit + " for correction."
                + "<br><br>"
                + "Regards, <br><br>"
                + "<i>Main editor <b>" + mainEditorUsername + "</b></i>";

        this.emailService.sendEmailNotification(message, EMAIL_SUBJECT, username, email);
    }
}