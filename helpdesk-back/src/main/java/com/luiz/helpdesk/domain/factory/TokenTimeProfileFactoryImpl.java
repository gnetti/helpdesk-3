package com.luiz.helpdesk.domain.factory;

import com.luiz.helpdesk.domain.enums.Profile;
import com.luiz.helpdesk.domain.model.TokenTimeProfile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TokenTimeProfileFactoryImpl implements TokenTimeProfileFactory {

    @Override
    public TokenTimeProfile createTokenTimeProfile(
            Profile profile,
            BigDecimal tokenExpirationTimeMinutes,
            BigDecimal timeToShowDialogMinutes,
            BigDecimal dialogDisplayTimeForTokenUpdateMinutes,
            BigDecimal tokenUpdateIntervalMinutes) {
        return TokenTimeProfile.builder()
                .withProfile(profile)
                .withTokenExpirationTimeMinutes(tokenExpirationTimeMinutes)
                .withTimeToShowDialogMinutes(timeToShowDialogMinutes)
                .withDialogDisplayTimeForTokenUpdateMinutes(dialogDisplayTimeForTokenUpdateMinutes)
                .withTokenUpdateIntervalMinutes(tokenUpdateIntervalMinutes)
                .build();
    }

    @Override
    public String toString() {
        return "TokenTimeProfileFactoryImpl{}";
    }
}