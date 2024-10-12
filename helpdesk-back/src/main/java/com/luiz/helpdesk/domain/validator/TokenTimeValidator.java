package com.luiz.helpdesk.domain.validator;

import com.luiz.helpdesk.domain.enums.Profile;
import com.luiz.helpdesk.domain.exception.profile.ProfileNotFoundException;
import com.luiz.helpdesk.domain.exception.tokenTime.DuplicateProfileException;
import com.luiz.helpdesk.domain.exception.tokenTime.UnauthorizedAccessException;
import com.luiz.helpdesk.domain.model.TokenTimeProfile;

public class TokenTimeValidator {
    public static void validateRootAccess(boolean isRoot) {

        if (!isRoot) throw new UnauthorizedAccessException("Access denied: Only ROOT users can access this resource.");
    }

    public static void validateUniqueProfile(boolean exists, Profile profile) {
        if (exists) throw new DuplicateProfileException("A record already exists for the profile", profile);
    }

    public static void validateProfileExists(boolean exists, Profile profile) {
        if (!exists) throw new ProfileNotFoundException("TokenTimeProfile not found for profile: " + profile);
    }

    public static void validateTokenTimeProfileNotNull(TokenTimeProfile tokenTimeProfile, Profile profile) {
        if (tokenTimeProfile == null)
            throw new ProfileNotFoundException("TokenTimeProfile not found for profile: " + profile);
    }

    public static void validateNotRootProfile(Profile profile) {
        if (profile == Profile.ROOT) throw new IllegalArgumentException("Cannot modify ROOT profile");
    }
}