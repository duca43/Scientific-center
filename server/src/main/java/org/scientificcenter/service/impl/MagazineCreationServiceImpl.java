package org.scientificcenter.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.impl.persistence.entity.VariableInstanceEntity;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.VariableInstance;
import org.camunda.bpm.engine.task.Task;
import org.modelmapper.ModelMapper;
import org.scientificcenter.dto.*;
import org.scientificcenter.exception.*;
import org.scientificcenter.model.Magazine;
import org.scientificcenter.model.PaymentType;
import org.scientificcenter.model.User;
import org.scientificcenter.service.AuthorityService;
import org.scientificcenter.service.MagazineCreationService;
import org.scientificcenter.service.MagazineService;
import org.scientificcenter.service.UserService;
import org.scientificcenter.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.SerializationUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class MagazineCreationServiceImpl implements MagazineCreationService, JavaDelegate {

    private final IdentityService identityService;

    private final RuntimeService runtimeService;

    private final TaskService taskService;

    private final FormService formService;

    private final UserService userService;

    private final AuthorityService authorityService;

    private final MagazineService magazineService;

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final Util util;

    private final ModelMapper modelMapper = new ModelMapper();

    private static final String PROCESS_KEY = "Magazine_creation";
    private static final String ROLE_EDITOR = "ROLE_EDITOR";
    private static final String ROLE_ADMINISTRATOR = "ROLE_ADMINISTRATOR";
    private static final String MAGAZINE_FORM_TASK = "Magazine_form_task";
    private static final String CHOOSE_EDITORS_AND_REVIEWERS_TASK = "Choose_editors_and_reviewers_task";
    private static final String MAGAZINE_SAVED = "magazine_saved";
    private static final String MAGAZINE_DTO = "magazineDto";
    private static final String USER_INITIATOR = "user_initiator";
    private static final String EDITORS_AND_REVIEWERS_DTO = "editorsAndReviewersDto";
    private static final String CHECK_MAGAZINE_DATA_TASK = "Check_magazine_data_task";
    private static final String MAGAZINE_ACTIVATED = "magazineActivated";
    private static final String MAGAZINE_ISSN = "magazine_issn";
    private final static String EDITORS = "editors";
    private final static String REVIEWERS = "reviewers";
    private static final String ROLE_REVIEWER = "ROLE_REVIEWER";

    @Autowired
    public MagazineCreationServiceImpl(final IdentityService identityService, final RuntimeService runtimeService, final TaskService taskService, final FormService formService, final UserService userService, final AuthorityService authorityService, final MagazineService magazineService, final Util util, final SimpMessagingTemplate simpMessagingTemplate) {
        this.identityService = identityService;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.formService = formService;
        this.userService = userService;
        this.authorityService = authorityService;
        this.magazineService = magazineService;
        this.util = util;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public FormFieldsDto beginMagazineCreationProcess(final String editor) {
        final User userEditor = this.userService.findByUsername(editor);
        if (userEditor == null || !userEditor.getAuthorities().contains(this.authorityService.findByName(MagazineCreationServiceImpl.ROLE_EDITOR))) {
            throw new InvalidProcessInitiatorException("editor");
        }

        this.identityService.setAuthenticatedUserId(editor);
        final ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey(MagazineCreationServiceImpl.PROCESS_KEY);
        MagazineCreationServiceImpl.log.info("Process instance with id '{}' is created.", processInstance.getId());

        final Task task = this.util.getActiveUserTaskByDefinitionKey(processInstance.getId(), MagazineCreationServiceImpl.MAGAZINE_FORM_TASK);

        if (task == null) throw new TaskNotFoundException(MagazineCreationServiceImpl.MAGAZINE_FORM_TASK);

        MagazineCreationServiceImpl.log.info("Magazine creation task is created. Assignee: '{}'.", task.getAssignee());

        final List<FormField> formFields = this.formService.getTaskFormData(task.getId()).getFormFields();

        return FormFieldsDto.builder()
                .processInstanceId(processInstance.getId())
                .taskId(task.getId())
                .formFields(formFields)
                .build();
    }

    @Override
    public void createMagazine(final MagazineDto magazineDto, final String processInstanceId) {
        Assert.notNull(magazineDto, "Magazine object can't be null!");
        Assert.notNull(processInstanceId, "Process instance id can't be null!");
        Assert.noNullElements(Stream.of(magazineDto.getIssn(),
                magazineDto.getName(),
                magazineDto.getPayment(),
                magazineDto.getScientificAreas())
                        .toArray(),
                "One or more fields are null!");

        MagazineCreationServiceImpl.log.info("Creating magazine with issn '{}' and name '{}'", magazineDto.getIssn(), magazineDto.getName());

        final Task task = this.util.getActiveUserTaskByDefinitionKey(processInstanceId, MagazineCreationServiceImpl.MAGAZINE_FORM_TASK);
        MagazineCreationServiceImpl.log.info("Retrieved fill magazine form task");

        if (task == null) throw new TaskNotFoundException(MagazineCreationServiceImpl.MAGAZINE_FORM_TASK);

        this.runtimeService.setVariable(task.getProcessInstanceId(), MagazineCreationServiceImpl.MAGAZINE_DTO, magazineDto);
        MagazineCreationServiceImpl.log.info("Set execution variable 'magazineDto' -> {}", magazineDto);

        final Map<String, Object> magazineCreationFormFields = this.util.transformObjectToMap(magazineDto);
        this.formService.submitTaskForm(task.getId(), magazineCreationFormFields);
        MagazineCreationServiceImpl.log.info("Submitted magazine creation form with following fields: {}", magazineCreationFormFields);
    }

    @Override
    public FormFieldsDto getChooseEditorsAndReviewersFormFields(final String issn, final String editor) {
        final String processInstanceId = this.getProcessInstanceId(issn, editor);

        final Task task = this.util.getActiveUserTaskByDefinitionKey(processInstanceId, MagazineCreationServiceImpl.CHOOSE_EDITORS_AND_REVIEWERS_TASK);

        if (task == null)
            throw new TaskNotFoundException(MagazineCreationServiceImpl.CHOOSE_EDITORS_AND_REVIEWERS_TASK);

        if (!task.getAssignee().equals(editor))
            throw new UnauthorizedTaskAccessException(editor, task.getId());

        MagazineCreationServiceImpl.log.info("Retrieved choose editors and reviewers task");

        final List<FormField> formFields = this.formService.getTaskFormData(task.getId()).getFormFields();

        this.util.fillMultipleEnumFormFields(this.magazineService.findByIssn(issn),
                formFields,
                UserEnumFormFieldDto.builder()
                        .id(MagazineCreationServiceImpl.EDITORS)
                        .role(MagazineCreationServiceImpl.ROLE_EDITOR)
                        .build(),
                UserEnumFormFieldDto.builder()
                        .id(MagazineCreationServiceImpl.REVIEWERS)
                        .role(MagazineCreationServiceImpl.ROLE_REVIEWER)
                        .build());

        return FormFieldsDto.builder()
                .processInstanceId(processInstanceId)
                .taskId(task.getId())
                .formFields(formFields)
                .build();
    }

    @Override
    public void chooseEditorsAndReviewers(final EditorsAndReviewersDto editorsAndReviewersDto, final String taskId) {
        Assert.notNull(editorsAndReviewersDto, "Editors and reviewers object can't be null!");
        Assert.notNull(taskId, "Task id can't be null!");
        Assert.noNullElements(Stream.of(editorsAndReviewersDto.getIssn(), editorsAndReviewersDto.getReviewers()).toArray(), "One or more fields are null!");

        MagazineCreationServiceImpl.log.info("Choosing editors and reviewers for magazine with issn '{}'", editorsAndReviewersDto.getIssn());

        final Task task = this.taskService.createTaskQuery().active().taskId(taskId).singleResult();
        MagazineCreationServiceImpl.log.info("Retrieved choose editors and reviewers task");

        if (task == null)
            throw new TaskNotFoundException(MagazineCreationServiceImpl.CHOOSE_EDITORS_AND_REVIEWERS_TASK);

        final Magazine magazine = this.magazineService.findByIssn(editorsAndReviewersDto.getIssn());

        if (editorsAndReviewersDto.getEditors() != null && editorsAndReviewersDto.getEditors().size() > 0) {
            final List<User> editors = editorsAndReviewersDto.getEditors()
                    .stream()
                    .map(basicUserInfoDto -> this.userService.findByUsername(basicUserInfoDto.getName()))
                    .collect(Collectors.toList());
            magazine.setEditors(new HashSet<>(editors));
        }

        final List<User> reviewers = editorsAndReviewersDto.getReviewers()
                .stream()
                .map(basicUserInfoDto -> this.userService.findByUsername(basicUserInfoDto.getName()))
                .collect(Collectors.toList());
        magazine.setReviewers(new HashSet<>(reviewers));

        magazine.setChosenEditorsAndReviewers(true);

        this.magazineService.save(magazine);

        this.runtimeService.setVariable(task.getProcessInstanceId(), MagazineCreationServiceImpl.EDITORS_AND_REVIEWERS_DTO, editorsAndReviewersDto);
        MagazineCreationServiceImpl.log.info("Set execution variable 'editorsAndReviewersDto' -> {}", editorsAndReviewersDto);

        final Map<String, Object> editorsAndReviewersFormFields = this.util.transformObjectToMap(editorsAndReviewersDto);
        this.formService.submitTaskForm(task.getId(), editorsAndReviewersFormFields);
        MagazineCreationServiceImpl.log.info("Submitted choose editors and reviewers form with following fields: {}", editorsAndReviewersFormFields);
    }

    @Override
    public List<TaskDto> getActiveCheckMagazineDataTasks() {
        MagazineCreationServiceImpl.log.info("Retrieving all active and unassigned 'check magazine data' tasks");
        return this.taskService.createTaskQuery()
                .taskDefinitionKey(MagazineCreationServiceImpl.CHECK_MAGAZINE_DATA_TASK)
                .active()
                .taskUnassigned()
                .list()
                .stream()
                .map(TaskDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public FormFieldsDto getCheckMagazineDataFormFields(final String taskId) {
        return this.util.getTaskFormFields(taskId);
    }

    @Override
    public void checkMagazineData(final CheckMagazineDto checkMagazineDto) {
        Assert.notNull(checkMagazineDto, "Check magazine object can't be null!");
        Assert.notNull(checkMagazineDto.getTaskId(), "Task id can't be null!");
        Assert.notNull(checkMagazineDto.getAdmin(), "Admin username can't be null!");

        MagazineCreationServiceImpl.log.info("Check magazine data for task with id '{}'", checkMagazineDto.getTaskId());
        final Task task = this.taskService.createTaskQuery().taskId(checkMagazineDto.getTaskId()).singleResult();

        final User admin = this.userService.findByUsername(checkMagazineDto.getAdmin());

        if (admin == null)
            throw new UserNotFoundException(checkMagazineDto.getAdmin());

        if (!admin.getAuthorities().contains(this.authorityService.findByName(MagazineCreationServiceImpl.ROLE_ADMINISTRATOR)))
            throw new UnauthorizedTaskAccessException(checkMagazineDto.getAdmin(), checkMagazineDto.getTaskId());

        this.taskService.setAssignee(task.getId(), checkMagazineDto.getAdmin());

        this.runtimeService.setVariable(task.getProcessInstanceId(), MagazineCreationServiceImpl.MAGAZINE_ACTIVATED, checkMagazineDto.isMagazineActivated());
        MagazineCreationServiceImpl.log.info("Set execution variable 'magazineActivated' -> {}", checkMagazineDto.isMagazineActivated());

        final HashMap<String, Object> formFields = new HashMap<>();
        formFields.put(MagazineCreationServiceImpl.MAGAZINE_ACTIVATED, checkMagazineDto.isMagazineActivated());
        this.formService.submitTaskForm(checkMagazineDto.getTaskId(), formFields);
        MagazineCreationServiceImpl.log.info("Submitted check magazine data form with following fields: {}", formFields);
    }

    @Override
    public FormFieldsDto getMagazineFormTaskFormFields(final String issn, final String editor) {
        final String processInstanceId = this.getProcessInstanceId(issn, editor);

        final Task task = this.util.getActiveUserTaskByDefinitionKey(processInstanceId, MagazineCreationServiceImpl.MAGAZINE_FORM_TASK);

        if (task == null)
            throw new TaskNotFoundException(MagazineCreationServiceImpl.MAGAZINE_FORM_TASK);

        if (!task.getAssignee().equals(editor))
            throw new UnauthorizedTaskAccessException(editor, task.getId());

        MagazineCreationServiceImpl.log.info("Retrieved magazine creation form task");

        final List<FormField> formFields = this.formService.getTaskFormData(task.getId()).getFormFields();

        return FormFieldsDto.builder()
                .processInstanceId(processInstanceId)
                .taskId(task.getId())
                .formFields(formFields)
                .build();
    }

    private String getProcessInstanceId(final String issn, final String editor) {
        Assert.notNull(issn, "Magazine issn can't be null!");
        Assert.notNull(editor, "Editor username can't be null!");

        final List<VariableInstance> magazineDtoVariables = this.runtimeService.createVariableInstanceQuery().variableName(MagazineCreationServiceImpl.MAGAZINE_DTO).list();

        String processInstanceId = null;
        for (final VariableInstance variableInstance : magazineDtoVariables) {
            final MagazineDto magazineDto = (MagazineDto) SerializationUtils.deserialize(((VariableInstanceEntity) variableInstance).getByteArrayValue());
            if (magazineDto != null && magazineDto.getIssn().equals(issn)) {
                processInstanceId = variableInstance.getProcessInstanceId();
                break;
            }
        }

        final ProcessInstance processInstance = this.runtimeService.createProcessInstanceQuery().active().processInstanceId(processInstanceId).singleResult();

        if (processInstance == null)
            throw new ProcessInstanceNotFoundException(processInstanceId);

        return processInstanceId;
    }

    @Override
    public void execute(final DelegateExecution delegateExecution) {
        MagazineCreationServiceImpl.log.info("Executing save magazine task");
        delegateExecution.setVariable(MagazineCreationServiceImpl.MAGAZINE_SAVED, false);

        final MagazineDto magazineDto = (MagazineDto) delegateExecution.getVariable(MagazineCreationServiceImpl.MAGAZINE_DTO);
        MagazineCreationServiceImpl.log.info("Retrieved execution variable 'magazineDto' -> {}", magazineDto);

        Magazine magazine = this.magazineService.findByIssn(magazineDto.getIssn());
        boolean existing = false;
        if (magazine != null) {
            final String magazineIssn = (String) delegateExecution.getVariable(MagazineCreationServiceImpl.MAGAZINE_ISSN);
            MagazineCreationServiceImpl.log.info("Retrieved execution variable 'magazine_issn' -> {}", magazineIssn);
            if (!magazine.getIssn().equals(magazineIssn)) {
                this.sendMagazineValidationMessage(false, "Magazine with issn '".concat(magazineDto.getIssn()).concat("' already exists!"));
                return;
            } else {
                existing = true;
            }
        }

        final String mainEditorUsername = (String) delegateExecution.getVariable(MagazineCreationServiceImpl.USER_INITIATOR);
        MagazineCreationServiceImpl.log.info("Retrieved execution variable 'user_initiator' -> {}", mainEditorUsername);

        final User mainEditor = this.userService.findByUsername(mainEditorUsername);
        if (mainEditor == null) {
            this.sendMagazineValidationMessage(false, "Main editor with the username '".concat(mainEditorUsername).concat("' does not exists!"));
            return;
        }

        final long id = existing ? magazine.getId() : -1;
        magazine = this.modelMapper.map(magazineDto, Magazine.class);

        if (existing) {
            magazine.setId(id);
        }

        magazine.setMainEditor(mainEditor);
        magazine.setPayment(PaymentType.valueOfGivenString(magazineDto.getPayment()));
        magazine.setEnabled(false);
        magazine.setRequestedChanges(false);
        magazine.setChosenEditorsAndReviewers(false);

        this.magazineService.save(magazine);
        MagazineCreationServiceImpl.log.info("Magazine entity with issn '{}' and name '{}' is saved successfully", magazine.getIssn(), magazine.getName());

        delegateExecution.setVariable(MagazineCreationServiceImpl.MAGAZINE_SAVED, true);
        delegateExecution.setVariable(MagazineCreationServiceImpl.MAGAZINE_ACTIVATED, false);
        delegateExecution.setVariable(MagazineCreationServiceImpl.MAGAZINE_ISSN, magazine.getIssn());

        this.sendMagazineValidationMessage(true, null);
    }

    private void sendMagazineValidationMessage(final boolean valid, final String message) {
        MagazineCreationServiceImpl.log.info("Sending notification about magazine creation through websocket");
        final ValidationDto validationDto = ValidationDto.builder()
                .valid(valid)
                .errorMessage(!valid ? message : null)
                .build();
        this.simpMessagingTemplate.convertAndSend("/magazine/creation", validationDto);
    }
}