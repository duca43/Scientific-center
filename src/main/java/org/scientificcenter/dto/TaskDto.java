package org.scientificcenter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.camunda.bpm.engine.task.Task;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDto {

    private String id;
    private String taskDefinitionKey;
    private String name;
    private Date createTime;

    public TaskDto(final Task task) {
        this.id = task.getId();
        this.taskDefinitionKey = task.getTaskDefinitionKey();
        this.name = task.getName();
        this.createTime = task.getCreateTime();
    }
}