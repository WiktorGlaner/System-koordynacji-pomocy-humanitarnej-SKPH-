package org.ioad.spring.resource.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidArgument extends RuntimeException {
    public InvalidArgument() {
    }

    public InvalidArgument(String message) {
        super(message);
    }

    public InvalidArgument(String message, Throwable cause) {
        super(message, cause);
    }

}
