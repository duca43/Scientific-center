package org.scientificcenter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class NotInvolvedInPaymentException extends RuntimeException {

    private static final long serialVersionUID = 3548373095377637618L;

    public NotInvolvedInPaymentException(final String merchantOrderId) {
        super("You are not involved in payment with merchant order id '".concat(merchantOrderId).concat("'!"));
    }
}