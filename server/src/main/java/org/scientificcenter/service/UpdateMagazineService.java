package org.scientificcenter.service;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.scientificcenter.dto.MembershipPriceDto;
import org.scientificcenter.model.Magazine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UpdateMagazineService implements JavaDelegate {

    private static final String MEMBERSHIP_PRICE_DTO = "membershipPriceDto";
    private final MagazineService magazineService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public UpdateMagazineService(final MagazineService magazineService, final SimpMessagingTemplate simpMessagingTemplate) {
        this.magazineService = magazineService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public void execute(final DelegateExecution delegateExecution) {

        final MembershipPriceDto membershipPriceDto = (MembershipPriceDto) delegateExecution.getVariable(MEMBERSHIP_PRICE_DTO);
        log.info("Retrieved execution variable 'membershipPriceDto' -> {}", membershipPriceDto);

        final Magazine magazine = this.magazineService.findByIssn(membershipPriceDto.getIssn());
        magazine.setMembershipPrice(membershipPriceDto.getPrice());
        magazine.setMembershipCurrency(membershipPriceDto.getCurrency());
        this.magazineService.save(magazine);

        log.info("Added membership price for magazine with issn '{}' and name '{}'", magazine.getIssn(), magazine.getName());

        this.simpMessagingTemplate.convertAndSend("/magazine/membership_price",
                "You have set membership price successfully for magazine with issn'".concat(magazine.getIssn()).concat("'! Next, you have to assign editors and reviewers."));
    }
}