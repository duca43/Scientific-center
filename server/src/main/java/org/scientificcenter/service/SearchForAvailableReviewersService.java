package org.scientificcenter.service;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.scientificcenter.dto.CreateScientificPaperDto;
import org.scientificcenter.model.ScientificArea;
import org.scientificcenter.model.ScientificPaper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SearchForAvailableReviewersService implements JavaDelegate {

    private final ScientificPaperService scientificPaperService;
    private static final String SCIENTIFIC_PAPER_DTO = "scientificPaperDto";
    private static final String ARE_THERE_ANY_AVAILABLE_REVIEWER = "areThereAnyAvailableReviewer";
    private static final String REVIEWERS = "reviewers";

    @Autowired
    public SearchForAvailableReviewersService(final ScientificPaperService scientificPaperService) {
        this.scientificPaperService = scientificPaperService;
    }

    @Override
    public void execute(final DelegateExecution delegateExecution) {

        final List<String> reviewers = (List<String>) delegateExecution.getVariable(REVIEWERS);

        if (reviewers != null) {
            log.info("Executing service named 'Search for rest of available reviewers'");
        } else {
            log.info("Executing service named 'Search for available reviewers'");
        }

        final CreateScientificPaperDto createScientificPaperDto = (CreateScientificPaperDto) delegateExecution.getVariable(SCIENTIFIC_PAPER_DTO);
        log.info("Retrieved execution variable scientificPaperDto -> {}", createScientificPaperDto);

        final ScientificPaper scientificPaper = this.scientificPaperService.findById(createScientificPaperDto.getId());
        final ScientificArea scientificArea = scientificPaper.getScientificArea();

        log.info("Searching for available reviewers for scientific area '{}' in scientific paper with id '{}'", scientificArea.getName(), scientificPaper.getId());

        final boolean areThereAnyAvailableReviewer;
        if (reviewers != null) {
            areThereAnyAvailableReviewer = scientificPaper.getMagazine()
                    .getReviewers()
                    .stream()
                    .anyMatch(user -> user.getScientificAreas().contains(scientificArea) && !reviewers.contains(user.getUsername()));
        } else {
            areThereAnyAvailableReviewer = scientificPaper.getMagazine()
                    .getReviewers()
                    .stream()
                    .anyMatch(user -> user.getScientificAreas().contains(scientificArea));
        }

        delegateExecution.setVariable(ARE_THERE_ANY_AVAILABLE_REVIEWER, areThereAnyAvailableReviewer);
        log.info("Found available reviewers flag: {}", areThereAnyAvailableReviewer);
    }
}