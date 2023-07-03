package com.piggyplugins.HerbCleaner;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

@ConfigGroup("HerbCleaner")
public interface HerbCleanerConfig extends Config {
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
            keyName = "herbType",
            name = "Herb Type",
            description = "",
            position = 1
    )
    default HerbType herbType() {
        return HerbType.GUAM;
    }
}
