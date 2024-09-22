package com.luiz.helpdesk.application.ports.in;

import com.luiz.helpdesk.domain.enums.Profile;

import java.util.List;

public interface ProfileUseCasePort {
    Profile getProfileByDescription(String description);

    Profile getProfileByCode(Integer code);

    List<Profile> getAllProfiles();

    boolean isValidProfile(String description);

    boolean isValidProfileCode(Integer code);
}