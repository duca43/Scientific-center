package org.scientificcenter.service;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.scientificcenter.dto.CreateScientificPaperDto;
import org.scientificcenter.dto.ReviewPaperResponseDto;
import org.scientificcenter.model.ScientificPaper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EnableScientificPaperService implements JavaDelegate {

    private final ScientificPaperService scientificPaperService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private static final String SCIENTIFIC_PAPER_DTO = "scientificPaperDto";

    @Autowired
    public EnableScientificPaperService(final ScientificPaperService scientificPaperService, final SimpMessagingTemplate simpMessagingTemplate) {
        this.scientificPaperService = scientificPaperService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public void execute(final DelegateExecution delegateExecution) {
        log.info("Executing service named 'Enable scientific paper'");

        final CreateScientificPaperDto createScientificPaperDto = (CreateScientificPaperDto) delegateExecution.getVariable(SCIENTIFIC_PAPER_DTO);
        log.info("Retrieved execution variable scientificPaperDto -> {}", createScientificPaperDto);

        log.info("Enabling scientific paper with id '{}'", createScientificPaperDto.getId());

        final ScientificPaper scientificPaper = this.scientificPaperService.findById(createScientificPaperDto.getId());

        scientificPaper.setEnabled(true);
        scientificPaper.setPdfFormattedWell(true);
        scientificPaper.setRequestedChanges(false);
        this.scientificPaperService.save(scientificPaper);

        final ReviewPaperResponseDto reviewPaperResponseDto = ReviewPaperResponseDto.builder()
                .scientificPaperId(scientificPaper.getId())
                .approved(true)
                .message("Scientific paper titled " + scientificPaper.getTitle() + " is enabled.")
                .build();

        this.simpMessagingTemplate.convertAndSend("/scientific_paper/enabled", reviewPaperResponseDto);
    }
}