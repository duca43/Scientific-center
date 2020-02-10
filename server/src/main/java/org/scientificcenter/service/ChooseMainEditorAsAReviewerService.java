package org.scientificcenter.service;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.scientificcenter.dto.CreateScientificPaperDto;
import org.scientificcenter.model.ScientificPaper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;

@Service
@Slf4j
public class ChooseMainEditorAsAReviewerService implements JavaDelegate {

    private final ScientificPaperService scientificPaperService;
    private final UserService userService;
    private static final String SCIENTIFIC_PAPER_DTO = "scientificPaperDto";
    private static final String REVIEWERS_FIELD_ID = "reviewers";

    @Autowired
    public ChooseMainEditorAsAReviewerService(final ScientificPaperService scientificPaperService, final UserService userService) {
        this.scientificPaperService = scientificPaperService;
        this.userService = userService;
    }

    @Override
    public void execute(final DelegateExecution delegateExecution) {
        log.info("Executing service named 'Choose main editor as a reviewer'");

        final CreateScientificPaperDto createScientificPaperDto = (CreateScientificPaperDto) delegateExecution.getVariable(SCIENTIFIC_PAPER_DTO);
        log.info("Retrieved execution variable scientificPaperDto -> {}", createScientificPaperDto);

        final ScientificPaper scientificPaper = this.scientificPaperService.findById(createScientificPaperDto.getId());
        final String mainEditorUsername = (String) delegateExecution.getVariable("main_editor");

        log.info("Setting main editor with username '{}' as a reviewer for scientific paper with id '{}'", mainEditorUsername, scientificPaper.getId());

        scientificPaper.setReviewers(new HashSet<>(Collections.singletonList(this.userService.findByUsername(mainEditorUsername))));
        this.scientificPaperService.save(scientificPaper);

        delegateExecution.setVariable(REVIEWERS_FIELD_ID, Collections.singletonList(mainEditorUsername));
        delegateExecution.setVariable("reviewsDeadline", 1L);
    }
}