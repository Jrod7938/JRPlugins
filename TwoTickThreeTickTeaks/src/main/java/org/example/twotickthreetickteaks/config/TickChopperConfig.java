package org.example.twotickthreetickteaks.config;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("tickChopper")
public interface TickChopperConfig extends Config {
    @ConfigItem(
            keyName = "teakChopper",
            name = "Choose Method",
            description = "Choose Tick Manipulation Method"
    )
    default ChopMode chopMode()
    {
        return ChopMode.NOT_SET;
    }
}
