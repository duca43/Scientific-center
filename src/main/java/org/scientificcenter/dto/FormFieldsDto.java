package org.scientificcenter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.camunda.bpm.engine.form.FormField;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormFieldsDto {

    private String processInstanceId;
    private String taskId;
    private List<FormField> formFields;
    private String token;
}