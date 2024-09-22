package com.luiz.helpdesk.domain.exception.person;

public class PersonAlreadyExistsException extends PersonException {

    public PersonAlreadyExistsException(String message) {
        super(message);
    }

    public PersonAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
