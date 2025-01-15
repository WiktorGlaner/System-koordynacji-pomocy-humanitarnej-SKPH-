package org.ioad.spring.resource.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExcepHandler {

    @ExceptionHandler(InvalidArgument.class)
    public ResponseEntity<ErrorResponse> handleInvalidArgument(InvalidArgument ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "error",
                ex.getMessage(),
                "400_INVALID_ARGUMENT"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFound ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "error",
                ex.getMessage(),
                "404_NOT_FOUND"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceExpiredError.class)
    public ResponseEntity<ErrorResponse> handleResourceExpiredError(ResourceExpiredError ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "error",
                ex.getMessage(),
                "410_RESOURCE_EXPIRED"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.GONE);
    }

    @ExceptionHandler(ResourceDamagedError.class)
    public ResponseEntity<ErrorResponse> handleResourceDamagedError(ResourceDamagedError ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "error",
                ex.getMessage(),
                "422_RESOURCE_DAMAGED"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(InsufficientResourceException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientResource(InsufficientResourceException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "error",
                ex.getMessage(),
                "409_INSUFFICIENT_RESOURCES"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceAssignmentNotFound.class)
    public ResponseEntity<ErrorResponse> handleResourceAssignmentNotFound(ResourceAssignmentNotFound ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "error",
                ex.getMessage(),
                "404_RESOURCE_ASSIGNMENT_NOT_FOUND"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}