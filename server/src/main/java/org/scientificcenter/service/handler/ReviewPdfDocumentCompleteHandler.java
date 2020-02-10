package org.scientificcenter.service.handler;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.scientificcenter.dto.CreateScientificPaperDto;
import org.scientificcenter.dto.ReviewPdfDocumentResponseDto;
import org.scientificcenter.model.ScientificPaper;
import org.scientificcenter.service.ScientificPaperService;
import org.scientificcenter.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ReviewPdfDocumentCompleteHandler implements TaskListener {

    private final ScientificPaperService scientificPaperService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final Util util;
    private static final String SCIENTIFIC_PAPER_DTO = "scientificPaperDto";
    private static final String IS_PAPER_FORMATTED_WELL_FLAG = "isPaperFormattedWell";

    @Autowired
    public ReviewPdfDocumentCompleteHandler(final ScientificPaperService scientificPaperService, final SimpMessagingTemplate simpMessagingTemplate, final Util util) {
        this.scientificPaperService = scientificPaperService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.util = util;
    }

    @Override
    public void notify(final DelegateTask delegateTask) {
        log.info("{} task - complete listener", delegateTask.getName());

        Boolean isPaperFormattedWellFlag = (Boolean) delegateTask.getExecution().getVariable(IS_PAPER_FORMATTED_WELL_FLAG);
        log.info("Retrieved execution variable isPaperFormattedWell -> {}", isPaperFormattedWellFlag);

        final CreateScientificPaperDto createScientificPaperDto = (CreateScientificPaperDto) delegateTask.getExecution().getVariable(SCIENTIFIC_PAPER_DTO);
        log.info("Retrieved execution variable scientificPaperDto -> {}", createScientificPaperDto);

        log.info("Updating scientific paper with id '{}': is paper formatted well flag -> {}", createScientificPaperDto.getId(), isPaperFormattedWellFlag);

        final ScientificPaper scientificPaper = this.scientificPaperService.findById(createScientificPaperDto.getId());

        isPaperFormattedWellFlag = isPaperFormattedWellFlag != null && isPaperFormattedWellFlag;

        scientificPaper.setPdfFormattedWell(isPaperFormattedWellFlag);
        scientificPaper.setRequestedChanges(!isPaperFormattedWellFlag);
        this.scientificPaperService.save(scientificPaper);

        final ReviewPdfDocumentResponseDto reviewPdfDocumentResponseDto = ReviewPdfDocumentResponseDto.builder()
                .scientificPaperId(scientificPaper.getId())
                .formattedWell(isPaperFormattedWellFlag)
                .requestedChanges(!isPaperFormattedWellFlag)
                .message(isPaperFormattedWellFlag ? "PDF document of scientific paper titled '" + scientificPaper.getTitle() + "' is well formatted."
                        : "PDF document of scientific paper titled '" + scientificPaper.getTitle() + "' is formatted badly. Please upload a new version od PDF document.")
                .build();
        this.simpMessagingTemplate.convertAndSend("/scientific_paper/review_pdf_document", reviewPdfDocumentResponseDto);
    }
}