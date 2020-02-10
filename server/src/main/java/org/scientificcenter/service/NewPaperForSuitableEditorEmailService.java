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
public class NewPaperForSuitableEditorEmailService implements JavaDelegate {

    private final EmailService emailService;
    private final UserService userService;
    private static final String EMAIL_SUBJECT = "New scientific paper assignment";
    private static final String SCIENTIFIC_PAPER_DTO = "scientificPaperDto";
    private static final String CHOSEN_EDITOR = "chosenEditor";

    @Autowired
    public NewPaperForSuitableEditorEmailService(final EmailService emailService, final UserService userService) {
        this.emailService = emailService;
        this.userService = userService;
    }

    @Override
    public void execute(final DelegateExecution delegateExecution) throws Exception {
        log.info("Executing service named 'New paper for suitable editor notification'");

        final String chosenEditor = (String) delegateExecution.getVariable(CHOSEN_EDITOR);
        final String chosenEditorEmail = this.userService.findByUsername(chosenEditor).getEmail();
        final String mainEditorUsername = (String) delegateExecution.getVariable("main_editor");

        final CreateScientificPaperDto createScientificPaperDto = (CreateScientificPaperDto) delegateExecution.getVariable(SCIENTIFIC_PAPER_DTO);
        log.info("Retrieved execution variable scientificPaperDto -> {}", createScientificPaperDto);

        this.sendNotification(chosenEditor, chosenEditorEmail, createScientificPaperDto.getTitle(), mainEditorUsername);
    }

    public void sendNotification(final String username, final String email, final String title, final String mainEditorUsername) throws UnsupportedEncodingException, MessagingException {

        final String message = "Hello " + username + ",<br><br>"
                + "You are assigned as an editor for a scientific paper titled <b>" + title + "</b>."
                + "<br><br>"
                + "Regards, <br><br>"
                + "<i>Main editor <b>" + mainEditorUsername + "</b></i>";

        this.emailService.sendEmailNotification(message, EMAIL_SUBJECT, username, email);
    }
}