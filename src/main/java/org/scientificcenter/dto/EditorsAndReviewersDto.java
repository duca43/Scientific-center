package org.scientificcenter.dto;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class EditorsAndReviewersDto implements Serializable {

    private static final long serialVersionUID = 2284618930546759579L;
    private String issn;
    private List<BasicUserInfoDto> editors;
    private List<BasicUserInfoDto> reviewers;
}