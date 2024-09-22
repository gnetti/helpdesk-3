package com.luiz.helpdesk.domain.exception.profile;

import com.luiz.helpdesk.domain.exception.DomainException;

public class ProfileNotFoundException extends DomainException {

    public ProfileNotFoundException(String message) {
        super(message);
    }

    public ProfileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
