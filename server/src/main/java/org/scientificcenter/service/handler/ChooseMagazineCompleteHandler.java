package org.scientificcenter.service.handler;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.scientificcenter.dto.ValidationDto;
import org.scientificcenter.model.Magazine;
import org.scientificcenter.model.MerchantOrderStatus;
import org.scientificcenter.model.Payment;
import org.scientificcenter.repository.PaymentRepository;
import org.scientificcenter.service.MagazineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ChooseMagazineCompleteHandler implements TaskListener {

    private final MagazineService magazineService;
    private final PaymentRepository paymentRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final static String MAGAZINE_FIELD_ID = "magazine";
    private final static String ACTIVE_MEMBERSHIP = "active_membership";
    private final static String SELECTED_MAGAZINE_PAYMENT = "selected_magazine_payment";

    @Autowired
    public ChooseMagazineCompleteHandler(final MagazineService magazineService, final PaymentRepository paymentRepository, final SimpMessagingTemplate simpMessagingTemplate) {
        this.magazineService = magazineService;
        this.paymentRepository = paymentRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public void notify(final DelegateTask delegateTask) {
        log.info("{} task - complete listener", delegateTask.getName());

        final String magazineIdString = (String) delegateTask.getExecution().getVariable(MAGAZINE_FIELD_ID);

        if (magazineIdString == null) {
            log.error("Magazine is not chosen correctly");
            final ValidationDto validationDto = ValidationDto.builder()
                    .valid(false)
                    .errorMessage("Magazine is not chosen correctly. Please, try again later.")
                    .build();
            this.simpMessagingTemplate.convertAndSend("/scientific_paper/magazine_chosen", validationDto);
            return;
        }

        final Long magazineId = Long.valueOf(magazineIdString);
        log.info("Check payment type and subscription for magazine with id '{}' and user with username '{}'", magazineId, delegateTask.getAssignee());

        final Magazine magazine = this.magazineService.findById(magazineId);
        final String paymentType = magazine.getPayment().getType();
        delegateTask.getExecution().setVariable(SELECTED_MAGAZINE_PAYMENT, paymentType);
        log.info("Payment type for selected magazine: {}", paymentType);

        final Payment payment = this.paymentRepository.findByUser_UsernameAndMagazine_Id(delegateTask.getAssignee(), magazineId);

        final boolean activeSubscriptionFlag = payment != null && payment.getStatus().equals(MerchantOrderStatus.FINISHED);
        delegateTask.getExecution().setVariable(ACTIVE_MEMBERSHIP, activeSubscriptionFlag);
        log.info("Active membership flag for selected magazine: {}", activeSubscriptionFlag);

        this.simpMessagingTemplate.convertAndSend("/scientific_paper/magazine_chosen", ValidationDto.builder().valid(true).build());
    }
}