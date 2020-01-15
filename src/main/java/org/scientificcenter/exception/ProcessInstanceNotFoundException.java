package org.scientificcenter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProcessInstanceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 3459560420338356284L;

    public ProcessInstanceNotFoundException(final String processInstanceId) {
        super("No active process instance with id '".concat(processInstanceId).concat("' is found!"));
    }
}