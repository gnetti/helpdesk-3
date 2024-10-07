package com.luiz.helpdesk.domain.exception;

import com.luiz.helpdesk.domain.exception.person.InvalidPasswordException;
import com.luiz.helpdesk.domain.exception.person.InvalidPersonDataException;
import com.luiz.helpdesk.domain.exception.person.PersonAlreadyExistsException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> body = createErrorBody(HttpStatus.BAD_REQUEST, "Validation Error");

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        body.put("errors", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException() {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "An unexpected error occurred");
    }

    @ExceptionHandler(PersonAlreadyExistsException.class)
    public ResponseEntity<Object> handlePersonAlreadyExistsException(PersonAlreadyExistsException ex) {
        return createErrorResponse(HttpStatus.CONFLICT, "Conflict", ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException() {
        return createErrorResponse(HttpStatus.CONFLICT, "Conflict", "A data integrity constraint has been violated");
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<Object> handleInvalidPasswordException(InvalidPasswordException ex) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
    }

    private ResponseEntity<Object> createErrorResponse(HttpStatus status, String error, String message) {
        Map<String, Object> body = createErrorBody(status, error);
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }

    private Map<String, Object> createErrorBody(HttpStatus status, String error) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        body.put("status", status.value());
        body.put("error", error);
        return body;
    }

    @ExceptionHandler(InvalidPersonDataException.class)
    public ResponseEntity<Object> handleInvalidPersonDataException(InvalidPersonDataException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}