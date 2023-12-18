package com.piggyplugins.AutoChop

import net.runelite.client.config.Config
import net.runelite.client.config.ConfigGroup
import net.runelite.client.config.ConfigItem

@ConfigGroup("AutoChop")
interface AutoChopConfig : Config {
    @ConfigItem(
        keyName = "overlay",
        name = "Overlay",
        description = "Display Overlay?",
        position = 0
    )
    fun displayOverlay(): Boolean = true

    @ConfigItem(
        keyName = "burnLogs",
        name = "Burn Logs?",
        description = "Burn Logs?",
        position = 1
    )
    fun burnLogs(): Boolean = false

    @ConfigItem(
        keyName = "treeName",
        name = "Tree Name",
        description = "Tree to cut",
        position = 2
    )
    fun TREEANDLOCATION(): TreeAndLocation = TreeAndLocation.Maple

}