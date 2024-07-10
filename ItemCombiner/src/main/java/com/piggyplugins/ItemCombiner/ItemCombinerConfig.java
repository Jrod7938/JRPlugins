package com.piggyplugins.ItemCombiner;

import net.runelite.client.config.*;

@ConfigGroup("ItemCombiner")
public interface ItemCombinerConfig extends Config {
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
            name = "Configuration",
            description = "Config for item combiner",
            position = 1
    )
    String configuration = "Configuration";

    @ConfigItem(
            keyName = "itemOneName",
            name = "Item One (Tool/Vial)",
            description = "Name of the first item",
            position = 2,
            section = configuration
    )
    default String itemOneName() {
        return "";
    }

    @ConfigItem(
            keyName = "itemOneAmt",
            name = "Item One Amount",
            description = "Amount of the first item",
            position = 3,
            section = configuration
    )
    default int itemOneAmt() {
        return 14;
    }

    @ConfigItem(
            keyName = "itemTwoName",
            name = "Item Two (Herb/Second/Gem/Etc.)",
            description = "Name of the second item",
            position = 4,
            section = configuration
    )
    default String itemTwoName() {
        return "";
    }

    @ConfigItem(
            keyName = "itemTwoAmt",
            name = "Item Two Amount",
            description = "Amount of the second item",
            position = 4,
            section = configuration
    )
    default int itemTwoAmt() {
        return 14;
    }
    @ConfigSection(
            name = "Game Tick Configuration",
            description = "Configure how the bot handles game tick delays, 1 game tick equates to roughly 600ms",
            position = 5
    )
    String tickDelayConfig = "tickDelayConfig";
    
    @Range(
            max = 10
    )
    @ConfigItem(
            keyName = "tickDelayMin",
            name = "Banking Tick Min",
            description = "",
            position = 7,
            section = tickDelayConfig
    )
    default int tickDelayBankingMin() {
        return 2;
    }

    @Range(
            max = 15
    )
    @ConfigItem(
            keyName = "tickDelayMax",
            name = "Banking Tick Max",
            description = "",
            position = 8,
            section = tickDelayConfig
    )
    default int tickDelayBankingMax() {
        return 3;
    }

    @ConfigItem(
            keyName = "tickDelayEnabled",
            name = "Enable Tick Delay",
            description = "enables some tick delays",
            position = 6,
            section = tickDelayConfig
    )
    default boolean tickDelayBanking() {
        return false;
    }
}
