package org.scientificcenter.service;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.identity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserValidationService implements JavaDelegate {

    private final IdentityService identityService;

    @Autowired
    public UserValidationService(final IdentityService identityService) {
        this.identityService = identityService;
    }

    @Override
    public void execute(final DelegateExecution delegateExecution) {
        final User userByUsername = this.identityService.createUserQuery().userId((String) delegateExecution.getVariable("username")).singleResult();
//        final User userByEmail = this.identityService.createUserQuery().userEmail((String) delegateExecution.getVariable("email")).singleResult();
        final boolean userAlreadyExistsFlag = userByUsername != null;
        delegateExecution.setVariable("user_exists", userAlreadyExistsFlag);
        UserValidationService.log.info("User already exists: {}", userAlreadyExistsFlag);
    }
}