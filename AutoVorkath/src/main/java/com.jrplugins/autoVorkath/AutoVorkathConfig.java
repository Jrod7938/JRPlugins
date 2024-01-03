package com.jrplugins.autoVorkath;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("AutoVorkath")
public interface AutoVorkathConfig extends Config {

    @ConfigItem(
            keyName = "crossbow",
            name = "Crossbow",
            description = "Choose your crossbow",
            position = 0
    )
    default CROSSBOW CROSSBOW() {
        return CROSSBOW.ARMADYL_CROSSBOW;
    }

    @ConfigItem(
            keyName = "slayersStaff",
            name = "Slayers Staff",
            description = "Choose your slayers staff",
            position = 1
    )
    default STAFF SLAYERSTAFF() {
        return STAFF.SLAYER_STAFF;
    }

    @ConfigItem(
            keyName = "teleport",
            name = "Teleport",
            description = "Choose your teleport",
            position = 2
    )
    default TELEPORT TELEPORT() {
        return TELEPORT.CONSTRUCT_CAPE_T;
    }

    @ConfigItem(
            keyName = "rigour",
            name = "Rigour",
            description = "Activate Rigour?",
            position = 3
    )
    default boolean ACTIVATERIGOUR() {
        return true;
    }

    @ConfigItem(
            keyName = "rangePotion",
            name = "Ranging Potion",
            description = "What Ranging potion to use?",
            position = 4
    )
    default RANGE_POTION RANGEPOTION() {
        return RANGE_POTION.DIVINE_RANGING_POTION;
    }

    @ConfigItem(
            keyName = "prayerPotion",
            name = "Prayer Potion",
            description = "What Prayer potion to use?",
            position = 5
    )
    default PRAYER_POTION PRAYERPOTION() {
        return PRAYER_POTION.PRAYER;
    }

    @ConfigItem(
            keyName = "food",
            name = "Food",
            description = "What food to use?",
            position = 6
    )
    default String FOOD() {
        return "Manta ray";
    }

    @ConfigItem(
            keyName = "Loot",
            name = "Loot Names",
            description = "Enter the name of the loot you want to pick up",
            position = 7
    )
    default String LOOTNAMES() {
        return "Green dragonhide,Blue dragonhide,Superior dragon bones,Battlestaff,Diamond,Dragonstone bolt tips,Chaos rune,Black dragonhide,Dragon bones,Dragon plateskirt,Red dragonhide,Grapes,Magic logs,Coins,Onyx bolt tips,Rune kiteshield,Loop half of key,Death rune,Adamantite ore,Manta ray,Rune longsword,Dragon bolts(unf),Dragon longsword,Dragon platelegs,Dragonbone necklace,Draconic visage,Skeletal visage,Jar of decay";
    }
}
