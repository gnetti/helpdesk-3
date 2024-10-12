package com.luiz.helpdesk.infrastructure.adapters.out.persistence.entity;

import com.luiz.helpdesk.domain.enums.Profile;
import com.luiz.helpdesk.domain.model.TokenTimeProfile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "token_time_profiles")
public class TokenTimeProfilesEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(nullable = false, unique = true)
    private Integer profile;

    @Column(nullable = false)
    private BigDecimal tokenExpirationTimeMinutes;

    @Column(nullable = false)
    private BigDecimal timeToShowDialogMinutes;

    @Column(nullable = false)
    private BigDecimal dialogDisplayTimeForTokenUpdateMinutes;

    @Column(nullable = false)
    private BigDecimal tokenUpdateIntervalMinutes;

    public TokenTimeProfilesEntity() {
    }

    private TokenTimeProfilesEntity(Builder builder) {
        this.profile = builder.profile.ordinal();
        this.tokenExpirationTimeMinutes = builder.tokenExpirationTimeMinutes;
        this.timeToShowDialogMinutes = builder.timeToShowDialogMinutes;
        this.dialogDisplayTimeForTokenUpdateMinutes = builder.dialogDisplayTimeForTokenUpdateMinutes;
        this.tokenUpdateIntervalMinutes = builder.tokenUpdateIntervalMinutes;
    }

    public static TokenTimeProfilesEntity fromDomainModel(TokenTimeProfile domainModel) {
        return new Builder()
                .withProfile(domainModel.getProfile())
                .withTokenExpirationTimeMinutes(domainModel.getTokenExpirationTimeMinutes())
                .withTimeToShowDialogMinutes(domainModel.getTimeToShowDialogMinutes())
                .withDialogDisplayTimeForTokenUpdateMinutes(domainModel.getDialogDisplayTimeForTokenUpdateMinutes())
                .withTokenUpdateIntervalMinutes(domainModel.getTokenUpdateIntervalMinutes())
                .build();
    }

    public TokenTimeProfile toDomainModel() {
        return TokenTimeProfile.builder()
                .withProfile(Profile.values()[this.profile])
                .withTokenExpirationTimeMinutes(this.tokenExpirationTimeMinutes)
                .withTimeToShowDialogMinutes(this.timeToShowDialogMinutes)
                .withDialogDisplayTimeForTokenUpdateMinutes(this.dialogDisplayTimeForTokenUpdateMinutes)
                .withTokenUpdateIntervalMinutes(this.tokenUpdateIntervalMinutes)
                .build();
    }

    public void updateFromDomainModel(TokenTimeProfile domainModel) {
        this.profile = domainModel.getProfile().ordinal();
        this.tokenExpirationTimeMinutes = domainModel.getTokenExpirationTimeMinutes();
        this.timeToShowDialogMinutes = domainModel.getTimeToShowDialogMinutes();
        this.dialogDisplayTimeForTokenUpdateMinutes = domainModel.getDialogDisplayTimeForTokenUpdateMinutes();
        this.tokenUpdateIntervalMinutes = domainModel.getTokenUpdateIntervalMinutes();
    }

    public Builder toBuilder() {
        return new Builder()
                .withProfile(Profile.values()[this.profile])
                .withTokenExpirationTimeMinutes(this.tokenExpirationTimeMinutes)
                .withTimeToShowDialogMinutes(this.timeToShowDialogMinutes)
                .withDialogDisplayTimeForTokenUpdateMinutes(this.dialogDisplayTimeForTokenUpdateMinutes)
                .withTokenUpdateIntervalMinutes(this.tokenUpdateIntervalMinutes);
    }

    public static class Builder {
        private Profile profile;
        private BigDecimal tokenExpirationTimeMinutes;
        private BigDecimal timeToShowDialogMinutes;
        private BigDecimal dialogDisplayTimeForTokenUpdateMinutes;
        private BigDecimal tokenUpdateIntervalMinutes;

        public Builder withProfile(Profile profile) {
            this.profile = profile;
            return this;
        }

        public Builder withTokenExpirationTimeMinutes(BigDecimal tokenExpirationTimeMinutes) {
            this.tokenExpirationTimeMinutes = tokenExpirationTimeMinutes;
            return this;
        }

        public Builder withTimeToShowDialogMinutes(BigDecimal timeToShowDialogMinutes) {
            this.timeToShowDialogMinutes = timeToShowDialogMinutes;
            return this;
        }

        public Builder withDialogDisplayTimeForTokenUpdateMinutes(BigDecimal dialogDisplayTimeForTokenUpdateMinutes) {
            this.dialogDisplayTimeForTokenUpdateMinutes = dialogDisplayTimeForTokenUpdateMinutes;
            return this;
        }

        public Builder withTokenUpdateIntervalMinutes(BigDecimal tokenUpdateIntervalMinutes) {
            this.tokenUpdateIntervalMinutes = tokenUpdateIntervalMinutes;
            return this;
        }

        public TokenTimeProfilesEntity build() {
            return new TokenTimeProfilesEntity(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public Profile getProfile() {
        return Profile.values()[profile];
    }

    public BigDecimal getTokenExpirationTimeMinutes() {
        return tokenExpirationTimeMinutes;
    }

    public BigDecimal getTimeToShowDialogMinutes() {
        return timeToShowDialogMinutes;
    }

    public BigDecimal getDialogDisplayTimeForTokenUpdateMinutes() {
        return dialogDisplayTimeForTokenUpdateMinutes;
    }

    public BigDecimal getTokenUpdateIntervalMinutes() {
        return tokenUpdateIntervalMinutes;
    }
}