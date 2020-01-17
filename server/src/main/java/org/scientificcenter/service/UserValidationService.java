package org.scientificcenter.service;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.identity.User;
import org.scientificcenter.dto.ValidationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserValidationService implements JavaDelegate {

    private final IdentityService identityService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public UserValidationService(final IdentityService identityService, final SimpMessagingTemplate simpMessagingTemplate) {
        this.identityService = identityService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public void execute(final DelegateExecution delegateExecution) {
        final User userByUsername = this.identityService.createUserQuery().userId((String) delegateExecution.getVariable("username")).singleResult();
//        final User userByEmail = this.identityService.createUserQuery().userEmail((String) delegateExecution.getVariable("email")).singleResult();
        final boolean userAlreadyExistsFlag = userByUsername != null;
        delegateExecution.setVariable("user_exists", userAlreadyExistsFlag);
        UserValidationService.log.info("User with username '{}' already exists: {}", userByUsername, userAlreadyExistsFlag);

        UserValidationService.log.info("Sending notification about user validation through websocket");
        final ValidationDto validationDto = ValidationDto.builder()
                .valid(!userAlreadyExistsFlag)
                .errorMessage(userAlreadyExistsFlag ? "User with given username and/or given email already exists!" : null)
                .build();
        this.simpMessagingTemplate.convertAndSend("/registration/validation", validationDto);
    }
}