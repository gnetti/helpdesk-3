package com.luiz.helpdesk.domain.exception.person;

public class InvalidPersonDataException extends PersonException {

    public InvalidPersonDataException(String message) {
        super(message);
    }

    public InvalidPersonDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
