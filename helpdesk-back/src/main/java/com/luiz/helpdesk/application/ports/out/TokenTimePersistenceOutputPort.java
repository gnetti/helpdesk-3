package com.luiz.helpdesk.application.ports.out;

import com.luiz.helpdesk.domain.enums.Profile;
import com.luiz.helpdesk.domain.model.TokenTimeProfile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TokenTimePersistenceOutputPort {

    TokenTimeProfile saveTokenTime(TokenTimeProfile tokenTimeProfile);

    Optional<TokenTimeProfile> findByProfile(Profile profile);

    List<TokenTimeProfile> findAllTokenTime();

    boolean existsByProfile(Profile profile);

    Optional<TokenTimeProfile> updateTokenTime(Profile profile, TokenTimeProfile tokenTimeProfile);

    TokenTimeProfile convertToMilliseconds(TokenTimeProfile profile);

    BigDecimal convertToMillis(BigDecimal minutes);

    boolean isRootUser();
}