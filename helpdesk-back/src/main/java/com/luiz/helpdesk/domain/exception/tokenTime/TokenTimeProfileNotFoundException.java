package com.luiz.helpdesk.domain.exception.tokenTime;

public class TokenTimeProfileNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TokenTimeProfileNotFoundException(String message) {
        super(message);
    }

    public TokenTimeProfileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}