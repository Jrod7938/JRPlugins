package com.polyplugins.AutoCombat;


import net.runelite.client.config.*;

@ConfigGroup("AutoCombatConfig")
public interface AutoCombatConfig extends Config {
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
            name = "Auto Combat Configuration",
            description = "Configure how to handles game tick delays, 1 game tick equates to roughly 600ms",
            position = 2,
            closedByDefault = false
    )
    String autoCombatConfig = "autoCombatConfig";

    @ConfigItem(
            keyName = "useCombatPotion",
            name = "Combat potions?",
            description = "Uses regular or super combat potions",
            position = 1,
            section = autoCombatConfig
    )
    default boolean useCombatPotion() {
        return true;
    }

    @Range(
            min = 1,
            max = 99
    )
    @ConfigItem(
            keyName = "useCombatAt",
            name = "Use at",
            description = "What level to use combat potions at",
            position = 2,
            section = autoCombatConfig
    )

    default int useCombatPotAt() {
        return 80;
    }

    @ConfigItem(
            keyName = "usePrayerPotion",
            name = "Prayer potions?",
            description = "Uses prayer potions",
            position = 3,
            section = autoCombatConfig
    )
    default boolean usePrayerPotion() {
        return true;
    }

    @Range(
            min = 1,
            max = 99
    )
    @ConfigItem(
            keyName = "usePrayerAt",
            name = "Use at",
            description = "What level to use prayer potions at, prayer or super restore",
            position = 4,
            section = autoCombatConfig
    )

    default int usePrayerPotAt() {
        return 20;
    }

    @ConfigItem(
            keyName = "targetName",
            name = "Target name",
            description = "",
            position = 5,
            section = autoCombatConfig
    )
    default String targetName() {
        return "Chicken";
    }

    @Range(
            min = 2,
            max = 90
    )
    @ConfigItem(keyName = "eatAt",
            name = "Eat at",
            description = "What HP to eat at",
            position = 7,
            section = autoCombatConfig)
    default int eatAt() {
        return 50;
    }

    @ConfigItem(keyName = "shutdownOnTaskDone",
            name = "Stop when task done?",
            description = "Teleports away and stops. Untick if you have no task or want to stay",
            position = 8,
            section = autoCombatConfig)
    default boolean shutdownOnTaskDone() {
        return false;
    }

    @ConfigItem(keyName = "buryBones",
            name = "Bury bones?",
            description = "Will bury ANY bone in your inventory",
            position = 9,
            section = autoCombatConfig)
    default boolean buryBones() {
        return false;
    }

    @ConfigSection(
            name = "Looting Configuration",
            description = "Configure how to handle looting",
            position = 2,
            closedByDefault = false
    )
    String lootingConfig = "lootingConfig";

    @ConfigItem(
            keyName = "lootEnabled",
            name = "Loot?",
            description = "Loots items",
            position = 1,
            section = lootingConfig
    )
    default boolean lootEnabled() {
        return true;
    }

    @ConfigItem(
            keyName = "lootNames",
            name = "Loot names",
            description = "",
            position = 3,
            section = lootingConfig
    )
    default String lootNames() {
        return "Feather";
    }
}

