/*
 * Copyright (c) 2024. By Jrod7938
 *
 */
package com.jrplugins.autoVorkath;

import com.jrplugins.autoVorkath.enums.*;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

@ConfigGroup("AutoVorkath")
public interface AutoVorkathConfig extends Config {

    @ConfigItem(
            keyName = "crossbow",
            name = "Crossbow",
            description = "Choose your crossbow",
            position = 0
    )
    default CROSSBOW CROSSBOW() {
        return CROSSBOW.DRAGON_HUNTER_CROSSBOW;
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
            keyName = "portal",
            name = "Portal",
            description = "What Portal to use to teleport to Lunar Isle.",
            position = 3
    )
    default PORTAL PORTAL() {
        return PORTAL.PORTAL_NEXUS;
    }

    @ConfigItem(
            keyName = "rigour",
            name = "Rigour",
            description = "Activate Rigour?",
            position = 4
    )
    default boolean ACTIVATERIGOUR() {
        return true;
    }

    @ConfigItem(
            keyName = "switchBolts",
            name = "Switch Bolts",
            description = "Change to Diamond Bolts (e) at 20% health?",
            position = 5
    )
    default boolean SWITCHBOLTS() {
        return true;
    }

    @ConfigItem(
            keyName = "rangePotion",
            name = "Ranging Potion",
            description = "What Ranging potion to use?",
            position = 6
    )
    default RANGE_POTION RANGEPOTION() {
        return RANGE_POTION.DIVINE_RANGING_POTION;
    }

    @ConfigItem(
            keyName = "prayerPotion",
            name = "Prayer Potion",
            description = "What Prayer potion to use?",
            position = 7
    )
    default PRAYER_POTION PRAYERPOTION() {
        return PRAYER_POTION.PRAYER;
    }

    @ConfigItem(
            keyName = "antiFirePotion",
            name = "Antifire Potion",
            description = "What Antifire potion to use?",
            position = 8
    )
    default ANTIFIRE ANTIFIRE() {
        return ANTIFIRE.EXTENDED_SUPER_ANTIFIRE;
    }

    @ConfigItem(
            keyName = "runePouch",
            name = "Rune Pouch",
            description = "What Rune Pouch to use?",
            position = 9
    )
    default RUNEPOUCH RUNEPOUCH() {
        return RUNEPOUCH.RUNE_POUCH;
    }

    @ConfigItem(
            keyName = "foodAmount",
            name = "Food Amount",
            description = "MAX FOOD : MIN FOOD",
            position = 10
    )
    default Dimension FOODAMOUNT() {
        return new Dimension(16, 15);
    }

    @ConfigItem(
            keyName = "poolDrinkat",
            name = "Ornate Pool Drink",
            description = "HEALTH : PRAYER",
            position = 11
    )
    default Dimension POOLDRINK() {
        return new Dimension(90, 90);
    }

    @Range(min = 40, max = 90)
    @ConfigItem(
            keyName = "eatat",
            name = "Eat At?",
            description = "Eat at what health?",
            position = 12
    )
    default int EATAT() {
        return 75;
    }

    @Range(min = 40, max = 90)
    @ConfigItem(
            keyName = "drinkAt",
            name = "Drink Prayer At?",
            description = "Drink at what prayer?",
            position = 13
    )
    default int DRINKPRAYERAT() {
        return 70;
    }

    @Range(
            min = 1,
            max = 2000
    )
    @ConfigItem(
            keyName = "sellAt",
            name = "Sell At?",
            description = "Sell items at what kill?",
            position = 14
    )
    default int SELLAT() {
        return 15;
    }

    @ConfigItem(
            keyName = "mule",
            name = "Mule GP?",
            description = "Trade GP after selling? (TARGET MUST BE IN GE)",
            position = 15
    )
    default boolean MULE() {
        return false;
    }

    @ConfigItem(
            keyName = "muleName",
            name = "Mule Name",
            description = "Name of player in GE to trade.",
            position = 16
    )
    default String MULENAME() {
        return "";
    }

    @ConfigItem(
            keyName = "food",
            name = "Food",
            description = "What food to use? (NOT MANTA RAY!)",
            position = 17
    )
    default String FOOD() {
        return "Shark";
    }
}
