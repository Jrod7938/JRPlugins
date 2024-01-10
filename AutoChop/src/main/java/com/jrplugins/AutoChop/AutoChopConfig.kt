/*
 * Copyright (c) 2024. By Jrod7938
 *
 */

package com.jrplugins.AutoChop

import com.jrplugins.AutoChop.enums.TreeAndLocation
import net.runelite.client.config.Config
import net.runelite.client.config.ConfigGroup
import net.runelite.client.config.ConfigItem
import net.runelite.client.config.Keybind

@ConfigGroup("AutoChop")
interface AutoChopConfig : Config {
    @ConfigItem(
        keyName = "Toggle",
        name = "Toggle",
        description = "",
        position = 0
    )
    fun toggle(): Keybind = Keybind.NOT_SET

    @ConfigItem(
        keyName = "overlay",
        name = "Overlay",
        description = "Display Overlay?",
        position = 1
    )
    fun displayOverlay(): Boolean = true

    @ConfigItem(
        keyName = "burnLogs",
        name = "Burn Logs?",
        description = "Burn Logs?",
        position = 2
    )
    fun burnLogs(): Boolean = false

    @ConfigItem(
        keyName = "treeName",
        name = "Tree Name",
        description = "Tree to cut",
        position = 3
    )
    fun TREEANDLOCATION(): TreeAndLocation = TreeAndLocation.Maple

}