package org.scientificcenter.dto;

import lombok.*;
import org.camunda.bpm.engine.form.FormField;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class FormFieldsDto {

    private String processInstanceId;
    private String taskId;
    private List<FormField> formFields;
    private String token;
}