package com.luiz.helpdesk.infrastructure.adapters.out.persistence.utils;

import com.luiz.helpdesk.domain.enums.Profile;
import com.luiz.helpdesk.domain.exception.profile.ProfileNotFoundException;
import com.luiz.helpdesk.domain.exception.tokenTime.DuplicateProfileException;
import com.luiz.helpdesk.domain.exception.tokenTime.UnauthorizedAccessException;
import com.luiz.helpdesk.domain.model.TokenTimeProfile;
import com.luiz.helpdesk.domain.validator.TokenTimeValidator;
import com.luiz.helpdesk.infrastructure.adapters.in.web.dto.TokenTimeProfileDTO;

import java.util.function.Supplier;

public class TokenTimeUtil {

    public static void validateForCreate(Supplier<Boolean> isRootUser, TokenTimeProfileDTO dto, Supplier<Boolean> profileExists)
            throws UnauthorizedAccessException, IllegalArgumentException, DuplicateProfileException {
        TokenTimeValidator.validateRootAccess(isRootUser.get());
        TokenTimeValidator.validateProfileCodeNotNull(dto.getProfileCode());
        Profile profile = Profile.fromCode(dto.getProfileCode());
        TokenTimeValidator.validateNotRootProfile(dto.getProfileCode());
        TokenTimeValidator.validateUniqueProfile(profileExists.get(), profile);
    }

    public static void validateForUpdate(Supplier<Boolean> isRootUser, Integer profileCode, Supplier<Boolean> profileExists)
            throws UnauthorizedAccessException, IllegalArgumentException, ProfileNotFoundException {
        TokenTimeValidator.validateProfileCodeNotNull(profileCode);
        Profile profile = Profile.fromCode(profileCode);
        TokenTimeValidator.validateRootAccess(isRootUser.get());
        TokenTimeValidator.validateNotRootProfile(profileCode);
        TokenTimeValidator.validateProfileExists(profileExists.get(), profile);
    }

    public static TokenTimeProfileDTO handleUpdate(Integer profileCode, TokenTimeProfileDTO dto,
                                                   TokenTimeProfile updatedProfile) {
        dto.setProfileCode(profileCode);
        return TokenTimeProfileDTO.fromDomainModel(updatedProfile);
    }

    public static void validateRootAccess(Supplier<Boolean> isRootUser) throws UnauthorizedAccessException {
        TokenTimeValidator.validateRootAccess(isRootUser.get());
    }
}