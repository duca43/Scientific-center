package org.scientificcenter.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.VariableInstance;
import org.modelmapper.ModelMapper;
import org.scientificcenter.dto.*;
import org.scientificcenter.exception.*;
import org.scientificcenter.model.Magazine;
import org.scientificcenter.model.MerchantOrderStatus;
import org.scientificcenter.model.Payment;
import org.scientificcenter.model.User;
import org.scientificcenter.repository.PaymentRepository;
import org.scientificcenter.service.MagazineService;
import org.scientificcenter.service.PaymentService;
import org.scientificcenter.service.UserService;
import org.scientificcenter.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private static final String MERCHANT_ORDER_ID = "merchant_order_id";
    private final static String MEMBERSHIP_PAYMENT_COMPLETED_FLAG = "membership_payment_completed";
    private final MagazineService magazineService;
    private final UserService userService;
    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;
    private final Util util;
    private final RuntimeService runtimeService;
    private final ModelMapper modelMapper;

    @Autowired
    public PaymentServiceImpl(final MagazineService magazineService, final UserService userService, final PaymentRepository paymentRepository, final RestTemplate restTemplate, final Util util, final RuntimeService runtimeService, final ModelMapper modelMapper) {
        this.magazineService = magazineService;
        this.userService = userService;
        this.paymentRepository = paymentRepository;
        this.restTemplate = restTemplate;
        this.util = util;
        this.runtimeService = runtimeService;
        this.modelMapper = modelMapper;
    }

    @Override
    public RedirectionResponse prepareMembershipPayment(final PaymentDto paymentDto) {

        final Magazine magazine = this.magazineService.findByMerchantId(paymentDto.getMerchantId());

        if (magazine == null) {
            log.error("Magazine with merchant id '{}' does not exist", paymentDto.getMerchantId());
            throw new MagazineNotFoundException(paymentDto.getMerchantId());
        }

        final User user = this.userService.findByUsername(paymentDto.getUsername());

        if (user == null) {
            log.error("User with username '{}' does not exist", paymentDto.getUsername());
            throw new UserNotFoundException(paymentDto.getUsername());
        }

        paymentDto.setReturnUrl(Util.HTTP_PREFIX.concat(this.util.FRONTEND_ADDRESS).concat(":").concat(this.util.FRONTEND_PORT).concat("/my_profile"));
        final RedirectionResponse redirectionResponse = this.preparePayment(paymentDto);

        if (redirectionResponse != null) {
            final Payment membershipPayment = Payment.builder()
                    .merchantOrderId(redirectionResponse.getId())
                    .status(MerchantOrderStatus.IN_PROGRESS)
                    .user(user)
                    .magazine(magazine)
                    .build();
            this.paymentRepository.save(membershipPayment);

            this.runtimeService.setVariable(paymentDto.getProcessInstanceId(), MERCHANT_ORDER_ID, redirectionResponse.getId());
        }

        return redirectionResponse;
    }

    @Override
    public PaymentCompleteDto completePayment(final MembershipPaymentCompleteDto membershipPaymentCompleteDto) {
        Assert.notNull(membershipPaymentCompleteDto, "Membership payment complete object can't be null!");
        Assert.notNull(membershipPaymentCompleteDto.getMerchantOrderId(), "Merchant order id can't be null!");
        Assert.notNull(membershipPaymentCompleteDto.getAuthorUsername(), "Author username can't be null!");

        log.info("Completing payment with merchant order id '{}'", membershipPaymentCompleteDto.getMerchantOrderId());

        final Payment payment = this.paymentRepository.findByMerchantOrderId(membershipPaymentCompleteDto.getMerchantOrderId());

        if (payment == null) {
            log.error("Payment with merchant order id '{}' does not exist", membershipPaymentCompleteDto.getMerchantOrderId());
            throw new PaymentNotFoundException(membershipPaymentCompleteDto.getMerchantOrderId());
        }

        final User author = this.userService.findByUsername(membershipPaymentCompleteDto.getAuthorUsername());

        if (author == null || !payment.getUser().getId().equals(author.getId())) {
            log.error("Author with username '{}' is not involved in payment with merchant order id '{}'", membershipPaymentCompleteDto.getAuthorUsername(), membershipPaymentCompleteDto.getMerchantOrderId());
            throw new NotInvolvedInPaymentException(membershipPaymentCompleteDto.getMerchantOrderId());
        }

        if (payment.getStatus().equals(MerchantOrderStatus.FINISHED)) {
            return PaymentCompleteDto.builder()
                    .flag(false)
                    .message("Payment is already completed!")
                    .build();
        }

        log.info("Call payment gateway service to check if payment with merchant order id '{}' is completed", membershipPaymentCompleteDto.getMerchantOrderId());
        final MerchantOrderStatus merchantOrderStatus = this.restTemplate.getForEntity(Util.HTTPS_PREFIX.concat(this.util.SERVER_ADDRESS).concat(":").concat(this.util.GATEWAY_PORT).concat("/order_status/").concat(membershipPaymentCompleteDto.getMerchantOrderId()), MerchantOrderStatus.class).getBody();
        log.info("Retrieved following response for checking payment with merchant order id '{}' from payment gateway service: status - {}", membershipPaymentCompleteDto.getMerchantOrderId(), merchantOrderStatus);

        if (merchantOrderStatus == null) {
            log.error("Response for checking payment with merchant order id '{}' from payment gateway is not retrieved properly", membershipPaymentCompleteDto.getMerchantOrderId());
            return PaymentCompleteDto.builder()
                    .flag(false)
                    .message("Problem in communication with external payment system. Please, try again later.")
                    .build();
        }

        payment.setStatus(merchantOrderStatus);
        this.paymentRepository.save(payment);
        log.info("Status of payment with merchant order id '{}' is updated to: {}", membershipPaymentCompleteDto.getMerchantOrderId(), merchantOrderStatus.name());

        final List<VariableInstance> merchantOrderIdInstances = this.runtimeService.createVariableInstanceQuery().variableName(MERCHANT_ORDER_ID).list();

        String processInstanceId = null;
        for (final VariableInstance variableInstance : merchantOrderIdInstances) {
            if (variableInstance != null && variableInstance.getValue().equals(membershipPaymentCompleteDto.getMerchantOrderId())) {
                processInstanceId = variableInstance.getProcessInstanceId();
                break;
            }
        }

        final ProcessInstance processInstance = this.runtimeService.createProcessInstanceQuery().active().processInstanceId(processInstanceId).singleResult();

        if (processInstance == null)
            throw new ProcessInstanceNotFoundException(processInstanceId);

        this.runtimeService.setVariable(processInstanceId, MEMBERSHIP_PAYMENT_COMPLETED_FLAG, merchantOrderStatus.equals(MerchantOrderStatus.FINISHED));

        return PaymentCompleteDto.builder()
                .flag(merchantOrderStatus.equals(MerchantOrderStatus.FINISHED))
                .message("Payment has completed successfully!")
                .processInstanceId(merchantOrderStatus.equals(MerchantOrderStatus.FINISHED) ? processInstanceId : null)
                .build();
    }

    @Override
//    @Scheduled(initialDelayString = "${scientific-center.scheduling-initial-delay}", fixedDelayString = "${scientific-center.scheduling-fixed-delay}")
    public void updatePaymentStatus() {
        log.info("Checking payment transactions with status IN_PROGRESS...");

        final List<Payment> payments = this.paymentRepository.findAllByStatus(MerchantOrderStatus.IN_PROGRESS);
        payments.forEach(payment -> {

            log.info("Sending request to payment gateway service to retrieve status of payment with merchant order id {}", payment.getMerchantOrderId());
            final MerchantOrderStatus merchantOrderStatus;
            try {
                merchantOrderStatus = this.restTemplate.getForEntity(Util.HTTPS_PREFIX.concat(this.util.SERVER_ADDRESS).concat(":").concat(this.util.GATEWAY_PORT).concat("/order_status/").concat(payment.getMerchantOrderId()), MerchantOrderStatus.class).getBody();
            } catch (final RestClientException e) {
                return;
            }
            log.info("Response from payment gateway service for payment with payment id {}: status - {}", payment.getMerchantOrderId(), merchantOrderStatus);

            if (merchantOrderStatus == null) return;

            if (payment.getStatus() != merchantOrderStatus) {
                log.info("Update status of payment with merchant order id '{}' from IN_PROGRESS to {}", payment.getMerchantOrderId(), merchantOrderStatus.name());
                payment.setStatus(merchantOrderStatus);
                this.paymentRepository.save(payment);
                log.info("Status of payment with merchant order id '{}' is successfully updated from IN_PROGRESS to {}", payment.getMerchantOrderId(), merchantOrderStatus.name());
            }
        });

        log.info("Checking payment transactions with status IN_PROGRESS is completed...");
    }

    private RedirectionResponse preparePayment(final PaymentDto paymentDto) {
        log.info("Begin payment process for user with username '{}' and merchant with id '{}",
                paymentDto.getUsername(),
                paymentDto.getMerchantId());

        final PaymentRequest paymentRequest = this.modelMapper.map(paymentDto, PaymentRequest.class);

        log.info("Call payment gateway service to prepare payment");
        final RedirectionResponse redirectionResponse = this.restTemplate.postForEntity(Util.HTTPS_PREFIX.concat(this.util.SERVER_ADDRESS).concat(":").concat(this.util.GATEWAY_PORT).concat("/prepare"), paymentRequest, RedirectionResponse.class).getBody();
        if (redirectionResponse == null || redirectionResponse.getId() == null || redirectionResponse.getRedirectionUrl() == null) {
            log.error("Response for payment for user with username '{}' and magazine with merchant id '{}' from payment gateway is not retrieved properly", paymentDto.getUsername(), paymentDto.getMerchantId());
            return null;
        }

        log.info("Retrieved following response for subscription for user with username '{}' and magazine with merchant id '{}' from payment gateway service: id - {}, redirection url - {}", paymentDto.getUsername(), paymentDto.getMerchantId(), redirectionResponse.getId(), redirectionResponse.getRedirectionUrl());
        return redirectionResponse;
    }
}