package org.scientificcenter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedTaskAccessException extends RuntimeException {

    private static final long serialVersionUID = 4247694690784021161L;

    public UnauthorizedTaskAccessException(final String user, final String taskId) {
        super("User with username '".concat(user).concat("' has no access to task with id '".concat(taskId).concat("'!")));
    }
}