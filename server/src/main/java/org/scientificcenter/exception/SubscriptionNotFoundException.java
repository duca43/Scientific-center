package org.scientificcenter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SubscriptionNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 3915220386629652145L;

    public SubscriptionNotFoundException(final Long id) {
        super("Subscription with id '".concat(String.valueOf(id)).concat("' is not found!"));
    }

    public SubscriptionNotFoundException(final String subscriptionId) {
        super("Subscription with subscription id '".concat(subscriptionId).concat("' is not found!"));
    }
}