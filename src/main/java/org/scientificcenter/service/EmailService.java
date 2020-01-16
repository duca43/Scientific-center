package org.scientificcenter.service;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
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
public class EmailService implements JavaDelegate {

    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String senderEmail;
    @Value("${mail.alias}")
    private String senderAlias;
    @Value("${frontend.port}")
    private String FRONTEND_PORT;
    @Value("${frontend.address}")
    private String FRONTEND_ADDRESS;
    @Value("${scientific-center.expires-in}")
    private int TOKEN_EXPIRES_IN;
    private static final String HTTP_PREFIX = "http://";
    private final BasicTextEncryptor basicTextEncryptor;

    @Autowired
    public EmailService(final JavaMailSender javaMailSender, final BasicTextEncryptor basicTextEncryptor) {
        this.javaMailSender = javaMailSender;
        this.basicTextEncryptor = basicTextEncryptor;
    }

    @Override
    public void execute(final DelegateExecution delegateExecution) throws Exception {
        final String username = (String) delegateExecution.getVariable("username");
        final String email = (String) delegateExecution.getVariable("email");
        this.sendRegistrationNotification(username, email);
        delegateExecution.setVariable("verification_time", this.TOKEN_EXPIRES_IN);
        EmailService.log.info("Verification time: {} seconds", this.TOKEN_EXPIRES_IN);
    }

    @Async
    public void sendRegistrationNotification(final String username, final String email) throws MailException, MessagingException {
        EmailService.log.info("Sending email to '{}' with email address: '{}'", username, email);

        final MimeMessage mail = this.javaMailSender.createMimeMessage();
        final MimeMessageHelper helper = new MimeMessageHelper(mail, false, "utf-8");
        try {
            helper.setTo(email);
            helper.setFrom(new InternetAddress(this.senderEmail, this.senderAlias));
            helper.setSubject("Scientific Center - Account activation");
            final String activationCode = this.basicTextEncryptor.encrypt(username);
            EmailService.log.info("Created activation code: {}", activationCode);
            final String message = "Hello " + username + ",<br><br>"
                    + "Thank you for registering your Scientific center account. To finally activate your account please "
                    + "<a href=\"" + this.makeActivationLink(username) + "\">click here.</a><br><br>"
                    + "<p>Use this code to activate your account: "
                    + "<b>"
                    + activationCode
                    + "</b>"
                    + "</p><br>"
                    + "Regards, <br><br>"
                    + "<i>Scientific Center Team</i>";
            helper.setText(message, true);
            this.javaMailSender.send(mail);
        } catch (final MessagingException | UnsupportedEncodingException e) {
            throw new MessagingException();
        }

        EmailService.log.info("Email is sent successfully to '{}' with email '{}'", username, email);
    }

    private String makeActivationLink(final String username) {
        return EmailService.HTTP_PREFIX.concat(this.FRONTEND_ADDRESS).concat(":").concat(this.FRONTEND_PORT).concat("/verify_account?user=").concat(username);
    }
}