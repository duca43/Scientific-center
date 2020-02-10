package org.scientificcenter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.camunda.bpm.engine.task.Task;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ReviewPdfDocumentTaskDto extends TaskDto {

    private Boolean afterCorrections;

    public ReviewPdfDocumentTaskDto(final Task task, final Boolean afterCorrections) {
        super(task);
        this.afterCorrections = afterCorrections;
    }
}