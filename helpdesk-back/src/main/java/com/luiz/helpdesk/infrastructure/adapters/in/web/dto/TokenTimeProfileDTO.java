package com.luiz.helpdesk.infrastructure.adapters.in.web.dto;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.luiz.helpdesk.domain.enums.Profile;
import com.luiz.helpdesk.infrastructure.adapters.in.web.annotation.ExcludeFromLogin;
import com.luiz.helpdesk.domain.model.TokenTimeProfile;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.Objects;

@Schema(description = "Data Transfer Object for Token Time Profile information",
        example = "{"
                + "\"tokenExpirationTimeMinutes\":60,"
                + "\"timeToShowDialogMinutes\":5,"
                + "\"dialogDisplayTimeForTokenUpdateMinutes\":1,"
                + "\"tokenUpdateIntervalMinutes\":30"
                + "}")

@JsonFilter("loginDTOFilter")
public class TokenTimeProfileDTO {

    @ExcludeFromLogin
    @Schema(description = "Unique identifier for the token time profile")
    private Long id;

    @Schema(description = "Profile code associated with the token time settings", example = "1")
    private Integer profileCode;

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

    public TokenTimeProfileDTO(Integer profileCode,
                               BigDecimal tokenExpirationTimeMinutes,
                               BigDecimal timeToShowDialogMinutes,
                               BigDecimal dialogDisplayTimeForTokenUpdateMinutes,
                               BigDecimal tokenUpdateIntervalMinutes) {
        this.profileCode = profileCode;
        this.tokenExpirationTimeMinutes = tokenExpirationTimeMinutes;
        this.timeToShowDialogMinutes = timeToShowDialogMinutes;
        this.dialogDisplayTimeForTokenUpdateMinutes = dialogDisplayTimeForTokenUpdateMinutes;
        this.tokenUpdateIntervalMinutes = tokenUpdateIntervalMinutes;
    }

    public TokenTimeProfileDTO(Long id, Integer profileCode, BigDecimal tokenExpirationTimeMinutes, BigDecimal timeToShowDialogMinutes,
                               BigDecimal dialogDisplayTimeForTokenUpdateMinutes, BigDecimal tokenUpdateIntervalMinutes) {
        this.id = id;
        this.profileCode = profileCode;
        this.tokenExpirationTimeMinutes = tokenExpirationTimeMinutes;
        this.timeToShowDialogMinutes = timeToShowDialogMinutes;
        this.dialogDisplayTimeForTokenUpdateMinutes = dialogDisplayTimeForTokenUpdateMinutes;
        this.tokenUpdateIntervalMinutes = tokenUpdateIntervalMinutes;
    }

    @Schema(description = "Create DTO from domain model")
    public static TokenTimeProfileDTO fromDomainModel(TokenTimeProfile domainModel) {
        return new TokenTimeProfileDTO(
                domainModel.getId(),
                domainModel.getProfile().getCode(),
                domainModel.getTokenExpirationTimeMinutes(),
                domainModel.getTimeToShowDialogMinutes(),
                domainModel.getDialogDisplayTimeForTokenUpdateMinutes(),
                domainModel.getTokenUpdateIntervalMinutes()
        );
    }

    @Schema(description = "Convert DTO to domain model")
    public TokenTimeProfile toDomainModel() {
        if (this.profileCode == null) {
            throw new IllegalArgumentException("Profile code cannot be null");
        }
        return TokenTimeProfile.builder()
                .withId(this.id)
                .withProfile(Profile.fromCode(this.profileCode))
                .withTokenExpirationTimeMinutes(this.tokenExpirationTimeMinutes)
                .withTimeToShowDialogMinutes(this.timeToShowDialogMinutes)
                .withDialogDisplayTimeForTokenUpdateMinutes(this.dialogDisplayTimeForTokenUpdateMinutes)
                .withTokenUpdateIntervalMinutes(this.tokenUpdateIntervalMinutes)
                .build();
    }

    @Schema(description = "Update existing domain model with DTO data")
    public TokenTimeProfile updateDomainModel(TokenTimeProfile existingProfile) {
        return TokenTimeProfile.builder()
                .withId(existingProfile.getId())
                .withProfile(existingProfile.getProfile())
                .withTokenExpirationTimeMinutes(this.tokenExpirationTimeMinutes)
                .withTimeToShowDialogMinutes(this.timeToShowDialogMinutes)
                .withDialogDisplayTimeForTokenUpdateMinutes(this.dialogDisplayTimeForTokenUpdateMinutes)
                .withTokenUpdateIntervalMinutes(this.tokenUpdateIntervalMinutes)
                .build();
    }

    @Schema(description = "Create DTO from domain model for login")
    public static TokenTimeProfileDTO fromDomainModelForLogin(TokenTimeProfile domainModel) {
        return new TokenTimeProfileDTO(
                domainModel.getProfile().getCode(),
                domainModel.getTokenExpirationTimeMinutes(),
                domainModel.getTimeToShowDialogMinutes(),
                domainModel.getDialogDisplayTimeForTokenUpdateMinutes(),
                domainModel.getTokenUpdateIntervalMinutes()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getProfileCode() {
        return profileCode;
    }

    public void setProfileCode(Integer profileCode) {
        this.profileCode = profileCode;
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
        if (!(o instanceof TokenTimeProfileDTO that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(profileCode, that.profileCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, profileCode);
    }

    @Override
    public String toString() {
        return "TokenTimeProfileDTO{" +
                "id=" + id +
                ", profileCode=" + profileCode +
                ", tokenExpirationTimeMinutes=" + tokenExpirationTimeMinutes +
                ", timeToShowDialogMinutes=" + timeToShowDialogMinutes +
                ", dialogDisplayTimeForTokenUpdateMinutes=" + dialogDisplayTimeForTokenUpdateMinutes +
                ", tokenUpdateIntervalMinutes=" + tokenUpdateIntervalMinutes +
                '}';
    }
}