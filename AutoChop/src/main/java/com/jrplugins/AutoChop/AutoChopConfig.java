/*
 * Copyright (c) 2024. By Jrod7938
 *
 */

package com.jrplugins.AutoChop;

import com.jrplugins.AutoChop.enums.TreeAndLocation;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

@ConfigGroup("AutoChop")
public interface AutoChopConfig extends Config {
    @ConfigItem(
            keyName = "toggle",
            name = "Toggle",
            description = "Start/Stop Toggle",
            position = 0
    )
    default Keybind toggle() { return Keybind.NOT_SET; }

    @ConfigItem(
            keyName = "overlay",
            name = "Overlay",
            description = "Display Overlay?",
            position = 1
    )
    default Boolean displayOverlay() { return true; }

    @ConfigItem(
            keyName = "burnLogs",
            name = "Burn Logs?",
            description = "Burn Logs?",
            position = 2
    )
    default Boolean burnLogs() { return false; }

    @ConfigItem(
            keyName = "treeName",
            name = "Tree Name",
            description = "Tree to cut",
            position = 3
    )
    default TreeAndLocation TREEANDLOCATION() { return TreeAndLocation.Maple; }

}