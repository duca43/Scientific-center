package org.scientificcenter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CustomAuthenticationException extends AuthenticationException {

    private static final long serialVersionUID = -2487552793307499828L;

    public CustomAuthenticationException(final String msg, final Throwable t) {
        super(msg, t);
    }
}