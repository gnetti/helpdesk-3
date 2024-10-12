package com.luiz.helpdesk.domain.exception.tokenTime;

import com.luiz.helpdesk.domain.enums.Profile;

public class DuplicateProfileException extends RuntimeException {

    private final Profile profile;

    public DuplicateProfileException(String message, Profile profile) {
        super(message);
        this.profile = profile;
    }

    public DuplicateProfileException(String message, Profile profile, Throwable cause) {
        super(message, cause);
        this.profile = profile;
    }

    public Profile getProfile() {
        return profile;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " Profile: " + profile;
    }
}