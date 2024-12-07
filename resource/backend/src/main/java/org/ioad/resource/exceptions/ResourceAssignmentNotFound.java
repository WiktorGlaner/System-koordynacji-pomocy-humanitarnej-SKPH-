package org.ioad.resource.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceAssignmentNotFound extends RuntimeException {
    public ResourceAssignmentNotFound() {
    }

    public ResourceAssignmentNotFound(String message) {
        super(message);
    }

    public ResourceAssignmentNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}
