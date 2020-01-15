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
public class MagazineActivationService implements JavaDelegate {

    private static final String MAGAZINE_DTO = "magazineDto";
    private final MagazineService magazineService;

    @Autowired
    public MagazineActivationService(final MagazineService magazineService) {
        this.magazineService = magazineService;
    }

    @Override
    public void execute(final DelegateExecution delegateExecution) {
        final MagazineDto magazineDto = (MagazineDto) delegateExecution.getVariable(MagazineActivationService.MAGAZINE_DTO);
        MagazineActivationService.log.info("Retrieved execution variable 'magazineDto' -> {}", magazineDto);

        final Magazine magazine = this.magazineService.findByIssn(magazineDto.getIssn());
        magazine.setEnabled(true);
        magazine.setRequestedChanges(false);
        this.magazineService.save(magazine);

        MagazineActivationService.log.info("Magazine with issn '{}' and name '{}' is enabled", magazine.getIssn(), magazine.getName());
    }
}