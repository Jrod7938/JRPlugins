package com.polyplugins.KittenFeeder;


import net.runelite.client.config.*;

@ConfigGroup("KittenFeederConfig")
public interface KittenFeederConfig extends Config {
    @ConfigItem(
            keyName = "Food",
            name = "Food",
            description = "",
            position = 0
    )
    default String food() {
        return "Raw salmon";
    }

    @ConfigItem(
            keyName = "frequency",
            name = "Minutes",
            description = "How often to feed your kitten",
            position = 1
    )
    default int frequency() {
        return 1;
    }

}

