package com.luiz.helpdesk.domain.exception.person;

public class IncompletePersonDataException extends RuntimeException {
    public IncompletePersonDataException(String message) {
        super(message);
    }
}
