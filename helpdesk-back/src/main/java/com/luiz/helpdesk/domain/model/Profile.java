package com.luiz.helpdesk.domain.model;

public enum Profile {
    ROOT(0, "ROLE_ROOT"),
    ADMIN(1, "ROLE_ADMIN"),
    CLIENT(2, "ROLE_CLIENT"),
    TECHNICIAN(3, "ROLE_TECHNICIAN");

    private final Integer code;
    private final String description;

    Profile(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static Profile fromCode(Integer code) {
        for (Profile profile : Profile.values()) {
            if (profile.getCode().equals(code)) {
                return profile;
            }
        }
        throw new IllegalArgumentException("Invalid profile code: " + code);
    }

    public static Profile fromDescription(String description) {
        for (Profile profile : Profile.values()) {
            if (profile.getDescription().equals(description)) {
                return profile;
            }
        }
        throw new IllegalArgumentException("Invalid profile description: " + description);
    }
}