package com.luiz.helpdesk.domain.exception.person;

public class CurrentPasswordMismatchException extends RuntimeException {
    public CurrentPasswordMismatchException(String message) {
        super(message);
    }
}