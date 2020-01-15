package org.scientificcenter.service;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.scientificcenter.dto.MagazineDto;
import org.scientificcenter.model.Magazine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UpdateMagazineStatusService implements JavaDelegate {

    private static final String MAGAZINE_DTO = "magazineDto";
    private final MagazineService magazineService;

    @Autowired
    public UpdateMagazineStatusService(final MagazineService magazineService) {
        this.magazineService = magazineService;
    }

    @Override
    public void execute(final DelegateExecution delegateExecution) {
        final MagazineDto magazineDto = (MagazineDto) delegateExecution.getVariable(UpdateMagazineStatusService.MAGAZINE_DTO);
        UpdateMagazineStatusService.log.info("Retrieved execution variable 'magazineDto' -> {}", magazineDto);

        final Magazine magazine = this.magazineService.findByIssn(magazineDto.getIssn());
        magazine.setRequestedChanges(true);
        this.magazineService.save(magazine);

        UpdateMagazineStatusService.log.info("Magazine with issn '{}' and name '{}' need changes", magazine.getIssn(), magazine.getName());
    }
}