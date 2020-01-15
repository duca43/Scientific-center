package org.scientificcenter.util;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.task.Task;
import org.scientificcenter.dto.FormFieldsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class Util {

    private final TaskService taskService;
    private final FormService formService;

    @Autowired
    public Util(final TaskService taskService, final FormService formService) {
        this.taskService = taskService;
        this.formService = formService;
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
                .taskId(taskId)
                .formFields(formFields)
                .build();
    }
}
