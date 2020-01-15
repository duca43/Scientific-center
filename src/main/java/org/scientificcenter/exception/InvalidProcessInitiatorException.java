package org.scientificcenter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidProcessInitiatorException extends RuntimeException {


    private static final long serialVersionUID = 6905644430598802617L;

    public InvalidProcessInitiatorException(final String authority) {
        super("Process must be started by existing user with '".concat(authority).concat("' authority!"));
    }
}