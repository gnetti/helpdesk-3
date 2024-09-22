package com.luiz.helpdesk.domain.exception.person;

public class PersonNotFoundException extends PersonException {

    public PersonNotFoundException(String message) {
        super(message);
    }

    public PersonNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
