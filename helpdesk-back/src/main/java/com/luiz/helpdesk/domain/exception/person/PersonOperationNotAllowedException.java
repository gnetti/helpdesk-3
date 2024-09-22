package com.luiz.helpdesk.domain.exception.person;

public class PersonOperationNotAllowedException extends PersonException {

    public PersonOperationNotAllowedException(String message) {
        super(message);
    }

    public PersonOperationNotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }
}
