package org.scientificcenter.service;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.scientificcenter.dto.AccountVerificationDto;
import org.scientificcenter.model.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UpdateUserRoleService implements JavaDelegate {

    private final IdentityService identityService;
    private final UserServiceImpl userService;
    private final AuthorityService authorityService;
    private static final String REVIEWERS_GROUP_ID = "reviewers";
    private static final String USERS_GROUP_ID = "users";
    private static final String REVIEWER_FLAG = "reviewer";
    private static final String ACCEPT_REVIEWER_FLAG = "acceptReviewer";
    private static final String ACCOUNT_VERIFICATION_DTO = "accountVerificationDto";
    private static final String ROLE_REVIEWER = "ROLE_REVIEWER";
    private static final String ROLE_USER = "ROLE_USER";

    @Autowired
    public UpdateUserRoleService(final IdentityService identityService, final UserServiceImpl userService, final AuthorityService authorityService) {
        this.identityService = identityService;
        this.userService = userService;
        this.authorityService = authorityService;
    }

    @Override
    public void execute(final DelegateExecution delegateExecution) {
        final AccountVerificationDto accountVerificationDto = (AccountVerificationDto) delegateExecution.getVariable(UpdateUserRoleService.ACCOUNT_VERIFICATION_DTO);
        final org.scientificcenter.model.User user = (org.scientificcenter.model.User) this.userService.loadUserByUsername(accountVerificationDto.getUsername());

        String groupId = UpdateUserRoleService.USERS_GROUP_ID;
        boolean reviewer = false;
        final Boolean reviewerFlag = (Boolean) delegateExecution.getVariable(UpdateUserRoleService.REVIEWER_FLAG);
        if (reviewerFlag != null && reviewerFlag) {
            final Boolean acceptReviewerFlag = (Boolean) delegateExecution.getVariable(UpdateUserRoleService.ACCEPT_REVIEWER_FLAG);
            if (acceptReviewerFlag != null && acceptReviewerFlag) {
                groupId = UpdateUserRoleService.REVIEWERS_GROUP_ID;
                reviewer = true;
            }
        }

        this.identityService.createMembership(accountVerificationDto.getUsername(), groupId);
        UpdateUserRoleService.log.info("Camunda user with username '{}' is added to group '{}", accountVerificationDto.getUsername(), groupId);

        final Set<Authority> authorities = new HashSet<>();
        authorities.add(this.authorityService.findByName(UpdateUserRoleService.ROLE_USER));

        if (reviewer) {
            authorities.add(this.authorityService.findByName(UpdateUserRoleService.ROLE_REVIEWER));
        }

        user.setAuthorities(authorities);
        UpdateUserRoleService.log.info("User entity with username: {} has gained following authorities: {}",
                accountVerificationDto.getUsername(),
                authorities.stream().map(Authority::getAuthority).collect(Collectors.toList()));
    }
}