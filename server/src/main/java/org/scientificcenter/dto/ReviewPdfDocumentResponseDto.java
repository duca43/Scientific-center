package org.scientificcenter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewPdfDocumentResponseDto {

    private Long scientificPaperId;
    private Boolean formattedWell;
    private Boolean requestedChanges;
    private String message;
}