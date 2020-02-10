package org.scientificcenter.service;

import org.scientificcenter.dto.ScientificPaperDto;
import org.scientificcenter.model.ScientificPaper;

import java.util.List;

public interface ScientificPaperService {

    ScientificPaper findById(Long id);

    ScientificPaper save(ScientificPaper scientificPaper);

    List<ScientificPaperDto> findAllByAuthor(String author);
}
