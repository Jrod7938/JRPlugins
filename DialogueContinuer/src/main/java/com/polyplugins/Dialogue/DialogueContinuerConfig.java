package com.polyplugins.Dialogue;


import net.runelite.client.config.*;
import com.polyplugins.Dialogue.DialogueContinuerPlugin.RunMode;

@ConfigGroup("DialogueContinuerConfig")
public interface DialogueContinuerConfig extends Config {

    @ConfigItem(
            keyName = "info",
            name = "Information",
            description = "",
            position = 0
    )
    default String info() {
        return "Dialogue continue works with quest helper.\n\n" +
                "Dismiss random will check for random npcs that are targeting you and reachable every 2 ticks and dismiss them.";
    }

    @ConfigItem(
            keyName = "Toggle",
            name = "Toggle",
            description = "",
            position = 0
    )
    default Keybind toggle() {
        return Keybind.NOT_SET;
    }

    @ConfigSection(
            name = "Tick Delay",
            description = "",
            position = 1

    )
    String tickDelaySection = "Tick Delay";

    @ConfigItem(
            name = "Tick Delay",
            keyName = "tickDelay",
            description = "Slow down dialogue",
            position = 1,
            section = tickDelaySection
    )
    default int tickDelay() {
        return 0;
    }

    @ConfigItem(
            name = "Dismiss Randoms",
            description = "",
            position = 20,
            keyName = "dismissRandoms")
    default boolean dismissRandoms() {
        return true;
    }

}

