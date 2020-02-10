package org.scientificcenter.service;

import org.scientificcenter.dto.MembershipPaymentCompleteDto;
import org.scientificcenter.dto.PaymentCompleteDto;
import org.scientificcenter.dto.PaymentDto;
import org.scientificcenter.dto.RedirectionResponse;

public interface PaymentService {

    RedirectionResponse prepareMembershipPayment(final PaymentDto paymentDto);

    PaymentCompleteDto completePayment(MembershipPaymentCompleteDto membershipPaymentCompleteDto);

    void updatePaymentStatus();
}