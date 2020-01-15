package org.scientificcenter.service;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.scientificcenter.dto.AccountVerificationDto;
import org.scientificcenter.security.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserActivationService implements JavaDelegate {

    private final UserServiceImpl userService;
    private final TokenUtils tokenUtils;
    private static final String USER_ACTIVATED = "user_activated";
    private static final String ACCOUNT_VERIFICATION_DTO = "accountVerificationDto";

    @Autowired
    public UserActivationService(final UserServiceImpl userService, final TokenUtils tokenUtils) {
        this.userService = userService;
        this.tokenUtils = tokenUtils;
    }

    @Override
    public void execute(final DelegateExecution delegateExecution) {
        final AccountVerificationDto accountVerificationDto = (AccountVerificationDto) delegateExecution.getVariable(UserActivationService.ACCOUNT_VERIFICATION_DTO);
        final org.scientificcenter.model.User user = (org.scientificcenter.model.User) this.userService.loadUserByUsername(accountVerificationDto.getUsername());
        final boolean activateUserFlag = !user.isEnabled() && this.tokenUtils.validateToken(accountVerificationDto.getActivationCode(), user);
        UserActivationService.log.info("User is activated successfully: {}", activateUserFlag);
        delegateExecution.setVariable(UserActivationService.USER_ACTIVATED, activateUserFlag);
    }
}