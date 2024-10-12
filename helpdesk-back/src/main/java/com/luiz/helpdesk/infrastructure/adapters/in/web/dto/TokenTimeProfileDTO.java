package com.luiz.helpdesk.infrastructure.adapters.in.web.dto;

import com.luiz.helpdesk.domain.enums.Profile;
import com.luiz.helpdesk.domain.model.TokenTimeProfile;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.Objects;

@Schema(description = "Data Transfer Object for Token Time Profile information",
        example = "{"
                + "\"profile\":\"ADMIN\","
                + "\"tokenExpirationTimeMinutes\":60,"
                + "\"timeToShowDialogMinutes\":5,"
                + "\"dialogDisplayTimeForTokenUpdateMinutes\":1,"
                + "\"tokenUpdateIntervalMinutes\":30"
                + "}")

public class TokenTimeProfileDTO {

    @Schema(description = "Profile associated with the token time settings", example = "ADMIN")
    @NotNull(message = "Profile is required")
    private Profile profile;

    @Schema(description = "Token expiration time in minutes", example = "60")
    @NotNull(message = "Token expiration time is required")
    @Positive(message = "Token expiration time must be positive")
    private BigDecimal tokenExpirationTimeMinutes;

    @Schema(description = "Time to show dialog before token expiration in minutes", example = "5")
    @NotNull(message = "Time to show dialog is required")
    @Positive(message = "Time to show dialog must be positive")
    private BigDecimal timeToShowDialogMinutes;

    @Schema(description = "Dialog display time for token update in minutes", example = "1")
    @NotNull(message = "Dialog display time is required")
    @Positive(message = "Dialog display time must be positive")
    private BigDecimal dialogDisplayTimeForTokenUpdateMinutes;

    @Schema(description = "Token update interval in minutes", example = "30")
    @NotNull(message = "Token update interval is required")
    @Positive(message = "Token update interval must be positive")
    private BigDecimal tokenUpdateIntervalMinutes;

    public TokenTimeProfileDTO() {
    }

    public TokenTimeProfileDTO(Profile profile, BigDecimal tokenExpirationTimeMinutes, BigDecimal timeToShowDialogMinutes,
                               BigDecimal dialogDisplayTimeForTokenUpdateMinutes, BigDecimal tokenUpdateIntervalMinutes) {
        this.profile = profile;
        this.tokenExpirationTimeMinutes = tokenExpirationTimeMinutes;
        this.timeToShowDialogMinutes = timeToShowDialogMinutes;
        this.dialogDisplayTimeForTokenUpdateMinutes = dialogDisplayTimeForTokenUpdateMinutes;
        this.tokenUpdateIntervalMinutes = tokenUpdateIntervalMinutes;
    }

    @Schema(description = "Create DTO from domain model")
    public static TokenTimeProfileDTO fromDomainModel(TokenTimeProfile domainModel) {
        return new TokenTimeProfileDTO(
                domainModel.getProfile(),
                domainModel.getTokenExpirationTimeMinutes(),
                domainModel.getTimeToShowDialogMinutes(),
                domainModel.getDialogDisplayTimeForTokenUpdateMinutes(),
                domainModel.getTokenUpdateIntervalMinutes()
        );
    }

    @Schema(description = "Convert DTO to domain model")
    public TokenTimeProfile toDomainModel() {
        return TokenTimeProfile.builder()
                .withProfile(this.profile)
                .withTokenExpirationTimeMinutes(this.tokenExpirationTimeMinutes)
                .withTimeToShowDialogMinutes(this.timeToShowDialogMinutes)
                .withDialogDisplayTimeForTokenUpdateMinutes(this.dialogDisplayTimeForTokenUpdateMinutes)
                .withTokenUpdateIntervalMinutes(this.tokenUpdateIntervalMinutes)
                .build();
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public BigDecimal getTokenExpirationTimeMinutes() {
        return tokenExpirationTimeMinutes;
    }

    public void setTokenExpirationTimeMinutes(BigDecimal tokenExpirationTimeMinutes) {
        this.tokenExpirationTimeMinutes = tokenExpirationTimeMinutes;
    }

    public BigDecimal getTimeToShowDialogMinutes() {
        return timeToShowDialogMinutes;
    }

    public void setTimeToShowDialogMinutes(BigDecimal timeToShowDialogMinutes) {
        this.timeToShowDialogMinutes = timeToShowDialogMinutes;
    }

    public BigDecimal getDialogDisplayTimeForTokenUpdateMinutes() {
        return dialogDisplayTimeForTokenUpdateMinutes;
    }

    public void setDialogDisplayTimeForTokenUpdateMinutes(BigDecimal dialogDisplayTimeForTokenUpdateMinutes) {
        this.dialogDisplayTimeForTokenUpdateMinutes = dialogDisplayTimeForTokenUpdateMinutes;
    }

    public BigDecimal getTokenUpdateIntervalMinutes() {
        return tokenUpdateIntervalMinutes;
    }

    public void setTokenUpdateIntervalMinutes(BigDecimal tokenUpdateIntervalMinutes) {
        this.tokenUpdateIntervalMinutes = tokenUpdateIntervalMinutes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TokenTimeProfileDTO)) return false;
        TokenTimeProfileDTO that = (TokenTimeProfileDTO) o;
        return profile == that.profile;
    }

    @Override
    public int hashCode() {
        return Objects.hash(profile);
    }

    @Override
    public String toString() {
        return "TokenTimeProfileDTO{" +
                "profile=" + profile +
                ", tokenExpirationTimeMinutes=" + tokenExpirationTimeMinutes +
                ", timeToShowDialogMinutes=" + timeToShowDialogMinutes +
                ", dialogDisplayTimeForTokenUpdateMinutes=" + dialogDisplayTimeForTokenUpdateMinutes +
                ", tokenUpdateIntervalMinutes=" + tokenUpdateIntervalMinutes +
                '}';
    }
}