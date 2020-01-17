package org.scientificcenter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TaskNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 470720748002241649L;

    public TaskNotFoundException(final String taskDefinitionKey) {
        super("No active task with definition key '".concat(taskDefinitionKey).concat("' is found!"));
    }
}