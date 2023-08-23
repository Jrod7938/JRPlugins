package com.piggyplugins.JadAutoPrayers;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("JadAutoPrayers")
public interface JadAutoPrayersConfig extends Config {
    @ConfigItem(
            keyName = "oneTickFlick",
            name = "One Tick Flick",
            description = ""
    )
    default boolean oneTickFlick() {
        return true;
    }
}
