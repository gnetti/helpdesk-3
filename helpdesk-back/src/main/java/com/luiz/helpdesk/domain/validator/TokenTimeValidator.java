package com.luiz.helpdesk.domain.validator;

import com.luiz.helpdesk.domain.enums.Profile;
import com.luiz.helpdesk.domain.exception.profile.ProfileNotFoundException;
import com.luiz.helpdesk.domain.exception.tokenTime.DuplicateProfileException;
import com.luiz.helpdesk.domain.exception.tokenTime.UnauthorizedAccessException;
import com.luiz.helpdesk.domain.model.TokenTimeProfile;

public class TokenTimeValidator {
    public static void validateRootAccess(boolean isRoot) throws UnauthorizedAccessException {
        if (!isRoot) {
            throw new UnauthorizedAccessException("Acesso negado: Apenas usuários ROOT podem acessar este recurso.");
        }
    }

    public static void validateUniqueProfile(boolean exists, Profile profile) throws DuplicateProfileException {
        if (exists) {
            throw new DuplicateProfileException("Já existe um registro para o perfil", profile);
        }
    }

    public static void validateProfileExists(boolean exists, Profile profile) throws ProfileNotFoundException {
        if (!exists) {
            throw new ProfileNotFoundException("TokenTimeProfile não encontrado para o perfil: " + profile);
        }
    }

    public static void validateTokenTimeProfileNotNull(TokenTimeProfile tokenTimeProfile, Profile profile) throws ProfileNotFoundException {
        if (tokenTimeProfile == null) {
            throw new ProfileNotFoundException("TokenTimeProfile não encontrado para o perfil: " + profile);
        }
    }

    public static void validateNotRootProfile(Integer profileCode) throws IllegalArgumentException {
        validateProfileCodeNotNull(profileCode);
        if (Profile.ROOT.getCode().equals(profileCode)) {
            throw new IllegalArgumentException("Não é possível modificar o perfil ROOT");
        }
    }

    public static void validateProfileCodeNotNull(Integer profileCode) throws IllegalArgumentException {
        if (profileCode == null) {
            throw new IllegalArgumentException("O código do perfil não pode ser nulo");
        }
    }

    public static void validateValidProfileCode(Integer profileCode) throws IllegalArgumentException {
        validateProfileCodeNotNull(profileCode);
        try {
            Profile.fromCode(profileCode);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Código de perfil inválido: " + profileCode);
        }
    }
}