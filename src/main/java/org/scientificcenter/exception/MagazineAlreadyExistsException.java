package org.scientificcenter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MagazineAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = -5295033525862231689L;

    public MagazineAlreadyExistsException(final String issn) {
        super("Magazine with issn '".concat(issn).concat("' already exists!"));
    }
}