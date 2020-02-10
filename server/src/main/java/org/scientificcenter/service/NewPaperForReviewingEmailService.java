package org.scientificcenter.service;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.scientificcenter.dto.CreateScientificPaperDto;
import org.scientificcenter.dto.PaperReviewByReviewerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class NewPaperForReviewingEmailService implements JavaDelegate {

    private final EmailService emailService;
    private final UserService userService;
    private static final String EMAIL_SUBJECT = "New scientific paper assignment";
    private static final String SCIENTIFIC_PAPER_DTO = "scientificPaperDto";
    private static final String CHOSEN_EDITOR = "chosenEditor";
    private static final String REVIEWERS_FIELD_ID = "reviewers";
    private final static String REVIEWS = "reviews";

    @Autowired
    public NewPaperForReviewingEmailService(final EmailService emailService, final UserService userService) {
        this.emailService = emailService;
        this.userService = userService;
    }

    @Override
    public void execute(final DelegateExecution delegateExecution) throws UnsupportedEncodingException, MessagingException {
        log.info("Executing service named 'New paper for reviewing notification'");

        final String chosenEditor = (String) delegateExecution.getVariable(CHOSEN_EDITOR);

        final CreateScientificPaperDto createScientificPaperDto = (CreateScientificPaperDto) delegateExecution.getVariable(SCIENTIFIC_PAPER_DTO);
        log.info("Retrieved execution variable scientificPaperDto -> {}", createScientificPaperDto);

        final List<String> reviewers = (List<String>) delegateExecution.getVariable(REVIEWERS_FIELD_ID);
        log.info("Retrieved execution variable reviewers -> {}", reviewers);

        final Long reviewsDeadline = (Long) delegateExecution.getVariable("reviewsDeadline");

        for (final String reviewer : reviewers) {
            final String email = this.userService.findByUsername(reviewer).getEmail();
            this.sendNotification(reviewer, email, createScientificPaperDto.getTitle(), chosenEditor, reviewsDeadline);
        }

        delegateExecution.setVariable(REVIEWS, new ArrayList<PaperReviewByReviewerDto>());
    }

    public void sendNotification(final String username, final String email, final String title, final String chosenEditorUsername, final Long reviewsDeadline) throws UnsupportedEncodingException, MessagingException {

        final String unit = (reviewsDeadline > 1) ? " hours" : " hour";
        final String message = "Hello " + username + ",<br><br>"
                + "You are assigned as an reviewer for a scientific paper titled <b>" + title + "</b>. "
                + "You have " + reviewsDeadline + unit + " to complete review."
                + "<br><br>"
                + "Regards, <br><br>"
                + "<i>Editor <b>" + chosenEditorUsername + "</b></i>";

        this.emailService.sendEmailNotification(message, EMAIL_SUBJECT, username, email);
    }
}