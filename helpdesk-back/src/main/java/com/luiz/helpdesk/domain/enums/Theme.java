package com.luiz.helpdesk.domain.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum Theme {
    AZURE_BLUE(0, "azureBlue"),
    CYAN_ORANGE(1, "cyanOrange"),
    DEEP_PURPLE_AMBER(2, "deepPurpleAmber"),
    INDIGO_PINK(3, "indigoPink"),
    MAGENTA_VIOLET(4, "magentaViolet"),
    PINK_BLUE_GREY(5, "pinkBlueGrey"),
    PURPLE_GREEN(6, "purpleGreen"),
    ROSE_RED(7, "roseRed");

    private static final Map<Integer, Theme> CODE_MAP;
    private static final Map<String, Theme> DESCRIPTION_MAP;

    static {
        CODE_MAP = Arrays.stream(Theme.values()).collect(Collectors.toMap(Theme::getCode, theme -> theme));
        DESCRIPTION_MAP = Arrays.stream(Theme.values()).collect(Collectors.toMap(Theme::getDescription, theme -> theme));
    }

    private final Integer code;
    private final String description;

    Theme(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static Theme fromDescription(String description) {
        Theme theme = DESCRIPTION_MAP.get(description);
        if (theme == null) {
            throw new IllegalArgumentException("Invalid theme description: " + description);
        }
        return theme;
    }

    public static Theme fromCode(Integer code) {
        Theme theme = CODE_MAP.get(code);
        if (theme == null) {
            throw new IllegalArgumentException("Invalid theme code: " + code);
        }
        return theme;
    }
}