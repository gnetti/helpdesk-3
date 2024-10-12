package com.luiz.helpdesk.domain.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum Profile {
    ROOT(0, "ROLE_ROOT"),
    ADMIN(1, "ROLE_ADMIN"),
    CLIENT(2, "ROLE_CLIENT"),
    TECHNICIAN(3, "ROLE_TECHNICIAN");

    private static final Map<Integer, Profile> CODE_MAP;
    private static final Map<String, Profile> DESCRIPTION_MAP;

    static {
        CODE_MAP = Arrays.stream(Profile.values()).collect(Collectors.toMap(Profile::getCode, profile -> profile));
        DESCRIPTION_MAP = Arrays.stream(Profile.values()).collect(Collectors.toMap(Profile::getDescription, profile -> profile));
    }

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

    public static Profile fromDescription(String description) {
        Profile profile = DESCRIPTION_MAP.get(description);
        if (profile == null) {
            throw new IllegalArgumentException("Invalid profile description: " + description);
        }
        return profile;
    }

    public static Profile fromCode(Integer code) {
        Profile profile = CODE_MAP.get(code);
        if (profile == null) {
            throw new IllegalArgumentException("Invalid profile code: " + code);
        }
        return profile;
    }
}