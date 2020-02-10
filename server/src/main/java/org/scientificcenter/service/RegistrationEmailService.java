package org.scientificcenter.service;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

@Service
@Slf4j
public class RegistrationEmailService implements JavaDelegate {

    @Value("${frontend.port}")
    private String FRONTEND_PORT;
    @Value("${frontend.address}")
    private String FRONTEND_ADDRESS;
    @Value("${scientific-center.expires-in}")
    private int TOKEN_EXPIRES_IN;
    private static final String HTTP_PREFIX = "http://";
    private final EmailService emailService;
    private final BasicTextEncryptor basicTextEncryptor;
    private static final String EMAIL_SUBJECT = "Scientific Center - Account activation";

    @Autowired
    public RegistrationEmailService(final EmailService emailService, final BasicTextEncryptor basicTextEncryptor) {
        this.emailService = emailService;
        this.basicTextEncryptor = basicTextEncryptor;
    }

    @Override
    public void execute(final DelegateExecution delegateExecution) throws Exception {
        log.info("Executing service named 'Send email to user'");
        final String username = (String) delegateExecution.getVariable("username");
        final String email = (String) delegateExecution.getVariable("email");
        this.sendRegistrationNotification(username, email);
        delegateExecution.setVariable("verification_time", this.TOKEN_EXPIRES_IN);
        RegistrationEmailService.log.info("Verification time: {} seconds", this.TOKEN_EXPIRES_IN);
    }

    public void sendRegistrationNotification(final String username, final String email) throws UnsupportedEncodingException, MessagingException {
        final String activationCode = this.basicTextEncryptor.encrypt(username);
        log.info("Created activation code: {}", activationCode);

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

        this.emailService.sendEmailNotification(message, EMAIL_SUBJECT, username, email);
    }

    private String makeActivationLink(final String username) {
        return RegistrationEmailService.HTTP_PREFIX.concat(this.FRONTEND_ADDRESS).concat(":").concat(this.FRONTEND_PORT).concat("/verify_account?user=").concat(username);
    }
}