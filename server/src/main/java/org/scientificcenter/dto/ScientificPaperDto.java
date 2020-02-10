package org.scientificcenter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scientificcenter.model.Keyword;
import org.scientificcenter.model.ScientificArea;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScientificPaperDto {

    private Long id;
    private String title;
    private String paperAbstract;
    private String pdfFilePath;
    private Boolean enabled;
    private Boolean chosenCoauthors;
    private Boolean approvedByMainEditor;
    private Boolean pdfFormattedWell;
    private Boolean requestedChanges;
    private ScientificArea scientificArea;
    private Set<Keyword> keywords;
    private List<AddCoauthorDto> coauthorDtos;
    private MagazineDto magazineDto;
}