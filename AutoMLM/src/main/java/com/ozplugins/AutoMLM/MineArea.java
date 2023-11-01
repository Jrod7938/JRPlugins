package com.ozplugins.AutoMLM;

import lombok.Getter;
import net.runelite.api.ItemID;

@Getter
public enum MineArea {
    UPPER_1("Upper 1"),
    UPPER_2("Upper 2"),
    LOWER_1("Lower 1"),
    LOWER_2("Lower 2");

    private final String mineArea;

    MineArea(String mineArea) {
        this.mineArea = mineArea;
    }
}
