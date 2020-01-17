package org.scientificcenter.util;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.task.Task;
import org.scientificcenter.dto.FormFieldsDto;
import org.scientificcenter.dto.UserEnumFormFieldDto;
import org.scientificcenter.model.Authority;
import org.scientificcenter.model.Magazine;
import org.scientificcenter.model.ScientificArea;
import org.scientificcenter.model.User;
import org.scientificcenter.service.AuthorityService;
import org.scientificcenter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class Util {

    private final TaskService taskService;
    private final FormService formService;
    private final UserService userService;
    private final AuthorityService authorityService;

    @Autowired
    public Util(final TaskService taskService, final FormService formService, final UserService userService, final AuthorityService authorityService) {
        this.taskService = taskService;
        this.formService = formService;
        this.userService = userService;
        this.authorityService = authorityService;
    }

    public Task getActiveUserTaskByDefinitionKey(final String processInstanceId, final String taskDefinitionKey) {
        return this.taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .taskDefinitionKey(taskDefinitionKey)
                .active()
                .list()
                .stream()
                .findFirst().orElse(null);
    }

    public <T> Map<String, Object> transformObjectToMap(final T object) {
        final Map<String, Object> map = new HashMap<>();
        Arrays.stream(object.getClass().getDeclaredFields()).forEach(field -> {
            try {
                field.setAccessible(true);
                map.put(field.getName(), field.get(object));
            } catch (final IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        return map;
    }

    public FormFieldsDto getTaskFormFields(final String taskId) {
        Assert.notNull(taskId, "Task id can't be null!");
        final List<FormField> formFields = this.formService.getTaskFormData(taskId).getFormFields();
        Util.log.info("Retrieving form fields of task with id '{}'", taskId);
        return FormFieldsDto.builder()
                .processInstanceId(this.taskService.createTaskQuery().taskId(taskId).active().singleResult().getProcessInstanceId())
                .taskId(taskId)
                .formFields(formFields)
                .build();
    }

    public void fillMultipleEnumFormFields(final Magazine magazine, final List<FormField> formFields, final UserEnumFormFieldDto... userEnumFormFieldDto) {
        if (userEnumFormFieldDto == null || userEnumFormFieldDto.length == 0) return;

        final Map<String, String> userEnumFormFieldDtoMap = Arrays.stream(userEnumFormFieldDto)
                .collect(Collectors.toMap(UserEnumFormFieldDto::getId, UserEnumFormFieldDto::getRole));
        formFields.forEach(formField -> {
            if (formField.getType() instanceof MultivaluedEnumFormType && userEnumFormFieldDtoMap.containsKey(formField.getId())) {
                Util.log.info(formField.getId() + " " + userEnumFormFieldDtoMap.get(formField.getId()));
                this.fillMultipleEnumFormField(magazine, formField, userEnumFormFieldDtoMap.get(formField.getId()), formField.getId());
            }
        });
    }

    private void fillMultipleEnumFormField(final Magazine magazine, final FormField formField, final String role, final String field) {
        final List<ScientificArea> magazineScientificAreas = new ArrayList<>(magazine.getScientificAreas());
        final Authority authority = this.authorityService.findByName(role);
        final Map<String, String> users = this.userService
                .findAllByUsernameNotAndAuthoritiesContainsAndScientificAreasIsIn(magazine.getMainEditor().getUsername(), authority, magazineScientificAreas)
                .stream()
                .collect(Collectors.toMap(user -> user.getId().toString(), User::getUsername));
        ((MultivaluedEnumFormType) formField.getType()).getValues().clear();
        ((MultivaluedEnumFormType) formField.getType()).getValues().putAll(users);
        Util.log.info("Added {} users into multivalued enum form field '{}'", users.values().size(), field);
    }
}
