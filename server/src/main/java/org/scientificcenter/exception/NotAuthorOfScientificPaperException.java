package org.scientificcenter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class NotAuthorOfScientificPaperException extends RuntimeException {

    private static final long serialVersionUID = 3333427094907915103L;

    public NotAuthorOfScientificPaperException(final Long scientificPaperId) {
        super("You are not author of scientific paper with id '".concat(String.valueOf(scientificPaperId)).concat("'!"));
    }
}