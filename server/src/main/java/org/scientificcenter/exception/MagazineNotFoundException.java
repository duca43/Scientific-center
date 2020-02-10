package org.scientificcenter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MagazineNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 4133309546506171225L;

    public MagazineNotFoundException(final Long id) {
        super("Magazine with id '".concat(String.valueOf(id)).concat("' is not found!"));
    }

    public MagazineNotFoundException(final String merchantId) {
        super("Magazine with merchant id '".concat(merchantId).concat("' is not found!"));
    }
}