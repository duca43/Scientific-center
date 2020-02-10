package org.scientificcenter.service.handler;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.scientificcenter.service.ScientificAreasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ReviewPaperCreateHandler implements TaskListener {

    private final ScientificAreasService scientificAreasService;
    private static final String SCIENTIFIC_AREA = "scientificArea";
    private static final String APPROVE_SCIENTIFIC_PAPER_FLAG = "approveScientificPaper";

    @Autowired
    public ReviewPaperCreateHandler(final ScientificAreasService scientificAreasService) {
        this.scientificAreasService = scientificAreasService;
    }

    @Override
    public void notify(final DelegateTask delegateTask) {
        log.info("{} task - create listener", delegateTask.getName());

        delegateTask.getExecution().setVariable(APPROVE_SCIENTIFIC_PAPER_FLAG, false);

        final String scientificAreaIdString = (String) delegateTask.getExecution().getVariable(SCIENTIFIC_AREA);
        log.info("Retrieved execution variable scientificArea -> {}", scientificAreaIdString);

        final long scientificAreaId;
        try {
            scientificAreaId = Long.parseLong(scientificAreaIdString);
        } catch (final NumberFormatException e) {
            return;
        }

        final String scientificAreaName = this.scientificAreasService.findById(scientificAreaId).getName();
        delegateTask.getExecution().setVariable(SCIENTIFIC_AREA, scientificAreaName);
        log.info("Updated execution variable scientificArea from '{}' to '{}'", scientificAreaIdString, scientificAreaName);
    }
}