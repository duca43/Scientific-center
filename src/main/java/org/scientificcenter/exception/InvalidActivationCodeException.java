package org.scientificcenter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidActivationCodeException extends RuntimeException {

    private static final long serialVersionUID = 8773142606345318140L;

    public InvalidActivationCodeException() {
        super("Your activation token is invalid or it has expired!");
    }
}