package org.scientificcenter.controller;

import org.scientificcenter.dto.MembershipPaymentCompleteDto;
import org.scientificcenter.dto.PaymentCompleteDto;
import org.scientificcenter.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(final PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping(value = "/complete", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaymentCompleteDto> completePayment(@RequestBody final MembershipPaymentCompleteDto membershipPaymentCompleteDto) {
        return ResponseEntity.ok(this.paymentService.completePayment(membershipPaymentCompleteDto));
    }
}