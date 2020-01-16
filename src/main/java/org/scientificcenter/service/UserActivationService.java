package org.scientificcenter.service;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.jasypt.util.text.BasicTextEncryptor;
import org.scientificcenter.dto.AccountVerificationDto;
import org.scientificcenter.dto.ValidationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserActivationService implements JavaDelegate {

    private final UserServiceImpl userService;
    private final BasicTextEncryptor basicTextEncryptor;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private static final String USER_ACTIVATED = "user_activated";
    private static final String ACCOUNT_VERIFICATION_DTO = "accountVerificationDto";

    @Autowired
    public UserActivationService(final UserServiceImpl userService, final BasicTextEncryptor basicTextEncryptor, final SimpMessagingTemplate simpMessagingTemplate) {
        this.userService = userService;
        this.basicTextEncryptor = basicTextEncryptor;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public void execute(final DelegateExecution delegateExecution) {
        final AccountVerificationDto accountVerificationDto = (AccountVerificationDto) delegateExecution.getVariable(UserActivationService.ACCOUNT_VERIFICATION_DTO);
        final org.scientificcenter.model.User user = (org.scientificcenter.model.User) this.userService.loadUserByUsername(accountVerificationDto.getUsername());
        String decryptedActivationCode = null;
        try {
            decryptedActivationCode = this.basicTextEncryptor.decrypt(accountVerificationDto.getActivationCode());
        } catch (final Exception ignored) {
        }
        final boolean activateUserFlag = !user.isEnabled() && decryptedActivationCode != null && decryptedActivationCode.equals(accountVerificationDto.getUsername());
        UserActivationService.log.info("User is activated successfully: {}", activateUserFlag);
        delegateExecution.setVariable(UserActivationService.USER_ACTIVATED, activateUserFlag);

        UserActivationService.log.info("Sending notification about user activation through websocket");
        final ValidationDto validationDto = ValidationDto.builder()
                .valid(activateUserFlag)
                .errorMessage(!activateUserFlag
                        ? (decryptedActivationCode == null || !decryptedActivationCode.equals(accountVerificationDto.getUsername()))
                        ? "Your activation code is invalid!"
                        : "User with username '".concat(accountVerificationDto.getUsername()).concat("' is already verified!")
                        : null)
                .build();
        this.simpMessagingTemplate.convertAndSend("/registration/verification", validationDto);
    }
}