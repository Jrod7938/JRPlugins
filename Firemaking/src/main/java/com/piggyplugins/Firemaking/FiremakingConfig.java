package com.piggyplugins.Firemaking;

import net.runelite.client.config.*;

@ConfigGroup("Firemaking")
public interface FiremakingConfig extends Config {
    @ConfigItem(
            keyName = "Toggle",
            name = "Toggle",
            description = "",
            position = 0
    )
    default Keybind toggle() {
        return Keybind.NOT_SET;
    }

    @Range(
            min = 1,
            max = 10
    )
    @ConfigItem(
            keyName = "logs",
            name = "Logs",
            description = "",
            position = 1
    )
    default String getLogs() {
        return "Magic logs";
    }

}
