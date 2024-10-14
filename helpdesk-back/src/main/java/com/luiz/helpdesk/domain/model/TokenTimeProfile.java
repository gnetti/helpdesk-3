package com.luiz.helpdesk.domain.model;

import com.luiz.helpdesk.domain.enums.Profile;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TokenTimeProfile {

    private final Long id;
    private final Profile profile;
    private final BigDecimal tokenExpirationTimeMinutes;
    private final BigDecimal timeToShowDialogMinutes;
    private final BigDecimal dialogDisplayTimeForTokenUpdateMinutes;
    private final BigDecimal tokenUpdateIntervalMinutes;

    private TokenTimeProfile(Builder builder) {
        validateFields(builder.profile, builder.tokenExpirationTimeMinutes, builder.timeToShowDialogMinutes,
                builder.dialogDisplayTimeForTokenUpdateMinutes, builder.tokenUpdateIntervalMinutes);
        this.id = builder.profile == Profile.ROOT ? null : builder.id;
        this.profile = builder.profile;
        this.tokenExpirationTimeMinutes = builder.tokenExpirationTimeMinutes;
        this.timeToShowDialogMinutes = builder.timeToShowDialogMinutes;
        this.dialogDisplayTimeForTokenUpdateMinutes = builder.dialogDisplayTimeForTokenUpdateMinutes;
        this.tokenUpdateIntervalMinutes = builder.tokenUpdateIntervalMinutes;
    }

    private void validateFields(Profile profile, BigDecimal tokenExpirationTimeMinutes, BigDecimal timeToShowDialogMinutes,
                                BigDecimal dialogDisplayTimeForTokenUpdateMinutes, BigDecimal tokenUpdateIntervalMinutes) {
        List<String> invalidFields = new ArrayList<>();

        validateField(profile, "Profile", invalidFields);
        validateField(tokenExpirationTimeMinutes, "Token Expiration Time", invalidFields);
        validateField(timeToShowDialogMinutes, "Time to Show Dialog", invalidFields);
        validateField(dialogDisplayTimeForTokenUpdateMinutes, "Dialog Display Time for Token Update", invalidFields);
        validateField(tokenUpdateIntervalMinutes, "Token Update Interval", invalidFields);

        if (!invalidFields.isEmpty()) {
            throw new IllegalArgumentException(buildErrorMessage(invalidFields));
        }
    }

    private void validateField(Object field, String fieldName, List<String> invalidFields) {
        if (field == null) {
            invalidFields.add(fieldName);
        }
    }

    private String buildErrorMessage(List<String> invalidFields) {
        return getString(invalidFields);
    }

    static String getString(List<String> invalidFields) {
        if (invalidFields.isEmpty()) {
            return "";
        }

        String joinedFields = String.join(", ", invalidFields);
        String lastField = invalidFields.get(invalidFields.size() - 1);

        if (invalidFields.size() > 1) {
            joinedFields = joinedFields.substring(0, joinedFields.lastIndexOf(", ")) + " and " + lastField;
        }

        String verb = invalidFields.size() > 1 ? "are" : "is";
        return joinedFields + " " + verb + " invalid or empty";
    }

    public TokenTimeProfile withoutId() {
        return new Builder()
                .withProfile(this.profile)
                .withTokenExpirationTimeMinutes(this.tokenExpirationTimeMinutes)
                .withTimeToShowDialogMinutes(this.timeToShowDialogMinutes)
                .withDialogDisplayTimeForTokenUpdateMinutes(this.dialogDisplayTimeForTokenUpdateMinutes)
                .withTokenUpdateIntervalMinutes(this.tokenUpdateIntervalMinutes)
                .build();
    }

    public Long getId() {
        return profile == Profile.ROOT ? null : id;
    }

    public Profile getProfile() {
        return profile;
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

    public static class Builder {
        private Long id;
        private Profile profile;
        private BigDecimal tokenExpirationTimeMinutes;
        private BigDecimal timeToShowDialogMinutes;
        private BigDecimal dialogDisplayTimeForTokenUpdateMinutes;
        private BigDecimal tokenUpdateIntervalMinutes;

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

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

        public TokenTimeProfile build() {
            return new TokenTimeProfile(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenTimeProfile that = (TokenTimeProfile) o;
        return Objects.equals(getId(), that.getId()) &&
                profile == that.profile &&
                Objects.equals(tokenExpirationTimeMinutes, that.tokenExpirationTimeMinutes) &&
                Objects.equals(timeToShowDialogMinutes, that.timeToShowDialogMinutes) &&
                Objects.equals(dialogDisplayTimeForTokenUpdateMinutes, that.dialogDisplayTimeForTokenUpdateMinutes) &&
                Objects.equals(tokenUpdateIntervalMinutes, that.tokenUpdateIntervalMinutes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), profile, tokenExpirationTimeMinutes, timeToShowDialogMinutes,
                dialogDisplayTimeForTokenUpdateMinutes, tokenUpdateIntervalMinutes);
    }

    @Override
    public String toString() {
        return "TokenTimeProfile{" +
                "id=" + getId() +
                ", profile=" + profile +
                ", tokenExpirationTimeMinutes=" + tokenExpirationTimeMinutes +
                ", timeToShowDialogMinutes=" + timeToShowDialogMinutes +
                ", dialogDisplayTimeForTokenUpdateMinutes=" + dialogDisplayTimeForTokenUpdateMinutes +
                ", tokenUpdateIntervalMinutes=" + tokenUpdateIntervalMinutes +
                '}';
    }
}