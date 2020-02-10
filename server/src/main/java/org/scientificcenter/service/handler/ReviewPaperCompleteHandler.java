package org.scientificcenter.service.handler;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.scientificcenter.dto.CreateScientificPaperDto;
import org.scientificcenter.dto.ReviewPaperResponseDto;
import org.scientificcenter.model.ScientificPaper;
import org.scientificcenter.service.ScientificPaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ReviewPaperCompleteHandler implements TaskListener {

    private final ScientificPaperService scientificPaperService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private static final String SCIENTIFIC_PAPER_DTO = "scientificPaperDto";
    private static final String APPROVE_SCIENTIFIC_PAPER_FLAG = "approveScientificPaper";

    @Autowired
    public ReviewPaperCompleteHandler(final ScientificPaperService scientificPaperService, final SimpMessagingTemplate simpMessagingTemplate) {
        this.scientificPaperService = scientificPaperService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public void notify(final DelegateTask delegateTask) {
        log.info("{} task - complete listener", delegateTask.getName());

        final Boolean approveScientificPaperFlag = (Boolean) delegateTask.getExecution().getVariable(APPROVE_SCIENTIFIC_PAPER_FLAG);
        log.info("Retrieved execution variable approveScientificPaper -> {}", approveScientificPaperFlag);

        final CreateScientificPaperDto createScientificPaperDto = (CreateScientificPaperDto) delegateTask.getExecution().getVariable(SCIENTIFIC_PAPER_DTO);
        log.info("Retrieved execution variable scientificPaperDto -> {}", createScientificPaperDto);

        log.info("Updating scientific paper with id '{}': approved by main editor flag -> {}", createScientificPaperDto.getId(), approveScientificPaperFlag);

        final ScientificPaper scientificPaper = this.scientificPaperService.findById(createScientificPaperDto.getId());

        scientificPaper.setApprovedByMainEditor(approveScientificPaperFlag);
        this.scientificPaperService.save(scientificPaper);

        final ReviewPaperResponseDto reviewPaperResponseDto = ReviewPaperResponseDto.builder()
                .scientificPaperId(scientificPaper.getId())
                .approved(approveScientificPaperFlag)
                .message(approveScientificPaperFlag != null && approveScientificPaperFlag
                        ? "Scientific paper titled '" + scientificPaper.getTitle() + "' is approved."
                        : "Scientific paper titled '" + scientificPaper.getTitle() + "' is rejected.")
                .build();

        this.simpMessagingTemplate.convertAndSend("/scientific_paper/review_paper", reviewPaperResponseDto);
    }
}