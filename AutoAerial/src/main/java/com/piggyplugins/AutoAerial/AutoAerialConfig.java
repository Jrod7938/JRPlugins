package com.piggyplugins.AutoAerial;


import net.runelite.client.config.*;

@ConfigGroup("AutoAerial")
public interface AutoAerialConfig extends Config {
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
            keyName = "Handle fish at",
            name = "Handle at",
            description = "# of fish to cut/drop at",
            position = 1
    )
    default int handleFishAt() {
        return 6;
    }
}
