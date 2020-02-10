package org.scientificcenter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MakeADecisionDto {

    private String taskId;
    private String decision;
    private Long correctionsDeadline;
}