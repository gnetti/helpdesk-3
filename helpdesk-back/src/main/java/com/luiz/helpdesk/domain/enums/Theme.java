package com.luiz.helpdesk.domain.enums;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Theme {
    AZURE_BLUE("azureBlue"),
    CYAN_ORANGE("cyanOrange"),
    DEEP_PURPLE_AMBER("deepPurpleAmber"),
    INDIGO_PINK("indigoPink"),
    MAGENTA_VIOLET("magentaViolet"),
    PINK_BLUE_GREY("pinkBlueGrey"),
    PURPLE_GREEN("purpleGreen"),
    ROSE_RED("roseRed");

    private final String value;
    private static final Map<String, Theme> LOOKUP_MAP;

    static {
        LOOKUP_MAP = Stream.of(values())
                .flatMap(theme -> Stream.of(
                        Map.entry(theme.value.toLowerCase(), theme),
                        Map.entry(theme.name().toLowerCase(), theme)
                ))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existing, replacement) -> existing
                ));
    }

    Theme(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Theme fromString(String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Theme text cannot be null or empty");
        }

        Theme theme = LOOKUP_MAP.get(text.toLowerCase());
        if (theme == null) {
            throw new IllegalArgumentException("No constant with text '" + text + "' found");
        }

        return theme;
    }
}