package org.scientificcenter.dto;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class PaperReviewByReviewerDto implements Serializable {

    private static final long serialVersionUID = 7665704631342155097L;
    private String reviewer;
    private String commentAboutPaper;
    private String recommendation;
    private String commentForEditor;
}