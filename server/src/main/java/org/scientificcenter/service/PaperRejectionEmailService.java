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
public class PaperRejectionEmailService implements JavaDelegate {

    private final EmailService emailService;
    private final UserService userService;
    private final MagazineService magazineService;
    private static final String EMAIL_SUBJECT = "Rejected scientific paper";
    private static final String SCIENTIFIC_PAPER_DTO = "scientificPaperDto";
    private final static String MAGAZINE_FIELD_ID = "magazine";

    @Autowired
    public PaperRejectionEmailService(final EmailService emailService, final UserService userService, final MagazineService magazineService) {
        this.emailService = emailService;
        this.userService = userService;
        this.magazineService = magazineService;
    }

    @Override
    public void execute(final DelegateExecution delegateExecution) throws Exception {
        log.info("Executing service named 'Paper rejection notification'");

        final String authorUsername = (String) delegateExecution.getVariable("author");
        final String authorEmail = this.userService.findByUsername(authorUsername).getEmail();
        final String mainEditorUsername = (String) delegateExecution.getVariable("main_editor");

        final CreateScientificPaperDto createScientificPaperDto = (CreateScientificPaperDto) delegateExecution.getVariable(SCIENTIFIC_PAPER_DTO);
        log.info("Retrieved execution variable scientificPaperDto -> {}", createScientificPaperDto);

        final String magazineIdString = (String) delegateExecution.getVariable(MAGAZINE_FIELD_ID);
        final Long magazineId = Long.valueOf(magazineIdString);
        final String magazineName = this.magazineService.findById(magazineId).getName();

        this.sendNotification(authorUsername, authorEmail, createScientificPaperDto.getTitle(), magazineName, mainEditorUsername);
    }

    public void sendNotification(final String username, final String email, final String title, final String magazineName, final String mainEditorUsername) throws UnsupportedEncodingException, MessagingException {

        final String message = "Hello " + username + ",<br><br>"
                + "Your scientific paper titled <b>" + title + "</b> is rejected because it is not thematically suitable for magazine named <b>" + magazineName + "</b>."
                + "<br><br>"
                + "Regards, <br><br>"
                + "<i>Main editor <b>" + mainEditorUsername + "</b></i>";

        this.emailService.sendEmailNotification(message, EMAIL_SUBJECT, username, email);
    }
}