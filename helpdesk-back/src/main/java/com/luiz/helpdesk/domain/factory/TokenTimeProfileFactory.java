package com.luiz.helpdesk.domain.factory;

import com.luiz.helpdesk.domain.enums.Profile;
import com.luiz.helpdesk.domain.model.TokenTimeProfile;

import java.math.BigDecimal;

public interface TokenTimeProfileFactory {
    TokenTimeProfile createTokenTimeProfile(Profile profile,
                                            BigDecimal tokenExpirationTimeMinutes,
                                            BigDecimal timeToShowDialogMinutes,
                                            BigDecimal dialogDisplayTimeForTokenUpdateMinutes,
                                            BigDecimal tokenUpdateIntervalMinutes);
}