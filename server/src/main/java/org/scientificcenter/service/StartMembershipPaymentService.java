package org.scientificcenter.service;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.scientificcenter.dto.OrderResponseDto;
import org.scientificcenter.dto.PaymentDto;
import org.scientificcenter.dto.RedirectionResponse;
import org.scientificcenter.model.Magazine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StartMembershipPaymentService implements JavaDelegate {

    private final IdentityService identityService;
    private final MagazineService magazineService;
    private final PaymentService paymentService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final static String MAGAZINE_FIELD_ID = "magazine";
    private final static String MEMBERSHIP_PAYMENT_STARTED_FLAG = "membership_payment_started";

    @Autowired
    public StartMembershipPaymentService(final IdentityService identityService, final MagazineService magazineService, final PaymentService paymentService, final SimpMessagingTemplate simpMessagingTemplate) {
        this.identityService = identityService;
        this.magazineService = magazineService;
        this.paymentService = paymentService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public void execute(final DelegateExecution delegateExecution) {
        log.info("Executing service task named 'Start membership payment'");

        final String username = (String) delegateExecution.getVariable("author");
        final String magazineIdString = (String) delegateExecution.getVariable(MAGAZINE_FIELD_ID);
        final Long magazineId = Long.valueOf(magazineIdString);

        final Magazine magazine = this.magazineService.findById(magazineId);

        if (magazine == null) return;

        final PaymentDto paymentDto = PaymentDto.builder()
                .merchantId(magazine.getMerchantId())
                .merchantName(magazine.getName())
                .item("Magazine membership")
                .description("Membership for magazine named '".concat(magazine.getName()).concat("'"))
                .price(magazine.getMembershipPrice())
                .currency(magazine.getMembershipCurrency())
                .username(username)
                .processInstanceId(delegateExecution.getProcessInstanceId())
                .build();

        log.info("Start membership payment process for user with username '{}' and magazine with id '{}'", username, magazineId);

        final OrderResponseDto orderResponseDto = OrderResponseDto.builder()
                .successFlag(false)
                .build();
        try {
            final RedirectionResponse redirectionResponse = this.paymentService.prepareMembershipPayment(paymentDto);
            orderResponseDto.setSuccessFlag(true);
            orderResponseDto.setRedirectionUrl(redirectionResponse.getRedirectionUrl());
        } catch (final Exception e) {
            orderResponseDto.setMessage("Error while preparing membership payment! Please, try again later.");
        } finally {
            log.info("Membership payment process preparation flag: {}", orderResponseDto.getSuccessFlag());
            delegateExecution.setVariable(MEMBERSHIP_PAYMENT_STARTED_FLAG, orderResponseDto.getSuccessFlag());
            this.simpMessagingTemplate.convertAndSend("/magazine/membership_payment", orderResponseDto);
        }
    }
}