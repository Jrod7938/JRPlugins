package com.ozplugins.AutoMLM;

import lombok.Getter;

@Getter
public enum UISettings {
    NONE("None"),
    SIMPLE("Simple"),
    DEFAULT("Default"),
    FULL("Full");

    private final String name;

    UISettings(String name) {
        this.name = name;
    }
}

