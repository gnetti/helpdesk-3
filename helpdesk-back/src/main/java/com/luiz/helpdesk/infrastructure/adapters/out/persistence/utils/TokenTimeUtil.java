package com.luiz.helpdesk.infrastructure.adapters.out.persistence.utils;

import com.luiz.helpdesk.application.ports.out.TokenTimePersistenceOutputPort;
import com.luiz.helpdesk.domain.enums.Profile;
import com.luiz.helpdesk.domain.exception.tokenTime.TokenTimeUpdateException;
import com.luiz.helpdesk.domain.model.TokenTimeProfile;
import com.luiz.helpdesk.infrastructure.adapters.in.web.dto.TokenTimeProfileDTO;

import java.math.BigDecimal;

public class TokenTimeUtil {

    public static TokenTimeProfileDTO createRootTokenTimeProfileDTO(
            TokenTimePersistenceOutputPort tokenTimeRepository,
            BigDecimal rootTokenExpirationTimeMinutes,
            BigDecimal rootTimeToShowDialogMinutes,
            BigDecimal rootDialogDisplayTimeForTokenUpdateMinutes,
            BigDecimal rootTokenUpdateIntervalMinutes) {
        return new TokenTimeProfileDTO(
                Profile.ROOT,
                tokenTimeRepository.convertToMillis(rootTokenExpirationTimeMinutes),
                tokenTimeRepository.convertToMillis(rootTimeToShowDialogMinutes),
                tokenTimeRepository.convertToMillis(rootDialogDisplayTimeForTokenUpdateMinutes),
                tokenTimeRepository.convertToMillis(rootTokenUpdateIntervalMinutes)
        );
    }

    public static BigDecimal getTokenExpirationTimeMillis(
            Profile profile,
            TokenTimePersistenceOutputPort tokenTimeRepository,
            BigDecimal rootTokenExpirationTimeMinutes) {
        if (profile == Profile.ROOT) {
            return tokenTimeRepository.convertToMillis(rootTokenExpirationTimeMinutes);
        }
        return tokenTimeRepository.findByProfile(profile)
                .map(TokenTimeProfile::getTokenExpirationTimeMinutes)
                .orElseThrow(() -> new TokenTimeUpdateException("TokenTimeProfile not found for profile: " + profile));
    }
}