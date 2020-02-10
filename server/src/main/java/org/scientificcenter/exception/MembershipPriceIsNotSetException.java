package org.scientificcenter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class MembershipPriceIsNotSetException extends RuntimeException {

    private static final long serialVersionUID = -9081635486961523457L;

    public MembershipPriceIsNotSetException(final String issn) {
        super("Error! You have to set membership price first for magazine with issn '".concat(issn).concat("' to be able to assign editors and reviewers!"));
    }
}