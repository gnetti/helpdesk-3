package com.luiz.helpdesk.domain.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.lang.NonNull;
import org.springframework.web.ErrorResponse;

import java.time.Instant;
import java.util.Objects;

public class ConcreteErrorResponse implements ErrorResponse {
    private final HttpStatus status;
    private final String error;
    private final String message;
    private final Instant timestamp;
    private final HttpHeaders headers;
    private final ProblemDetail body;

    public ConcreteErrorResponse(HttpStatus status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = Instant.now();
        this.headers = new HttpHeaders();
        this.body = ProblemDetail.forStatusAndDetail(status, message);
        this.body.setTitle(error);
        this.body.setProperty("timestamp", this.timestamp);
    }

    @Override
    @NonNull
    public HttpStatusCode getStatusCode() {
        return status;
    }

    @Override
    @NonNull
    public HttpHeaders getHeaders() {
        return headers;
    }

    @Override
    @NonNull
    public ProblemDetail getBody() {
        return body;
    }

    @NonNull
    public HttpStatus getStatus() {
        return status;
    }

    @NonNull
    public String getError() {
        return error;
    }

    @NonNull
    public String getMessage() {
        return message;
    }

    @NonNull
    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    @NonNull
    public String getDetailMessageCode() {
        return ErrorResponse.super.getDetailMessageCode();
    }

    @Override
    @NonNull
    public Object[] getDetailMessageArguments() {
        return Objects.requireNonNull(ErrorResponse.super.getDetailMessageArguments());
    }

    @Override
    @NonNull
    public String getTitleMessageCode() {
        return ErrorResponse.super.getTitleMessageCode();
    }

    @Override
    @NonNull
    public String getTypeMessageCode() {
        return ErrorResponse.super.getTypeMessageCode();
    }
}