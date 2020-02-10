package org.scientificcenter.dto;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CreateScientificPaperDto implements Serializable {

    private static final long serialVersionUID = -3424512689919196479L;

    private Long id;
    private String title;
    private String paperAbstract;
    private String scientificArea;
    private String pdfFile;
    private List<String> keywords;
}