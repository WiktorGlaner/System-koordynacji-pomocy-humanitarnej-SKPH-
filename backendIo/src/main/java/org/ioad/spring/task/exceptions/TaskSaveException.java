package org.ioad.spring.task.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class TaskSaveException extends RuntimeException {
    public TaskSaveException(String message) {
        super(message);
    }
}

