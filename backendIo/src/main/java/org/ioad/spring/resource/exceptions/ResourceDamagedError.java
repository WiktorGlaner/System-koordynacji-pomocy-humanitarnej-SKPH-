package org.ioad.spring.resource.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class ResourceDamagedError extends RuntimeException {
    public ResourceDamagedError() {
    }

    public ResourceDamagedError(String message) {
        super(message);
    }

    public ResourceDamagedError(String message, Throwable cause) {
        super(message, cause);
    }
}
