package com.piggyplugins.AutoRifts;

import net.runelite.client.config.*;


@ConfigGroup("AutoRifts")
public interface AutoRiftsConfig extends Config {
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
            name = "Game Tick Configuration",
            description = "Configure how the bot handles game tick delays, 1 game tick equates to roughly 600ms",
            position = 1,
            closedByDefault = true
    )
    String delayTickConfig = "delayTickConfig";

    @Range(
            max = 10
    )
    @ConfigItem(
            keyName = "tickDelayMin",
            name = "Game Tick Min",
            description = "",
            position = 2,
            section = delayTickConfig
    )
    default int tickDelayMin() {
        return 1;
    }

    @Range(
            max = 10
    )
    @ConfigItem(
            keyName = "tickDelayMax",
            name = "Game Tick Max",
            description = "",
            position = 3,
            section = delayTickConfig
    )
    default int tickDelayMax() {
        return 3;
    }

    @ConfigItem(
            keyName = "tickDelayEnabled",
            name = "Tick delay",
            description = "enables some tick delays",
            position = 4,
            section = delayTickConfig
    )
    default boolean tickDelay() {
        return true;
    }

    @ConfigSection(
            name = "Auto Rifts Configuration",
            description = "Configure your settings for the AutoRifts plugin",
            position = 2,
            closedByDefault = true
    )
    String autoRiftsConfig = "autoRiftsConfig";

    @ConfigItem(
            keyName = "startFrags",
            name = "Starting Fragments",
            description = "How many fragments you should get before leaving the starting zone",
            position = 1,
            section = autoRiftsConfig
    )
    default int startingFrags() {
        return 60;
    }

    @ConfigItem(
            keyName = "minFrags",
            name = "Minimum Fragments",
            description = "When you should mine more fragments",
            position = 2,
            section = autoRiftsConfig
    )
    default int minFrags() {
        return 24;
    }

    @ConfigItem(
            keyName = "ignorePortal",
            name = "Ignore Portal Ess",
            description = "How much essence you should have to ignore portal",
            position = 3,
            section = autoRiftsConfig
    )
    default int ignorePortal() {
        return 20;
    }

    @ConfigItem(
            keyName = "dropRunes",
            name = "Drop Runes",
            description = "Drop Runes instead of depositing (kek uim)",
            position = 4,
            section = autoRiftsConfig
    )
    default boolean dropRunes() {
        return false;
    }

    @ConfigItem(
            keyName = "dropRunesFilter",
            name = "Drop Runes Filter",
            description = "If Drop Runes is not enabled and this has runes entered, the type of rune entered here will still get dropped, others will get deposited (ex: air, Mind, Body). Add runes with full name, air rune, mind rune , cosmic rune, etc... and split with comma ','",
            position = 5,
            section = autoRiftsConfig
    )
    default String dropRunesFilter() {return "";}

    @ConfigItem(
            keyName = "usePouches",
            name = "Use Essence Pouches?",
            description = "Requires NPC Contact runes in Rune Pouch or Redwood lit Lantern",
            position = 6,
            section = autoRiftsConfig
    )
    default boolean usePouches() {
        return false;
    }

    @ConfigItem(
            keyName = "hasBook",
            name = "Abyssal Book in bank? (IMPORTANT FOR NPC CONTACT)",
            description = "IMPORTANT TO USE NPC CONTACT",
            position = 7,
            section = autoRiftsConfig
    )
    default boolean hasBook() {
        return true;
    }

    @ConfigItem(
            keyName = "prioritizeCatalytic",
            name = "Prioritizes Catalytic Energy",
            description = "Will try to balance points if not ticked",
            position = 8,
            section = autoRiftsConfig
    )
    default boolean prioritizeCatalytic() {
        return true;
    }

    @ConfigItem(
            keyName = "prioritizeHigher",
            name = "Prioritize Higher Tier Runes(BETA)",
            description = "Prioritizes Nature/Law/Death/Blood even if points arent balanced - Expect some bugs",
            position = 9,
            section = autoRiftsConfig
    )
    default boolean prioritizeHighTier() {
        return true;
    }

    @ConfigItem(
            keyName = "prioritizePortal",
            name = "Prioritize Portal(BETA)",
            description = "Prioritizes Portal, mainly affects when to drop/deposit runes - Expect some bugs",
            position = 10,
            section = autoRiftsConfig
    )
    default boolean prioritizePortal() {
        return true;
    }
}
