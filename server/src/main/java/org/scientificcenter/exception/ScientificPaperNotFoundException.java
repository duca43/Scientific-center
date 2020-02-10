package org.scientificcenter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ScientificPaperNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 6902510921323832256L;

    public ScientificPaperNotFoundException(final Long id) {
        super("Scientific paper with id '".concat(String.valueOf(id)).concat("' is not found!"));
    }
}