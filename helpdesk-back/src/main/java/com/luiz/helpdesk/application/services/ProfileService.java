package com.luiz.helpdesk.application.services;

import com.luiz.helpdesk.application.ports.in.ProfileUseCasePort;
import com.luiz.helpdesk.domain.enums.Profile;
import com.luiz.helpdesk.domain.exception.profile.ProfileNotFoundException;
import com.luiz.helpdesk.domain.validator.ProfileValidator;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ProfileService implements ProfileUseCasePort {

    @Override
    public Profile getProfileByDescription(String description) {
        ProfileValidator.validateDescription(description);

        String normalizedDescription = description.toUpperCase().replace("ROLE_", "");
        try {
            return Profile.fromDescription("ROLE_" + normalizedDescription);
        } catch (IllegalArgumentException e) {
            throw new ProfileNotFoundException("Nenhum perfil encontrado para descrição: " + description, e);
        }
    }

    @Override
    public Profile getProfileByCode(Integer code) {
        ProfileValidator.validateCode(code);

        try {
            return Profile.fromCode(code);
        } catch (IllegalArgumentException e) {
            throw new ProfileNotFoundException("Nenhum perfil encontrado para o código: " + code, e);
        }
    }

    @Override
    public List<Profile> getAllProfiles() {
        return Arrays.asList(Profile.values());
    }

    @Override
    public boolean isValidProfile(String description) {
        try {
            getProfileByDescription(description);
            return true;
        } catch (ProfileNotFoundException e) {
            return false;
        }
    }

    @Override
    public boolean isValidProfileCode(Integer code) {
        try {
            getProfileByCode(code);
            return true;
        } catch (ProfileNotFoundException e) {
            return false;
        }
    }
}