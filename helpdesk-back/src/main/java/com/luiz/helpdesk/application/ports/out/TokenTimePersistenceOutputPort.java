package com.luiz.helpdesk.application.ports.out;

import com.luiz.helpdesk.domain.enums.Profile;
import com.luiz.helpdesk.domain.model.TokenTimeProfile;

import java.util.List;
import java.util.Optional;

public interface TokenTimePersistenceOutputPort {

    TokenTimeProfile saveTokenTime(TokenTimeProfile tokenTimeProfile);

    Optional<TokenTimeProfile> findByProfile(Integer profile);

    Optional<TokenTimeProfile> findByProfileForLogin(Integer profile);

    List<TokenTimeProfile> findAllTokenTime();

    boolean existsByProfile(Integer profile);

    Optional<TokenTimeProfile> updateTokenTime(Integer profile, TokenTimeProfile tokenTimeProfile);

    long getTokenExpirationTimeInMillis(Profile profile);

    boolean isRootUser();
}