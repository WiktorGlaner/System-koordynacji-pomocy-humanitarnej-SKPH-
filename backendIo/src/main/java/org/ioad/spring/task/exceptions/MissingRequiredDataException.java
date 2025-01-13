package org.ioad.spring.task.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MissingRequiredDataException extends RuntimeException {
    public MissingRequiredDataException(String message) {
        super(message);
    }
}

