package org.scientificcenter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserNotEnabledException extends RuntimeException {

    private static final long serialVersionUID = 1831022680418128681L;

    public UserNotEnabledException(final String username) {
        super("User with username '".concat(username).concat("' is not enabled yet!"));
    }
}