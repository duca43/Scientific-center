package org.scientificcenter.service;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.scientificcenter.dto.CreateScientificPaperDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

@Service
@Slf4j
public class PaperFinalDecisionEmailService implements JavaDelegate {

    private final EmailService emailService;
    private final UserService userService;
    private static final String SCIENTIFIC_PAPER_DTO = "scientificPaperDto";

    @Autowired
    public PaperFinalDecisionEmailService(final EmailService emailService, final UserService userService) {
        this.emailService = emailService;
        this.userService = userService;
    }

    @Override
    public void execute(final DelegateExecution delegateExecution) throws Exception {
        final String subjectForEmail = (String) delegateExecution.getVariable("subjectForEmail");
        final String decisionForEmail = (String) delegateExecution.getVariable("decisionForEmail");
        final Boolean decisionFlagForEmail = (Boolean) delegateExecution.getVariable("decisionFlagForEmail");

        if (decisionFlagForEmail) {
            log.info("Executing service named 'Paper acceptance notification'");
        } else {
            log.info("Executing service named 'Paper final rejection notification'");
        }


        final String authorUsername = (String) delegateExecution.getVariable("author");
        final String authorEmail = this.userService.findByUsername(authorUsername).getEmail();
        final String mainEditorUsername = (String) delegateExecution.getVariable("main_editor");

        final CreateScientificPaperDto createScientificPaperDto = (CreateScientificPaperDto) delegateExecution.getVariable(SCIENTIFIC_PAPER_DTO);
        log.info("Retrieved execution variable scientificPaperDto -> {}", createScientificPaperDto);

        this.sendNotification(authorUsername, authorEmail, createScientificPaperDto.getTitle(), mainEditorUsername, subjectForEmail, decisionForEmail);
    }

    public void sendNotification(final String username, final String email, final String title, final String mainEditorUsername, final String subject, final String decision)
            throws UnsupportedEncodingException, MessagingException {

        final String message = "Hello " + username + ",<br><br>"
                + "Final decision is that your scientific paper titled <b>" + title + "</b> is " + decision + "."
                + "<br><br>"
                + "Regards, <br><br>"
                + "<i>Main editor <b>" + mainEditorUsername + "</b></i>";

        this.emailService.sendEmailNotification(message, subject, username, email);
    }
}