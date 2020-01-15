package org.scientificcenter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckReviewerDto {

    private String taskId;
    private String admin;
    private boolean acceptReviewer;
}