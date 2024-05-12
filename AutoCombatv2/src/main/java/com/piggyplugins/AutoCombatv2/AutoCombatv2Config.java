package com.piggyplugins.AutoCombatv2;


import net.runelite.client.config.*;

@ConfigGroup("AutoCombatv2Config")
public interface AutoCombatv2Config extends Config {
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
            keyName = "npcTarget",
            name = "npcTarget",
            description = "NPCs you want to kill",
            position = 1
    )
    default String npcTarget() {
        return "Hill giant";
    }

    @ConfigItem(
            keyName = "loot",
            name = "Items to loot",
            description = "Write a list of item names (Case sensitive) separated by commas.",
            position = 2
    )
    default String loot() {
        return "Coins, Big bones";
    }

    @ConfigItem(
            keyName = "tickDelay",
            name = "Tick Delay",
            description = "Slow down certain actions",
            position = 3
    )
    default int tickDelay() {
        return 0;
    }
}