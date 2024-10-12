package com.luiz.helpdesk.application.ports.in;

import com.luiz.helpdesk.domain.enums.Profile;
import com.luiz.helpdesk.infrastructure.adapters.in.web.dto.TokenTimeProfileDTO;

import java.util.List;
import java.util.Optional;

public interface TokenTimeManagementUseCasePort {

    TokenTimeProfileDTO create(TokenTimeProfileDTO tokenTimeProfileDTO);

    TokenTimeProfileDTO update(Profile profile, TokenTimeProfileDTO tokenTimeProfileDTO);

    Optional<TokenTimeProfileDTO> findByProfile(Profile profile);

    List<TokenTimeProfileDTO> findAll();

    boolean existsByProfile(Profile profile);

    long getExpirationTimeInMillis(Profile profile);

    TokenTimeProfileDTO getTokenTimeProfile(Profile profile);
}