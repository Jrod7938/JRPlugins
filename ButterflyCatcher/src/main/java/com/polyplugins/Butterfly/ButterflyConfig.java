package com.polyplugins.Butterfly;


import net.runelite.client.config.*;

@ConfigGroup("ButterflyConfig")
public interface ButterflyConfig extends Config {
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
            keyName = "butterflyName",
            name = "Butterfly name",
            description = "",
            position = 1
    )
    default String butterfly() {
        return "Ruby harvest";
    }

    @ConfigItem(
            keyName = "tickDelay",
            name = "Tick delay",
            description = "Will slow the plugin by this many ticks",
            position = 2
    )
    default int tickDelay() {
        return 0;
    }
}

