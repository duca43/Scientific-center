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
public class CreateScientificPaperEmailService implements JavaDelegate {

    private final EmailService emailService;
    private final UserService userService;
    private static final String EMAIL_SUBJECT = "New scientific paper";
    private static final String SCIENTIFIC_PAPER_DTO = "scientificPaperDto";

    @Autowired
    public CreateScientificPaperEmailService(final EmailService emailService, final UserService userService) {
        this.emailService = emailService;
        this.userService = userService;
    }

    @Override
    public void execute(final DelegateExecution delegateExecution) throws Exception {
        log.info("Executing service named 'Send email to main editor and author'");

        final String authorUsername = (String) delegateExecution.getVariable("author");
        final String authorEmail = this.userService.findByUsername(authorUsername).getEmail();
        final String mainEditorUsername = (String) delegateExecution.getVariable("main_editor");
        final String mainEditorEmail = (String) delegateExecution.getVariable("main_editor_email");

        final CreateScientificPaperDto createScientificPaperDto = (CreateScientificPaperDto) delegateExecution.getVariable(SCIENTIFIC_PAPER_DTO);
        log.info("Retrieved execution variable scientificPaperDto -> {}", createScientificPaperDto);

        this.sendNotification(authorUsername, authorEmail, createScientificPaperDto.getTitle());
        this.sendNotification(mainEditorUsername, mainEditorEmail, createScientificPaperDto.getTitle());
    }

    public void sendNotification(final String username, final String email, final String title) throws UnsupportedEncodingException, MessagingException {

        final String message = "Hello " + username + ",<br><br>"
                + "New scientific paper titled '" + title + "' is added to the system. <br><br>"
                + "Regards, <br><br>"
                + "<i>Scientific Center Team</i>";

        this.emailService.sendEmailNotification(message, EMAIL_SUBJECT, username, email);
    }
}