package org.scientificcenter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -8162472995944892738L;

    public UserNotFoundException(final Long id) {
        super("User with id '".concat(String.valueOf(id)).concat("' is not found!"));
    }

    public UserNotFoundException(final String username) {
        super("User with username '".concat(username).concat("' is not found!"));
    }
}