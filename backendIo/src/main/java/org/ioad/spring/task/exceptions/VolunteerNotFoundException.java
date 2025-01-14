package org.ioad.spring.task.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class VolunteerNotFoundException extends RuntimeException {
    public VolunteerNotFoundException() {
    }

    public VolunteerNotFoundException(String message) {
        super(message);
    }

    public VolunteerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}