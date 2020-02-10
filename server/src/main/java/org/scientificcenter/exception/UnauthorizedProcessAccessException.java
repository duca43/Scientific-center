package org.scientificcenter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedProcessAccessException extends RuntimeException {

    private static final long serialVersionUID = 5167481148169062146L;

    public UnauthorizedProcessAccessException(final String user, final String processName) {
        super("User with username '".concat(user).concat("' has no access to process named '".concat(processName).concat("'!")));
    }
}