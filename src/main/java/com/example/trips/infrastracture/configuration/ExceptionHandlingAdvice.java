package com.example.trips.infrastracture.configuration;

import com.example.trips.domain.exception.InternalServerErrorException;
import com.example.trips.domain.exception.NotFoundException;
import com.example.trips.domain.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlingAdvice {
    private static final Logger log = LoggerFactory.getLogger(ExceptionHandlingAdvice.class);
    private static final String UNKNOWN_ERROR = "Unknown error";

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<String> handleException(Exception ex) {
        log.error(UNKNOWN_ERROR, ex);
        return new ResponseEntity<>(UNKNOWN_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ValidationException.class)
    public final ResponseEntity<String> handleValidationException(ValidationException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public final ResponseEntity<String> handleInternalServerErrorException(InternalServerErrorException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public final ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
        return new ResponseEntity<>("You don't have enough permissions to perform such request", HttpStatus.FORBIDDEN);
    }
}
