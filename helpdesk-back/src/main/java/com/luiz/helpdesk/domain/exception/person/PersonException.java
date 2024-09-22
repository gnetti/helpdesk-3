package com.luiz.helpdesk.domain.exception.person;

public abstract class PersonException extends RuntimeException {

    public PersonException(String message) {
        super(message);
    }

    public PersonException(String message, Throwable cause) {
        super(message, cause);
    }
}
