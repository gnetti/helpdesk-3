package com.luiz.helpdesk.domain.validator;

import com.luiz.helpdesk.domain.exception.profile.InvalidProfileException;

public class ProfileValidator {

    public static void validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new InvalidProfileException("Profile description cannot be null or empty", new IllegalArgumentException("Empty or null description"));
        }
    }

    public static void validateCode(Integer code) {
        if (code == null) {
            throw new InvalidProfileException("Profile code cannot be null", new IllegalArgumentException("Null code"));
        }
    }
}
