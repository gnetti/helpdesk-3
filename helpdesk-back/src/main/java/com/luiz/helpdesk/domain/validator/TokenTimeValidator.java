package com.luiz.helpdesk.domain.validator;

import com.luiz.helpdesk.domain.enums.Profile;
import com.luiz.helpdesk.domain.exception.profile.ProfileNotFoundException;
import com.luiz.helpdesk.domain.exception.tokenTime.DuplicateProfileException;
import com.luiz.helpdesk.domain.exception.tokenTime.UnauthorizedAccessException;
import com.luiz.helpdesk.domain.model.TokenTimeProfile;

public class TokenTimeValidator {
    public static void validateRootAccess(boolean isRoot) throws UnauthorizedAccessException {
        if (!isRoot) {
            throw new UnauthorizedAccessException("Access denied: Only ROOT users can access this resource.");
        }
    }

    public static void validateUniqueProfile(boolean exists, Profile profile) throws DuplicateProfileException {
        if (exists) {
            throw new DuplicateProfileException("A record already exists for the profile", profile);
        }
    }

    public static void validateProfileExists(boolean exists, Profile profile) throws ProfileNotFoundException {
        if (!exists) {
            throw new ProfileNotFoundException("TokenTimeProfile not found for profile: " + profile);
        }
    }

    public static void validateTokenTimeProfileNotNull(TokenTimeProfile tokenTimeProfile, Profile profile) throws ProfileNotFoundException {
        if (tokenTimeProfile == null) {
            throw new ProfileNotFoundException("TokenTimeProfile not found for profile: " + profile);
        }
    }

    public static void validateNotRootProfile(Integer profileCode) throws IllegalArgumentException {
        validateProfileCodeNotNull(profileCode);
        if (Profile.ROOT.getCode().equals(profileCode)) {
            throw new IllegalArgumentException("Cannot modify ROOT profile");
        }
    }

    public static void validateProfileCodeNotNull(Integer profileCode) throws IllegalArgumentException {
        if (profileCode == null) {
            throw new IllegalArgumentException("Profile code cannot be null");
        }
    }

    public static void validateValidProfileCode(Integer profileCode) throws IllegalArgumentException {
        validateProfileCodeNotNull(profileCode);
        try {
            Profile.fromCode(profileCode);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid profile code: " + profileCode);
        }
    }
}