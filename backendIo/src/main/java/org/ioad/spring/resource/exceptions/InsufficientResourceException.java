package org.ioad.spring.resource.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InsufficientResourceException extends RuntimeException {
    public InsufficientResourceException() {
    }

    public InsufficientResourceException(String message) {
        super(message);
    }

    public InsufficientResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
