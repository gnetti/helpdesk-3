package com.luiz.helpdesk.application.ports.in;

import com.luiz.helpdesk.domain.enums.Profile;
import com.luiz.helpdesk.domain.exception.profile.ProfileNotFoundException;
import com.luiz.helpdesk.domain.exception.tokenTime.DuplicateProfileException;
import com.luiz.helpdesk.domain.exception.tokenTime.TokenTimeUpdateException;
import com.luiz.helpdesk.domain.exception.tokenTime.UnauthorizedAccessException;
import com.luiz.helpdesk.infrastructure.adapters.in.web.dto.TokenTimeProfileDTO;

import java.util.List;
import java.util.Optional;

public interface TokenTimeManagementUseCasePort {

    TokenTimeProfileDTO create(TokenTimeProfileDTO tokenTimeProfileDTO) throws UnauthorizedAccessException, IllegalArgumentException, DuplicateProfileException;

    TokenTimeProfileDTO update(Integer profileCode, TokenTimeProfileDTO tokenTimeProfileDTO) throws UnauthorizedAccessException, IllegalArgumentException, ProfileNotFoundException, TokenTimeUpdateException;

    Optional<TokenTimeProfileDTO> findByProfile(Integer profileCode) throws UnauthorizedAccessException;

    Optional<TokenTimeProfileDTO> findByProfileForLogin(Integer profileCode);

    List<TokenTimeProfileDTO> findAll() throws UnauthorizedAccessException;

    boolean existsByProfile(Integer profileCode);

    long getExpirationTimeInMillis(Profile profile);

    TokenTimeProfileDTO getTokenTimeProfile(Integer profileCode) throws TokenTimeUpdateException;
}