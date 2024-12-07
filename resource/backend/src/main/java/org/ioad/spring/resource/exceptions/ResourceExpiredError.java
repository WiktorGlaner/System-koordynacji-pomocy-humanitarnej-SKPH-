package org.ioad.spring.resource.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class ResourceExpiredError extends RuntimeException {
    public ResourceExpiredError() {
    }

    public ResourceExpiredError(String message) {
        super(message);
    }

    public ResourceExpiredError(String message, Throwable cause) {
        super(message, cause);
    }
}
