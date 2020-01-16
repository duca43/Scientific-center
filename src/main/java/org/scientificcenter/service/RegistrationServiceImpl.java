package org.scientificcenter.service;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.scientificcenter.dto.*;
import org.scientificcenter.exception.TaskNotFoundException;
import org.scientificcenter.exception.UnauthorizedTaskAccessException;
import org.scientificcenter.exception.UserAlreadyExistsException;
import org.scientificcenter.exception.UserNotFoundException;
import org.scientificcenter.security.TokenUtils;
import org.scientificcenter.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class RegistrationServiceImpl implements RegistrationService, JavaDelegate {

    private final IdentityService identityService;

    private final RuntimeService runtimeService;

    private final TaskService taskService;

    private final FormService formService;

    private final TokenUtils tokenUtils;

    private final UserServiceImpl userService;

    private final AuthorityService authorityService;

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final Util util;

    private static final String PROCESS_KEY = "User_registration";
    private static final String GUEST_USER = "anonymous";
    private static final String REGISTRATION_FORM_TASK = "Registration_form_task";
    private static final String CONFIRM_REGISTRATION_TASK = "Confirm_registration_task";
    private static final String CHECK_REVIEWER_TASK = "Check_reviewer_task";
    private static final String ACCOUNT_VERIFICATION_DTO = "accountVerificationDto";
    private static final String ACCEPT_REVIEWER_FLAG = "acceptReviewer";
    private static final String ROLE_ADMINISTRATOR = "ROLE_ADMINISTRATOR";

    @Autowired
    public RegistrationServiceImpl(final IdentityService identityService, final RuntimeService runtimeService, final TaskService taskService, final FormService formService, final TokenUtils tokenUtils, final UserServiceImpl userService, final AuthorityService authorityService, final SimpMessagingTemplate simpMessagingTemplate, final Util util) {
        this.identityService = identityService;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.formService = formService;
        this.tokenUtils = tokenUtils;
        this.userService = userService;
        this.authorityService = authorityService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.util = util;
    }

    @Override
    public FormFieldsDto beginRegistrationProcess() {
        this.identityService.setAuthenticatedUserId(RegistrationServiceImpl.GUEST_USER);
        final ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey(RegistrationServiceImpl.PROCESS_KEY);
        RegistrationServiceImpl.log.info("Process instance with id '{}' is created.", processInstance.getId());

        final Task task = this.util.getActiveUserTaskByDefinitionKey(processInstance.getId(), RegistrationServiceImpl.REGISTRATION_FORM_TASK);
        RegistrationServiceImpl.log.info("Registration form task is created. Assignee: '{}'.", task.getAssignee());

        final List<FormField> formFields = this.formService.getTaskFormData(task.getId()).getFormFields();

        final String token = this.tokenUtils.generateToken(RegistrationServiceImpl.GUEST_USER);

        return FormFieldsDto.builder()
                .processInstanceId(processInstance.getId())
                .taskId(task.getId())
                .formFields(formFields)
                .token(token)
                .build();
    }

    @Override
    public void registerUser(final RegistrationUserDto registrationUserDto, final String processInstanceId) throws UserAlreadyExistsException {
        Assert.notNull(registrationUserDto, "Registration user object can't be null!");
        Assert.notNull(processInstanceId, "Process instance id can't be null!");

        RegistrationServiceImpl.log.info("Registration of user with username {}", registrationUserDto.getUsername());
        final Task task = this.util.getActiveUserTaskByDefinitionKey(processInstanceId, RegistrationServiceImpl.REGISTRATION_FORM_TASK);
        RegistrationServiceImpl.log.info("Retrieved registration form task");

        this.runtimeService.setVariable(processInstanceId, "registrationUserDto", registrationUserDto);
        RegistrationServiceImpl.log.info("Set execution variable 'registrationUserDto' -> {}", registrationUserDto);

        final Map<String, Object> registrationFormFields = this.util.transformObjectToMap(registrationUserDto);
        this.formService.submitTaskForm(task.getId(), registrationFormFields);
        RegistrationServiceImpl.log.info("Submitted registration form with following fields: {}", registrationFormFields);
    }

    @Override
    public FormFieldsDto getConfirmRegistrationFormFields(final String processInstanceId) {
        final Task task = this.util.getActiveUserTaskByDefinitionKey(processInstanceId, RegistrationServiceImpl.CONFIRM_REGISTRATION_TASK);
        if (task == null) throw new TaskNotFoundException(RegistrationServiceImpl.CONFIRM_REGISTRATION_TASK);
        final List<FormField> formFields = this.formService.getTaskFormData(task.getId()).getFormFields();
        final String token = this.tokenUtils.generateToken(RegistrationServiceImpl.GUEST_USER);
        return FormFieldsDto.builder()
                .processInstanceId(processInstanceId)
                .taskId(task.getId())
                .formFields(formFields)
                .token(token)
                .build();
    }

    @Override
    public void confirmRegistration(final AccountVerificationDto accountVerificationDto, final String processInstanceId) {
        Assert.notNull(accountVerificationDto, "Account verification object can't be null!");
        Assert.notNull(processInstanceId, "Process instance id can't be null!");
        Assert.noNullElements(Stream.of(accountVerificationDto.getActivationCode(), accountVerificationDto.getUsername()).toArray(),
                "Both activation code and username must be set!");
        RegistrationServiceImpl.log.info("Account verification for user with username '{}' begins", accountVerificationDto.getUsername());

        final Task task = this.util.getActiveUserTaskByDefinitionKey(processInstanceId, RegistrationServiceImpl.CONFIRM_REGISTRATION_TASK);
        RegistrationServiceImpl.log.info("Retrieved confirm registration task");

        this.runtimeService.setVariable(processInstanceId, RegistrationServiceImpl.ACCOUNT_VERIFICATION_DTO, accountVerificationDto);
        RegistrationServiceImpl.log.info("Set execution variable 'accountVerificationDto' -> {}", accountVerificationDto);

        final Map<String, Object> confirmRegistrationFields = this.util.transformObjectToMap(accountVerificationDto);
        this.formService.submitTaskForm(task.getId(), confirmRegistrationFields);
        RegistrationServiceImpl.log.info("Submitted confirm registration form with following fields: {}", confirmRegistrationFields);

        RegistrationServiceImpl.log.info("Account verification for user with username '{}' is completed", accountVerificationDto.getUsername());
    }

    @Override
    public List<TaskDto> getActiveCheckReviewerTasks() {
        RegistrationServiceImpl.log.info("Retrieving all active and unassigned 'check reviewer' tasks");
        return this.taskService.createTaskQuery()
                .taskDefinitionKey(RegistrationServiceImpl.CHECK_REVIEWER_TASK)
                .active()
                .taskUnassigned()
                .list()
                .stream()
                .map(TaskDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public FormFieldsDto getCheckReviewerFormFields(final String taskId) {
        return this.util.getTaskFormFields(taskId);
    }

    @Override
    public void checkReviewer(final CheckReviewerDto checkReviewerDto) {
        Assert.notNull(checkReviewerDto, "Check reviewer object can't be null!");
        Assert.notNull(checkReviewerDto.getTaskId(), "Task id can't be null!");

        RegistrationServiceImpl.log.info("Check reviewer for task with id '{}'", checkReviewerDto.getTaskId());
        final Task task = this.taskService.createTaskQuery().taskId(checkReviewerDto.getTaskId()).singleResult();

        final org.scientificcenter.model.User admin = this.userService.findByUsername(checkReviewerDto.getAdmin());

        if (admin == null)
            throw new UserNotFoundException(checkReviewerDto.getAdmin());

        if (!admin.getAuthorities().contains(this.authorityService.findByName(RegistrationServiceImpl.ROLE_ADMINISTRATOR)))
            throw new UnauthorizedTaskAccessException(checkReviewerDto.getAdmin(), checkReviewerDto.getTaskId());

        this.taskService.setAssignee(task.getId(), checkReviewerDto.getAdmin());

        this.runtimeService.setVariable(task.getProcessInstanceId(), RegistrationServiceImpl.ACCEPT_REVIEWER_FLAG, checkReviewerDto.isAcceptReviewer());
        RegistrationServiceImpl.log.info("Set execution variable 'acceptReviewer' -> {}", checkReviewerDto.isAcceptReviewer());

        final HashMap<String, Object> formFields = new HashMap<>();
        formFields.put(RegistrationServiceImpl.ACCEPT_REVIEWER_FLAG, checkReviewerDto.isAcceptReviewer());
        this.formService.submitTaskForm(checkReviewerDto.getTaskId(), formFields);
        RegistrationServiceImpl.log.info("Submitted check reviewer form with following fields: {}", formFields);
    }

    @Override
    public void execute(final DelegateExecution delegateExecution) {
        RegistrationServiceImpl.log.info("Executing save user task");

        final RegistrationUserDto registrationUserDto = (RegistrationUserDto) delegateExecution.getVariable("registrationUserDto");
        RegistrationServiceImpl.log.info("Retrieved execution variable 'registrationUserDto' -> {}", registrationUserDto);

        final User user = this.createUser(registrationUserDto);
        RegistrationServiceImpl.log.info("Camunda user with username '{}' is created", registrationUserDto.getUsername());

        this.identityService.saveUser(user);
        RegistrationServiceImpl.log.info("Camunda user with username '{}' is saved", registrationUserDto.getUsername());

        final org.scientificcenter.model.User userEntity = this.userService.save(registrationUserDto);
        RegistrationServiceImpl.log.info("User entity with username '{}' is saved", registrationUserDto.getUsername());

        delegateExecution.setVariable("user_enabled", false);

        this.simpMessagingTemplate.convertAndSend("/registration/new", userEntity);
    }

    private User createUser(final RegistrationUserDto registrationUserDto) {
        final User user = this.identityService.newUser(registrationUserDto.getUsername());
        user.setPassword(registrationUserDto.getPassword());
        user.setEmail(registrationUserDto.getEmail());
        user.setFirstName(registrationUserDto.getFirstname());
        user.setLastName(registrationUserDto.getLastname());
        return user;
    }
}