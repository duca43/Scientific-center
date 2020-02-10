package org.scientificcenter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PaymentNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 3915220386629652145L;

    public PaymentNotFoundException(final Long id) {
        super("Payment with id '".concat(String.valueOf(id)).concat("' is not found!"));
    }

    public PaymentNotFoundException(final String merchantOrderId) {
        super("Payment with merchant order id '".concat(merchantOrderId).concat("' is not found!"));
    }
}