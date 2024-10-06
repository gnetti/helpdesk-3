package com.luiz.helpdesk.domain.model;

public enum Theme {
    AZURE_BLUE(0, "azureBlue"),
    CYAN_ORANGE(1, "cyanOrange"),
    DEEP_PURPLE_AMBER(2, "deepPurpleAmber"),
    INDIGO_PINK(3, "indigoPink"),
    MAGENTA_VIOLET(4, "magentaViolet"),
    PINK_BLUE_GREY(5, "pinkBlueGrey"),
    PURPLE_GREEN(6, "purpleGreen"),
    ROSE_RED(7, "roseRed");

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
}