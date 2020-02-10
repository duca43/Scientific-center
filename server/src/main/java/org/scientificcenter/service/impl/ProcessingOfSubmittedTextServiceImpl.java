package org.scientificcenter.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.form.FormFieldValidationConstraint;
import org.camunda.bpm.engine.impl.form.FormFieldImpl;
import org.camunda.bpm.engine.impl.form.FormFieldValidationConstraintImpl;
import org.camunda.bpm.engine.impl.form.type.StringFormType;
import org.camunda.bpm.engine.impl.persistence.entity.VariableInstanceEntity;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.VariableInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.variable.impl.value.PrimitiveTypeValueImpl;
import org.scientificcenter.dto.*;
import org.scientificcenter.exception.*;
import org.scientificcenter.model.Recommendation;
import org.scientificcenter.model.ScientificArea;
import org.scientificcenter.model.ScientificPaper;
import org.scientificcenter.model.User;
import org.scientificcenter.service.*;
import org.scientificcenter.util.MultivaluedEnumFormType;
import org.scientificcenter.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.SerializationUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class ProcessingOfSubmittedTextServiceImpl implements ProcessingOfSubmittedTextService {

    private final IdentityService identityService;

    private final RuntimeService runtimeService;

    private final TaskService taskService;

    private final FormService formService;

    private final UserService userService;

    private final AuthorityService authorityService;

    private final RegistrationService registrationService;

    private final ScientificPaperService scientificPaperService;

    private final Util util;

    @Value("${scientific-papers-dir}")
    private String scientificPapersDirectory;
    private static final String PROCESS_KEY = "Processing_of_submitted_text";
    private static final String REGISTRATION_SUB_PROCESS_KEY = "User_registration";
    private static final String CHOOSE_MAGAZINE_TASK = "Choose_magazine_task";
    private static final String ENTER_SCIENTIFIC_PAPER_INFO_TASK = "Enter_scientific_paper_info_task";
    private static final String ADD_CO_AUTHORS_TASK = "Add_co-authors_task";
    private static final String REVIEW_PAPER_TASK = "Review_paper_task";
    private static final String REVIEW_PDF_DOCUMENT_TASK = "Review_PDF_document_task";
    private static final String CORRECT_PAPER_TASK = "Correct_paper_task";
    private static final String CHOOSE_REVIEWERS_TASK = "Choose_reviewers_task";
    private static final String PAPER_REVIEW_BY_REVIEWER_TASK = "Paper_review_by_reviewer_task";
    private static final String ANALYZE_REVIEW_TASK = "Analyze_review_task";
    private static final String DECISION_TASK = "Decision_task";
    private static final String FINAL_CORRECT_PAPER_TASK = "Final_correct_paper_task";
    private static final String REVIEW_CORRECTED_PDF_DOCUMENT_TASK = "Review_corrected_PDF_document_task";
    private static final String MAKE_FINAL_DECISION_TASK = "Make_final_decision_task";
    private static final String MAGAZINE_FIELD_ID = "magazine";
    private static final String REVIEWERS_FIELD_ID = "reviewers";
    private static final String ROLE_USER = "ROLE_USER";
    private static final String SCIENTIFIC_PAPER_DTO = "scientificPaperDto";
    private static final String ADD_COAUTHOR_DTO = "addCoauthorDto";
    private static final String APPROVE_SCIENTIFIC_PAPER_FLAG = "approveScientificPaper";
    private static final String IS_PAPER_FORMATTED_WELL_FLAG = "isPaperFormattedWell";
    private final static String RECOMMENDATION = "recommendation";
    private final static String REVIEWS = "reviews";
    private static final String COMMENT_ABOUT_PAPER = "commentAboutPaper";
    private static final String COMMENT_FOR_EDITOR = "commentForEditor";
    private final static String DECISION = "decision";
    private static final String CORRECTIONS_DEADLINE = "correctionsDeadline";
    private final static String FINAL_DECISION = "finalDecision";

    @Autowired
    public ProcessingOfSubmittedTextServiceImpl(final IdentityService identityService, final RuntimeService runtimeService, final TaskService taskService, final FormService formService, final UserService userService, final AuthorityService authorityService, final RegistrationService registrationService, final ScientificPaperService scientificPaperService, final Util util) {
        this.identityService = identityService;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.formService = formService;
        this.userService = userService;
        this.authorityService = authorityService;
        this.registrationService = registrationService;
        this.scientificPaperService = scientificPaperService;
        this.util = util;
    }

    @Override
    public BeginProcessForSubmittingTextDto beginProcessingOfSubmittedTextProcess(final BasicUserInfoDto authorInfo) {
        Assert.notNull(authorInfo, "Author info object can't be null!");
        log.info("Starting process for processing submitted text");

        boolean userExists = false;
        this.identityService.setAuthenticatedUserId(null);
        if (authorInfo.getName() != null) {
            final User user = this.userService.findByUsername(authorInfo.getName());
            if (user != null && user.isEnabled() && user.getAuthorities().contains(this.authorityService.findByName(ROLE_USER))) {
                userExists = true;
                this.identityService.setAuthenticatedUserId(authorInfo.getName());
            }
        }

        log.info("Existence of user with given user name: {}", userExists);

        final ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey(PROCESS_KEY);
        log.info("Process instance with id '{}' is created.", processInstance.getId());

        final FormFieldsDto formFieldsDto;
        if (userExists) {
            formFieldsDto = this.getChooseMagazineFormFields(processInstance.getId());
        } else {
            final ProcessInstance registrationProcessInstance = this.runtimeService.createProcessInstanceQuery()
                    .superProcessInstanceId(processInstance.getId())
                    .processDefinitionKey(REGISTRATION_SUB_PROCESS_KEY)
                    .active()
                    .singleResult();
            formFieldsDto = this.registrationService.createRegistrationFormTask(registrationProcessInstance.getId());
        }

        return BeginProcessForSubmittingTextDto.builder()
                .formFieldsDto(formFieldsDto)
                .userExists(userExists)
                .build();
    }

    @Override
    public FormFieldsDto getChooseMagazineFormFields(final String processInstanceId) {
        return this.getFormFieldsDto(processInstanceId, CHOOSE_MAGAZINE_TASK);
    }

    @Override
    public void chooseMagazine(final ChooseMagazineDto chooseMagazineDto) {
        Assert.notNull(chooseMagazineDto, "Choose magazine object can't be null!");
        Assert.notNull(chooseMagazineDto.getMagazineId(), "Magazine id can't be null!");
        Assert.notNull(chooseMagazineDto.getProcessInstanceId(), "Process instance id can't be null!");

        log.info("Choosing magazine with id '{}'", chooseMagazineDto.getMagazineId());

        final Task task = this.util.getActiveUserTaskByDefinitionKey(chooseMagazineDto.getProcessInstanceId(), CHOOSE_MAGAZINE_TASK);

        if (task == null)
            throw new TaskNotFoundException(CHOOSE_MAGAZINE_TASK);

        log.info("Task for choosing magazine is retrieved. Assignee: '{}'.", task.getAssignee());

        final Map<String, Object> chooseMagazineFormFields = new HashMap<>();
        chooseMagazineFormFields.put(MAGAZINE_FIELD_ID, String.valueOf(chooseMagazineDto.getMagazineId()));

        this.formService.submitTaskForm(task.getId(), chooseMagazineFormFields);
        log.info("Submitted choose magazine form with following fields: {}", chooseMagazineFormFields);
    }

    @Override
    public FormFieldsDto getEnterScientificPaperInfoFormFields(final String processInstanceId) {
        return this.getFormFieldsDto(processInstanceId, ENTER_SCIENTIFIC_PAPER_INFO_TASK);
    }

    @Override
    public void enterScientificPaperInfo(final CreateScientificPaperDto createScientificPaperDto, final String processInstanceId) {
        Assert.notNull(createScientificPaperDto, "Scientific paper object can't be null!");
        Assert.notNull(processInstanceId, "Process instance id can't be null!");
        Assert.notNull(createScientificPaperDto.getTitle(), "Scientific paper title can't be null!");

        log.info("Entering scientific paper info titled '{}'", createScientificPaperDto.getTitle());

        final Task task = this.util.getActiveUserTaskByDefinitionKey(processInstanceId, ENTER_SCIENTIFIC_PAPER_INFO_TASK);

        if (task == null)
            throw new TaskNotFoundException(ENTER_SCIENTIFIC_PAPER_INFO_TASK);

        log.info("Enter scientific paper info task is retrieved. Assignee: '{}'.", task.getAssignee());

        this.runtimeService.setVariable(task.getProcessInstanceId(), SCIENTIFIC_PAPER_DTO, createScientificPaperDto);
        log.info("Set execution variable 'scientificPaperDto' -> {}", createScientificPaperDto);

        final Map<String, Object> enterScientificPaperInfoFormFields = this.util.transformObjectToMap(createScientificPaperDto);
        this.formService.submitTaskForm(task.getId(), enterScientificPaperInfoFormFields);
        log.info("Submitted enter scientific paper info form with following fields: {}", enterScientificPaperInfoFormFields);
    }

    @Override
    public CreateScientificPaperDto uploadScientificPaper(final MultipartFile file, final String author) {

        final String targetPath = this.upload(file, author);

        final ScientificPaper scientificPaper = ScientificPaper.builder()
                .pdfFilePath(targetPath)
                .build();

        this.scientificPaperService.save(scientificPaper);

        return CreateScientificPaperDto.builder()
                .id(scientificPaper.getId())
                .pdfFile(scientificPaper.getPdfFilePath())
                .build();
    }

    @Override
    public CreateScientificPaperDto reUploadScientificPaper(final MultipartFile file, final String author, final Long scientificPaperId) {
        final String targetPath = this.upload(file, author);

        final ScientificPaper scientificPaper = this.scientificPaperService.findById(scientificPaperId);

        if (scientificPaper == null)
            throw new ScientificPaperNotFoundException(scientificPaperId);

        log.info("Updating pdf file path of scientific paper with id '{}' to '{}'", scientificPaperId, targetPath);
        scientificPaper.setPdfFilePath(targetPath);
        this.scientificPaperService.save(scientificPaper);

        return CreateScientificPaperDto.builder()
                .pdfFile(scientificPaper.getPdfFilePath())
                .build();
    }

    @SneakyThrows
    private String upload(final MultipartFile file, final String author) {
        Assert.notNull(file, "Scientific paper file can't be null!");
        Assert.notNull(author, "Author username can't be null!");

        log.info("Uploading file named '{}'", file.getOriginalFilename());

        final URL url = this.getClass().getClassLoader().getResource(this.scientificPapersDirectory);

        if (url == null) throw new IOException();

        final File directory = new File(url.getPath().replace("%20", " ") + File.separator + author);

        if (!directory.exists()) directory.mkdirs();

        final String targetPath = directory.getAbsolutePath() + File.separator + file.getOriginalFilename();
        log.info("Created target path: {}", targetPath);

        final Path target = Paths.get(targetPath);
        Files.write(target, file.getBytes());
        log.info("File named '{}' is written successfully", file.getOriginalFilename());

        return targetPath;
    }

    @Override
    public Resource downloadScientificPaper(final String author, final Long scientificPaperId) {
        Assert.notNull(author, "Author username can't be null!");
        Assert.notNull(scientificPaperId, "Scientific paper id can't be null!");

        log.info("Downloading scientific paper with id '{}'", scientificPaperId);
        final ScientificPaper scientificPaper = this.scientificPaperService.findById(scientificPaperId);

        if (scientificPaper == null)
            throw new ScientificPaperNotFoundException(scientificPaperId);

        if (!scientificPaper.getAuthor().getUsername().equals(author))
            throw new NotAuthorOfScientificPaperException(scientificPaperId);

        log.info("Path of scientific paper with id '{}': {}", scientificPaperId, scientificPaper.getPdfFilePath());

        final Path path = Paths.get(scientificPaper.getPdfFilePath());

        try {
            return new UrlResource(path.toUri());
        } catch (final MalformedURLException e) {
            log.error("Downloading of scientific paper with id '{}' has failed", scientificPaperId);
            return null;
        }
    }

    @Override
    public FormFieldsDto getAddCoauthorsFormFields(final Long scientificPaperId) {
        final String processInstanceId = this.getProcessInstanceId(scientificPaperId);
        return this.getFormFieldsDto(processInstanceId, ADD_CO_AUTHORS_TASK);
    }

    @Override
    public void addCoauthor(final AddCoauthorDto addCoauthorDto, final String processInstanceId) {
        Assert.notNull(addCoauthorDto, "Add coauthor object can't be null!");
        Assert.notNull(processInstanceId, "Process instance id can't be null!");
        Assert.noNullElements(Stream.of(addCoauthorDto.getName(), addCoauthorDto.getEmail(), addCoauthorDto.getLocation()).toArray(),
                "Name, email and/or location can't be null!");

        log.info("Adding co-author named '{}'", addCoauthorDto.getName());

        final Task task = this.util.getActiveUserTaskByDefinitionKey(processInstanceId, ADD_CO_AUTHORS_TASK);

        if (task == null)
            throw new TaskNotFoundException(ADD_CO_AUTHORS_TASK);

        log.info("Add co-author task is retrieved. Assignee: '{}'.", task.getAssignee());

        this.runtimeService.setVariable(task.getProcessInstanceId(), ADD_COAUTHOR_DTO, addCoauthorDto);
        log.info("Set execution variable 'addCoauthorDto' -> {}", addCoauthorDto);

        final Map<String, Object> addCoauthorFormFields = this.util.transformObjectToMap(addCoauthorDto);
        this.formService.submitTaskForm(task.getId(), addCoauthorFormFields);
        log.info("Submitted add co-author form with following fields: {}", addCoauthorFormFields);
    }

    @Override
    public List<TaskDto> getActiveReviewPaperTasks(final String mainEditor) {
        log.info("Retrieving all active 'review paper' tasks for editor '{}'", mainEditor);
        return this.taskService.createTaskQuery()
                .taskDefinitionKey(REVIEW_PAPER_TASK)
                .active()
                .taskAssignee(mainEditor)
                .list()
                .stream()
                .map(TaskDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public FormFieldsDto getTaskFormFields(final String taskId) {
        return this.util.getTaskFormFields(taskId);
    }

    @Override
    public void reviewPaper(final ReviewPaperDto reviewPaperDto) {
        Assert.notNull(reviewPaperDto, "Review paper object can't be null!");
        Assert.notNull(reviewPaperDto.getTaskId(), "Task id can't be null!");
        Assert.notNull(reviewPaperDto.getEditor(), "Editor username can't be null!");

        final Task task = this.taskService.createTaskQuery().taskId(reviewPaperDto.getTaskId()).active().singleResult();

        if (task == null)
            throw new TaskNotFoundException(REVIEW_PAPER_TASK);

        log.info("Review paper task is retrieved. Assignee: '{}'.", task.getAssignee());

        if (!task.getAssignee().equals(reviewPaperDto.getEditor()))
            throw new UnauthorizedTaskAccessException(reviewPaperDto.getEditor(), reviewPaperDto.getTaskId());

        final Map<String, Object> reviewPaperFormFields = new HashMap<>();
        reviewPaperFormFields.put(APPROVE_SCIENTIFIC_PAPER_FLAG, reviewPaperDto.getApproveScientificPaper() != null && reviewPaperDto.getApproveScientificPaper());
        this.formService.submitTaskForm(task.getId(), reviewPaperFormFields);
        log.info("Submitted review paper form with following fields: {}", reviewPaperFormFields);
    }

    @Override
    public List<TaskDto> getActiveReviewPdfDocumentTasks(final String mainEditor) {
        log.info("Retrieving all active 'review pdf document' tasks for editor '{}'", mainEditor);
        final List<TaskDto> newPaperTasks = this.taskService.createTaskQuery()
                .taskDefinitionKey(REVIEW_PDF_DOCUMENT_TASK)
                .active()
                .taskAssignee(mainEditor)
                .list()
                .stream()
                .map(task -> new ReviewPdfDocumentTaskDto(task, false))
                .collect(Collectors.toList());
        final List<TaskDto> correctedPaperTasks = this.taskService.createTaskQuery()
                .taskDefinitionKey(REVIEW_CORRECTED_PDF_DOCUMENT_TASK)
                .active()
                .taskAssignee(mainEditor)
                .list()
                .stream()
                .map(task -> new ReviewPdfDocumentTaskDto(task, true))
                .collect(Collectors.toList());

        if (!correctedPaperTasks.isEmpty()) {
            newPaperTasks.addAll(correctedPaperTasks);
        }

        return newPaperTasks;
    }

    @Override
    public FormFieldsDto reviewPdfDocument(final ReviewPdfDto reviewPdfDto) {
        Assert.notNull(reviewPdfDto, "Review PDF document object can't be null!");
        Assert.notNull(reviewPdfDto.getTaskId(), "Task id can't be null!");
        Assert.notNull(reviewPdfDto.getEditor(), "Editor username can't be null!");

        final Task task = this.taskService.createTaskQuery().taskId(reviewPdfDto.getTaskId()).active().singleResult();

        if (task == null)
            throw new TaskNotFoundException(REVIEW_PDF_DOCUMENT_TASK);

        log.info("{} task is retrieved. Assignee: '{}'.", task.getName(), task.getAssignee());

        if (!task.getAssignee().equals(reviewPdfDto.getEditor()))
            throw new UnauthorizedTaskAccessException(reviewPdfDto.getEditor(), reviewPdfDto.getTaskId());

        final boolean afterReviewsCorrection = this.runtimeService.getVariable(task.getProcessInstanceId(), DECISION) != null;

        final Map<String, Object> reviewPdfDocumentFormFields = new HashMap<>();

        if (!afterReviewsCorrection) {
            reviewPdfDocumentFormFields.put(IS_PAPER_FORMATTED_WELL_FLAG, reviewPdfDto.getIsPaperFormattedWell() != null && reviewPdfDto.getIsPaperFormattedWell());
            reviewPdfDocumentFormFields.put("comment", reviewPdfDto.getComment());
            reviewPdfDocumentFormFields.put("correctionTime", reviewPdfDto.getCorrectionTime() == null ? 1L : reviewPdfDto.getCorrectionTime());
        }

        this.formService.submitTaskForm(task.getId(), reviewPdfDocumentFormFields);
        log.info("Submitted {} form with following fields: {}", task.getName(), reviewPdfDocumentFormFields);

        if (!afterReviewsCorrection) {
            return this.util.getMakeADecisionTaskFormFieldsDto(task.getProcessInstanceId(), task.getAssignee());
        } else {
            final Task makeFinalDecisionTask = this.taskService.createTaskQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .taskDefinitionKey(MAKE_FINAL_DECISION_TASK)
                    .taskAssignee(task.getAssignee())
                    .active()
                    .singleResult();

            if (makeFinalDecisionTask != null) {
                log.info("Make final decision task assigned to '{}' is found", makeFinalDecisionTask.getAssignee());
                final FormFieldsDto formFieldsDto = this.getTaskFormFields(makeFinalDecisionTask.getId());
                final CreateScientificPaperDto createScientificPaperDto = (CreateScientificPaperDto) this.runtimeService.getVariable(formFieldsDto.getProcessInstanceId(), SCIENTIFIC_PAPER_DTO);
                formFieldsDto.setToken(createScientificPaperDto.getTitle());
                return formFieldsDto;
            }

            return null;
        }
    }

    @Override
    public FormFieldsDto getCorrectPaperFormFields(final Long scientificPaperId) {
        final String processInstanceId = this.getProcessInstanceId(scientificPaperId);
        final boolean afterReviewsCorrection = this.runtimeService.getVariable(processInstanceId, DECISION) != null;

        if (afterReviewsCorrection) {
            final FormFieldsDto formFieldsDto = this.getFormFieldsDto(processInstanceId, FINAL_CORRECT_PAPER_TASK);
            final List<PaperReviewByReviewerDto> paperReviewByReviewerDtos = (List<PaperReviewByReviewerDto>) this.runtimeService.getVariable(processInstanceId, REVIEWS);
            log.info("Retrieve execution variable 'paperReviewByReviewerDtos' -> {}", paperReviewByReviewerDtos);

            final StringFormType stringFormType = new StringFormType();
            final FormFieldValidationConstraintImpl formFieldValidationConstraint = new FormFieldValidationConstraintImpl("readonly", null);
            final List<FormFieldValidationConstraint> validators = Collections.singletonList(formFieldValidationConstraint);
            for (int i = 0; i < paperReviewByReviewerDtos.size(); i++) {
                final PaperReviewByReviewerDto paperReviewByReviewerDto = paperReviewByReviewerDtos.get(i);
                final FormFieldImpl formFieldImpl = new FormFieldImpl();
                formFieldImpl.setId("reviewerCommentForAuthor".concat(String.valueOf(i + 1)));
                final PrimitiveTypeValueImpl.StringValueImpl stringValue = new PrimitiveTypeValueImpl.StringValueImpl(paperReviewByReviewerDto.getCommentAboutPaper());
                formFieldImpl.setValue(stringValue);
                formFieldImpl.setLabel("Comment by ".concat(paperReviewByReviewerDto.getReviewer()));
                formFieldImpl.setType(stringFormType);
                formFieldImpl.setValidationConstraints(validators);
                formFieldsDto.getFormFields().add(formFieldImpl);
                log.info("Added form field with id '{}' into form fields list", formFieldImpl.getId());
            }

            return formFieldsDto;
        } else {
            return this.getFormFieldsDto(processInstanceId, CORRECT_PAPER_TASK);
        }
    }

    @Override
    public void correctPaper(final CorrectScientificPaperDto correctScientificPaperDto) {
        Assert.notNull(correctScientificPaperDto, "Correct paper object can't be null!");
        Assert.notNull(correctScientificPaperDto.getProcessInstanceId(), "Process instance id can't be null!");
        Assert.notNull(correctScientificPaperDto.getPdfFile(), "Pdf file path can't be null!");

        final boolean afterReviewsCorrection = this.runtimeService.getVariable(correctScientificPaperDto.getProcessInstanceId(), DECISION) != null;

        final Task task;
        if (afterReviewsCorrection) {
            log.info("Final paper correction");
            task = this.util.getActiveUserTaskByDefinitionKey(correctScientificPaperDto.getProcessInstanceId(), FINAL_CORRECT_PAPER_TASK);
        } else {
            log.info("Paper correction");
            task = this.util.getActiveUserTaskByDefinitionKey(correctScientificPaperDto.getProcessInstanceId(), CORRECT_PAPER_TASK);
        }

        if (task == null)
            throw new TaskNotFoundException(afterReviewsCorrection ? FINAL_CORRECT_PAPER_TASK : CORRECT_PAPER_TASK);

        log.info("{} task is retrieved. Assignee: '{}'.", task.getName(), task.getAssignee());

        final Map<String, Object> correctPaperFormFields = new HashMap<>();
        correctPaperFormFields.put("pdfFile", correctScientificPaperDto.getPdfFile());

        if (afterReviewsCorrection)
            correctPaperFormFields.put("authorReply", correctScientificPaperDto.getAuthorReply());

        this.formService.submitTaskForm(task.getId(), correctPaperFormFields);
        log.info("Submitted {} form with following fields: {}", task.getName(), correctPaperFormFields);

        final CreateScientificPaperDto createScientificPaperDto = (CreateScientificPaperDto) this.runtimeService.getVariable(task.getProcessInstanceId(), SCIENTIFIC_PAPER_DTO);
        final ScientificPaper scientificPaper = this.scientificPaperService.findById(createScientificPaperDto.getId());
        scientificPaper.setRequestedChanges(false);

        if (afterReviewsCorrection)
            scientificPaper.setPdfFormattedWell(true);

        this.scientificPaperService.save(scientificPaper);
    }

    @Override
    public List<TaskDto> getActiveChooseReviewersTasks(final String chosenEditor) {
        log.info("Retrieving all active 'choose reviewers' tasks for editor '{}'", chosenEditor);
        return this.taskService.createTaskQuery()
                .taskDefinitionKey(CHOOSE_REVIEWERS_TASK)
                .active()
                .taskAssignee(chosenEditor)
                .list()
                .stream()
                .map(TaskDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public FormFieldsDto getChooseReviewersTaskFormFields(final String taskId) {
        final FormFieldsDto formFieldsDto = this.util.getTaskFormFields(taskId);
        final CreateScientificPaperDto createScientificPaperDto = (CreateScientificPaperDto) this.runtimeService.getVariable(formFieldsDto.getProcessInstanceId(), SCIENTIFIC_PAPER_DTO);
        final ScientificPaper scientificPaper = this.scientificPaperService.findById(createScientificPaperDto.getId());
        final ScientificArea scientificArea = scientificPaper.getScientificArea();

        final List<String> reviewers = (List<String>) this.runtimeService.getVariable(formFieldsDto.getProcessInstanceId(), "reviewers");

        final Map<String, String> availableReviewers;

        if (reviewers != null) {
            availableReviewers = scientificPaper.getMagazine()
                    .getReviewers()
                    .stream()
                    .filter(user -> user.getScientificAreas().contains(scientificArea) && !reviewers.contains(user.getUsername()))
                    .collect(Collectors.toMap(user -> user.getId().toString(), User::getUsername));
        } else {
            availableReviewers = scientificPaper.getMagazine()
                    .getReviewers()
                    .stream()
                    .filter(user -> user.getScientificAreas().contains(scientificArea))
                    .collect(Collectors.toMap(user -> user.getId().toString(), User::getUsername));
        }

        formFieldsDto.getFormFields().forEach(formField -> {
            if (formField.getType() instanceof MultivaluedEnumFormType && formField.getId().equals(REVIEWERS_FIELD_ID)) {
                ((MultivaluedEnumFormType) formField.getType()).getValues().clear();
                ((MultivaluedEnumFormType) formField.getType()).getValues().putAll(availableReviewers);
                log.info("Added {} reviewers into multivalued enum form field '{}'", availableReviewers.values().size(), REVIEWERS_FIELD_ID);
            }
        });
        formFieldsDto.setToken(scientificPaper.getTitle());
        return formFieldsDto;
    }

    @Override
    public void chooseReviewers(final ChooseReviewersDto chooseReviewersDto) {
        Assert.notNull(chooseReviewersDto, "Correct paper object can't be null!");
        Assert.notNull(chooseReviewersDto.getTaskId(), "Task id can't be null!");
        Assert.notNull(chooseReviewersDto.getReviewers(), "Reviewers list can't be null!");
        Assert.notNull(chooseReviewersDto.getReviewsDeadline(), "Reviews deadline can't be null!");

        log.info("Choosing reviewers for scientific paper");

        final Task task = this.taskService.createTaskQuery().taskId(chooseReviewersDto.getTaskId()).active().singleResult();

        if (task == null)
            throw new TaskNotFoundException(CHOOSE_REVIEWERS_TASK);

        log.info("Choose reviewers task is retrieved. Assignee: '{}'.", task.getAssignee());

        final Map<String, Object> chooseReviewersFormFields = new HashMap<>();
        chooseReviewersFormFields.put(REVIEWERS_FIELD_ID, chooseReviewersDto.getReviewers().stream().map(BasicUserInfoDto::getName).collect(Collectors.toList()));
        chooseReviewersFormFields.put("reviewsDeadline", chooseReviewersDto.getReviewsDeadline());
        this.formService.submitTaskForm(task.getId(), chooseReviewersFormFields);
        log.info("Submitted choose reviewers form with following fields: {}", chooseReviewersFormFields);

        final CreateScientificPaperDto createScientificPaperDto = (CreateScientificPaperDto) this.runtimeService.getVariable(task.getProcessInstanceId(), SCIENTIFIC_PAPER_DTO);
        final ScientificPaper scientificPaper = this.scientificPaperService.findById(createScientificPaperDto.getId());
        final Set<User> reviewers = chooseReviewersDto.getReviewers()
                .stream()
                .map(basicUserInfoDto -> this.userService.findByUsername(basicUserInfoDto.getName()))
                .collect(Collectors.toSet());
        scientificPaper.setReviewers(reviewers);
        this.scientificPaperService.save(scientificPaper);
    }

    @Override
    public List<TaskDto> getActivePaperReviewByReviewerTasks(final String reviewer) {
        log.info("Retrieving all active 'paper review by reviewer' tasks for reviewer '{}'", reviewer);
        return this.taskService.createTaskQuery()
                .taskDefinitionKey(PAPER_REVIEW_BY_REVIEWER_TASK)
                .active()
                .taskAssignee(reviewer)
                .list()
                .stream()
                .map(TaskDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public FormFieldsDto getPaperReviewByReviewerTaskFormFields(final String taskId) {
        final FormFieldsDto formFieldsDto = this.util.getTaskFormFields(taskId);
        this.util.fillRecommendationsEnum(formFieldsDto.getFormFields(), RECOMMENDATION);
        final CreateScientificPaperDto createScientificPaperDto = (CreateScientificPaperDto) this.runtimeService.getVariable(formFieldsDto.getProcessInstanceId(), SCIENTIFIC_PAPER_DTO);
        formFieldsDto.setToken(createScientificPaperDto.getTitle());
        return formFieldsDto;
    }

    @Override
    public void paperReviewByReviewer(final PaperReviewByReviewerDto paperReviewByReviewerDto, final String taskId) {
        Assert.notNull(paperReviewByReviewerDto, "Paper review by reviewer object can't be null!");
        Assert.notNull(taskId, "Task id can't be null!");
        Assert.notNull(paperReviewByReviewerDto.getReviewer(), "Reviewer username can't be null!");
        Assert.notNull(paperReviewByReviewerDto.getCommentAboutPaper(), "Comment about paper can't be null!");
        Assert.notNull(paperReviewByReviewerDto.getRecommendation(), "Recommendation can't be null!");

        log.info("Paper review by reviewer '{}'", paperReviewByReviewerDto.getReviewer());

        final Task task = this.taskService.createTaskQuery().taskId(taskId).active().singleResult();

        if (task == null)
            throw new TaskNotFoundException(PAPER_REVIEW_BY_REVIEWER_TASK);

        log.info("Paper review by reviewer task is retrieved. Assignee: '{}'.", task.getAssignee());

        final List<PaperReviewByReviewerDto> paperReviewByReviewerDtos = (List<PaperReviewByReviewerDto>) this.runtimeService.getVariable(task.getProcessInstanceId(), REVIEWS);
        log.info("Retrieve execution variable 'paperReviewByReviewerDtos' -> {}", paperReviewByReviewerDtos);

        paperReviewByReviewerDtos.add(paperReviewByReviewerDto);
        this.runtimeService.setVariable(task.getProcessInstanceId(), REVIEWS, paperReviewByReviewerDtos);
        log.info("Set execution variable 'paperReviewByReviewerDtos' -> {}", paperReviewByReviewerDtos);

        final Map<String, Object> paperReviewByReviewerFormFields = new HashMap<>();
        paperReviewByReviewerFormFields.put(COMMENT_ABOUT_PAPER, paperReviewByReviewerDto.getCommentAboutPaper());
        paperReviewByReviewerFormFields.put(RECOMMENDATION, paperReviewByReviewerDto.getRecommendation());
        paperReviewByReviewerFormFields.put(COMMENT_FOR_EDITOR, paperReviewByReviewerDto.getCommentForEditor());
        this.formService.submitTaskForm(task.getId(), paperReviewByReviewerFormFields);
        log.info("Submitted paper review by reviewer form with following fields: {}", paperReviewByReviewerFormFields);

        this.runtimeService.setVariable(task.getProcessInstanceId(), COMMENT_ABOUT_PAPER, null);
        this.runtimeService.setVariable(task.getProcessInstanceId(), RECOMMENDATION, null);
        this.runtimeService.setVariable(task.getProcessInstanceId(), COMMENT_FOR_EDITOR, null);
    }

    @Override
    public List<TaskDto> getActiveAnalyzeReviewTasks(final String chosenEditor) {
        log.info("Retrieving all active 'analyze review' tasks for chosen editor '{}'", chosenEditor);
        return this.taskService.createTaskQuery()
                .taskDefinitionKey(ANALYZE_REVIEW_TASK)
                .active()
                .taskAssignee(chosenEditor)
                .list()
                .stream()
                .map(TaskDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public FormFieldsDto getAnalyzeReviewTaskFormFields(final String taskId) {
        final FormFieldsDto formFieldsDto = this.getTaskFormFields(taskId);
        final List<FormField> formFields = new ArrayList<>();
        for (final FormField formField : formFieldsDto.getFormFields()) {
            if (formField.getDefaultValue() != null) {
                final FormFieldImpl formFieldImpl = new FormFieldImpl();
                formFieldImpl.setId(formField.getId());
                final PrimitiveTypeValueImpl.StringValueImpl stringValue = new PrimitiveTypeValueImpl.StringValueImpl((String) formField.getDefaultValue());
                formFieldImpl.setValue(stringValue);
                formFieldImpl.setLabel(formField.getLabel());
                formFieldImpl.setType(formField.getType());
                formFieldImpl.setValidationConstraints(formField.getValidationConstraints());
                formFields.add(formFieldImpl);
            } else {
                formFields.add(formField);
            }
        }
        formFieldsDto.setFormFields(formFields);
        return formFieldsDto;
    }

    @Override
    public FormFieldsDto analyzeReview(final String taskId) {
        Assert.notNull(taskId, "Task id can't be null!");

        log.info("Analyze review");

        final Task task = this.taskService.createTaskQuery().taskId(taskId).active().singleResult();

        if (task == null)
            throw new TaskNotFoundException(ANALYZE_REVIEW_TASK);

        log.info("Analyze review task is retrieved. Assignee: '{}'.", task.getAssignee());

        this.formService.submitTaskForm(task.getId(), new HashMap<>());
        log.info("Submitted analyze review form");

        return this.util.getMakeADecisionTaskFormFieldsDto(task.getProcessInstanceId(), task.getAssignee());
    }

    @Override
    public FormFieldsDto getMakeADecisionFormFields(final String processInstanceId) {
        final FormFieldsDto formFieldsDto = this.getFormFieldsDto(processInstanceId, DECISION_TASK);
        this.util.fillRecommendationsEnum(formFieldsDto.getFormFields(), DECISION);
        final CreateScientificPaperDto createScientificPaperDto = (CreateScientificPaperDto) this.runtimeService.getVariable(formFieldsDto.getProcessInstanceId(), SCIENTIFIC_PAPER_DTO);
        formFieldsDto.setToken(createScientificPaperDto.getTitle());
        return formFieldsDto;
    }

    @Override
    public void makeADecision(final MakeADecisionDto makeADecisionDto) {
        Assert.notNull(makeADecisionDto, "Make a decision object can't be null!");
        Assert.notNull(makeADecisionDto.getTaskId(), "Task id can't be null!");
        Assert.notNull(makeADecisionDto.getDecision(), "Decision can't be null!");

        log.info("Making a decision");

        final Task task = this.taskService.createTaskQuery().taskId(makeADecisionDto.getTaskId()).active().singleResult();

        if (task == null)
            throw new TaskNotFoundException(DECISION_TASK);

        log.info("Make a decision task is retrieved. Assignee: '{}'.", task.getAssignee());

        if (makeADecisionDto.getDecision().equals(Recommendation.ACCEPT.getType())) {
            this.runtimeService.setVariable(task.getProcessInstanceId(), "subjectForEmail", "Accepted scientific paper");
            this.runtimeService.setVariable(task.getProcessInstanceId(), "decisionForEmail", "accepted");
            this.runtimeService.setVariable(task.getProcessInstanceId(), "decisionFlagForEmail", true);
        } else {
            this.runtimeService.setVariable(task.getProcessInstanceId(), "subjectForEmail", "Rejected scientific paper");
            this.runtimeService.setVariable(task.getProcessInstanceId(), "decisionForEmail", "rejected");
            this.runtimeService.setVariable(task.getProcessInstanceId(), "decisionFlagForEmail", false);
        }

        final long correctionsDeadline = makeADecisionDto.getCorrectionsDeadline() == null ? 1L : makeADecisionDto.getCorrectionsDeadline();
        this.runtimeService.setVariable(task.getProcessInstanceId(), CORRECTIONS_DEADLINE, correctionsDeadline);
        log.info("Set execution variable 'correctionsDeadline' -> {}", correctionsDeadline);

        final Map<String, Object> makeADecisionFormFields = new HashMap<>();
        makeADecisionFormFields.put(DECISION, makeADecisionDto.getDecision());
        makeADecisionFormFields.put(CORRECTIONS_DEADLINE, correctionsDeadline);
        this.formService.submitTaskForm(task.getId(), makeADecisionFormFields);
        log.info("Submitted make a decision form with following fields: {}", makeADecisionFormFields);
    }

    @Override
    public void makeFinalDecision(final MakeFinalDecisionDto makeFinalDecisionDto) {
        Assert.notNull(makeFinalDecisionDto, "Make final decision object can't be null!");
        Assert.notNull(makeFinalDecisionDto.getTaskId(), "Task id can't be null!");

        log.info("Making final decision");

        final Task task = this.taskService.createTaskQuery().taskId(makeFinalDecisionDto.getTaskId()).active().singleResult();

        if (task == null)
            throw new TaskNotFoundException(MAKE_FINAL_DECISION_TASK);

        log.info("Make final decision task is retrieved. Assignee: '{}'.", task.getAssignee());

        if (makeFinalDecisionDto.getFinalDecision() != null && makeFinalDecisionDto.getFinalDecision()) {
            this.runtimeService.setVariable(task.getProcessInstanceId(), "subjectForEmail", "Accepted scientific paper");
            this.runtimeService.setVariable(task.getProcessInstanceId(), "decisionForEmail", "accepted");
            this.runtimeService.setVariable(task.getProcessInstanceId(), "decisionFlagForEmail", true);
        }

        final Map<String, Object> makeFinalDecisionFormFields = new HashMap<>();
        makeFinalDecisionFormFields.put(FINAL_DECISION, makeFinalDecisionDto.getFinalDecision() != null && makeFinalDecisionDto.getFinalDecision());
        this.formService.submitTaskForm(task.getId(), makeFinalDecisionFormFields);
        log.info("Submitted make final decision form with following fields: {}", makeFinalDecisionFormFields);
    }

    private String getProcessInstanceId(final Long scientificPaperId) {
        Assert.notNull(scientificPaperId, "Scientific paper id can't be null!");

        final List<VariableInstance> createScientificPaperDtos = this.runtimeService.createVariableInstanceQuery().variableName(SCIENTIFIC_PAPER_DTO).list();

        String processInstanceId = null;
        for (final VariableInstance variableInstance : createScientificPaperDtos) {
            final CreateScientificPaperDto createScientificPaperDto = (CreateScientificPaperDto) SerializationUtils.deserialize(((VariableInstanceEntity) variableInstance).getByteArrayValue());
            if (createScientificPaperDto != null && createScientificPaperDto.getId().equals(scientificPaperId)) {
                processInstanceId = variableInstance.getProcessInstanceId();
                break;
            }
        }

        final ProcessInstance processInstance = this.runtimeService.createProcessInstanceQuery().active().processInstanceId(processInstanceId).singleResult();

        if (processInstance == null)
            throw new ProcessInstanceNotFoundException(processInstanceId);

        return processInstanceId;
    }

    private FormFieldsDto getFormFieldsDto(final String processInstanceId, final String taskDefinitionKey) {
        Assert.notNull(processInstanceId, "Process instance id can't be null!");

        final Task task = this.util.getActiveUserTaskByDefinitionKey(processInstanceId, taskDefinitionKey);

        if (task == null)
            throw new TaskNotFoundException(taskDefinitionKey);

        log.info("Task with definition key {} is retrieved. Assignee: '{}'.", taskDefinitionKey, task.getAssignee());

        final List<FormField> formFields = this.formService.getTaskFormData(task.getId()).getFormFields();

        return FormFieldsDto.builder()
                .processInstanceId(processInstanceId)
                .taskId(task.getId())
                .formFields(formFields)
                .build();
    }
}