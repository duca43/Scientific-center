package org.scientificcenter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class NotMainEditorException extends RuntimeException {

    private static final long serialVersionUID = -1350491843012884594L;

    public NotMainEditorException(final Long id, final String username) {
        super("User with username".concat(username).concat(" is not main editor of magazine with id '").concat(String.valueOf(id)).concat("' is not found!"));
    }

    public NotMainEditorException(final String merchantId, final String username) {
        super("User with username".concat(username).concat(" is not main editor of magazine with merchant id '").concat(merchantId).concat("' is not found!"));
    }
}