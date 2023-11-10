package com.piggyplugins.AutoSmith;

import net.runelite.client.config.*;

@ConfigGroup("PlateSmith")
public interface AutoSmithConfig extends Config {
    @ConfigItem(
            keyName = "Toggle",
            name = "Toggle",
            description = "",
            position = 0
    )
    default Keybind toggle() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "Bar",
            name = "bar",
            description = "Which bar you will use",
            position = 1
    )
    default Bar bar() {
        return Bar.MITHRIL;
    }

    @ConfigItem(
            keyName = "Item",
            name = "item",
            description = "Which item you will make",
            position = 2
    )
    default SmithingItem item() {
        return SmithingItem.PLATE_BODY;
    }



}