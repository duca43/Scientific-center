package org.scientificcenter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewPdfDto {

    private String editor;
    private String taskId;
    private String comment;
    private Long correctionTime;
    private Boolean isPaperFormattedWell;
}