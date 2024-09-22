package com.luiz.helpdesk.domain.exception.profile;

import com.luiz.helpdesk.domain.exception.DomainException;

public class InvalidProfileException extends DomainException {

    public InvalidProfileException(String message) {
        super(message);
    }

    public InvalidProfileException(String message, Throwable cause) {
        super(message, cause);
    }
}
