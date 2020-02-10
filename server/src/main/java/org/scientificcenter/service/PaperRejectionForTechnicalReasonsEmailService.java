package org.scientificcenter.service;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.scientificcenter.dto.CreateScientificPaperDto;
import org.scientificcenter.dto.ReviewPaperResponseDto;
import org.scientificcenter.model.ScientificPaper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

@Service
@Slf4j
public class PaperRejectionForTechnicalReasonsEmailService implements JavaDelegate {

    private final EmailService emailService;
    private final UserService userService;
    private final ScientificPaperService scientificPaperService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private static final String EMAIL_SUBJECT = "Rejected scientific paper";
    private static final String SCIENTIFIC_PAPER_DTO = "scientificPaperDto";

    @Autowired
    public PaperRejectionForTechnicalReasonsEmailService(final EmailService emailService, final UserService userService, final ScientificPaperService scientificPaperService, final SimpMessagingTemplate simpMessagingTemplate) {
        this.emailService = emailService;
        this.userService = userService;
        this.scientificPaperService = scientificPaperService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public void execute(final DelegateExecution delegateExecution) throws Exception {
        log.info("Executing service named 'Paper rejection for technical reasons notification'");

        final String mainEditorUsername = (String) delegateExecution.getVariable("main_editor");
        final String authorUsername = (String) delegateExecution.getVariable("author");
        final String authorEmail = this.userService.findByUsername(authorUsername).getEmail();

        final CreateScientificPaperDto createScientificPaperDto = (CreateScientificPaperDto) delegateExecution.getVariable(SCIENTIFIC_PAPER_DTO);
        log.info("Retrieved execution variable scientificPaperDto -> {}", createScientificPaperDto);

        log.info("Updating scientific paper with id '{}': approved by main editor flag -> false", createScientificPaperDto.getId());

        final ScientificPaper scientificPaper = this.scientificPaperService.findById(createScientificPaperDto.getId());

        scientificPaper.setApprovedByMainEditor(false);
        scientificPaper.setRequestedChanges(false);
        this.scientificPaperService.save(scientificPaper);

        final ReviewPaperResponseDto reviewPaperResponseDto = ReviewPaperResponseDto.builder()
                .scientificPaperId(scientificPaper.getId())
                .approved(false)
                .message("Scientific paper titled '" + scientificPaper.getTitle() + "' is rejected because of technical reasons.")
                .build();

        this.simpMessagingTemplate.convertAndSend("/scientific_paper/review_paper", reviewPaperResponseDto);

        this.sendNotification(authorUsername, authorEmail, createScientificPaperDto.getTitle(), mainEditorUsername);
    }

    public void sendNotification(final String username, final String email, final String title, final String mainEditorUsername) throws UnsupportedEncodingException, MessagingException {

        final String message = "Hello " + username + ",<br><br>"
                + "Your scientific paper titled <b>" + title + "</b> is rejected because of technical reasons."
                + "<br><br>"
                + "Regards, <br><br>"
                + "<i>Main editor <b>" + mainEditorUsername + "</b></i>";

        this.emailService.sendEmailNotification(message, EMAIL_SUBJECT, username, email);
    }
}