package com.piggyplugins.JadAutoPrayers;

import com.example.EthanApiPlugin.EthanApiPlugin;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.piggyplugins.PiggyUtils.API.NpcUtil;
import com.piggyplugins.PiggyUtils.API.PrayerUtil;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Prayer;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@PluginDescriptor(
        name = "<html><font color=\"#FF9DF9\">[PP]</font> Jad Auto Prayers</html>",
        description = "Automatically switches & flicks prayer at Jad (multiple jads not supported)",
        tags = {"ethan", "piggy"}
)
@Slf4j
public class JadAutoPrayersPlugin extends Plugin {

    @Inject
    private Client client;
    @Inject
    private JadAutoPrayersConfig config;

    private Prayer shouldPray = Prayer.PROTECT_FROM_MELEE;
    private final List<Prayer> extraPrayerList = new ArrayList<>();
    private int ticksSinceAnimation = 0;

    @Provides
    private JadAutoPrayersConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(JadAutoPrayersConfig.class);
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (!inFight()) {
            return;
        }

        ticksSinceAnimation++;

        if (ticksSinceAnimation > 3) {
            // back to melee cus healers? :shrug:
            shouldPray = Prayer.PROTECT_FROM_MELEE;
        }

        if (config.eagleEye() || config.rigour()) {
            oneTickMultiFlick();
        } else {
            oneTickFlick();
        }
    }

    private void oneTickFlick() {
        if (PrayerUtil.isPrayerActive(shouldPray)) {
            PrayerUtil.togglePrayer(shouldPray);
        }
        PrayerUtil.togglePrayer(shouldPray);
    }

    private void oneTickMultiFlick() {
        if (config.eagleEye()) {
            if (PrayerUtil.isPrayerActive(shouldPray)) {
                PrayerUtil.toggleMultiplePrayers(shouldPray, Prayer.EAGLE_EYE);
            }
            PrayerUtil.toggleMultiplePrayers(shouldPray, Prayer.EAGLE_EYE);
            return;
        }

        if (config.rigour()) {
            if (PrayerUtil.isPrayerActive(shouldPray)) {
                PrayerUtil.toggleMultiplePrayers(shouldPray, Prayer.RIGOUR);
            }
            PrayerUtil.toggleMultiplePrayers(shouldPray, Prayer.RIGOUR);
        }
    }


    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        this.setupJadPrayers();
    }

    private void setupJadPrayers() {
        Optional<NPC> jad = NpcUtil.nameContainsNoCase("-jad").nearestToPlayer();

        if (jad.isEmpty()) {
            return;
        }

        int animationID = EthanApiPlugin.getAnimation(jad.get());
        if (animationID == 2656 || animationID == 7592) {
            ticksSinceAnimation = 0;
            shouldPray = Prayer.PROTECT_FROM_MAGIC;
        } else if (animationID == 2652 || animationID == 7593) {
            ticksSinceAnimation = 0;
            shouldPray = Prayer.PROTECT_FROM_MISSILES;
        }
    }

    private boolean inFight() {
        return NpcUtil.nameContainsNoCase("-jad").nearestToPlayer().isPresent();
    }
}
