package org.scientificcenter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserAlreadyVerifiedException extends RuntimeException {

    private static final long serialVersionUID = 1426419092257669933L;

    public UserAlreadyVerifiedException(final String username) {
        super("User with username '".concat(username).concat("' is already verified!"));
    }
}