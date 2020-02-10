package org.scientificcenter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String senderEmail;
    @Value("${mail.alias}")
    private String senderAlias;

    @Autowired
    public EmailService(final JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void sendEmailNotification(final String message, final String subject, final String username, final String email)
            throws MessagingException, UnsupportedEncodingException {

        log.info("Sending email to '{}' with email address: '{}'", username, email);

        final MimeMessage mail = this.javaMailSender.createMimeMessage();
        final MimeMessageHelper helper = new MimeMessageHelper(mail, false, "utf-8");
        helper.setTo(email);
        helper.setFrom(new InternetAddress(this.senderEmail, this.senderAlias));
        helper.setSubject(subject);
        helper.setText(message, true);
        this.javaMailSender.send(mail);

        log.info("Email is sent successfully to '{}' with email '{}'", username, email);
    }
}