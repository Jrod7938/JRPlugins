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
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;

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

    private final Map<NPC, Integer> jadMap = new HashMap<>();
    private final Queue<Prayer> nextPrayers = new PriorityQueue<>();
    private Prayer shouldPray = Prayer.PROTECT_FROM_MAGIC;

    @Provides
    private JadAutoPrayersConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(JadAutoPrayersConfig.class);
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (!inFight()) {
            return;
        }

        for (Map.Entry<NPC, Integer> entry : jadMap.entrySet()) {
            if (entry.getKey().isDead()) {
                continue;
            }

            entry.setValue(entry.getValue()+1);
        }

        if (!nextPrayers.isEmpty()) {
            jadMap.forEach((key, value) -> {
                if (value == 3) {
                    shouldPray = nextPrayers.poll();
                }
            });
        }

        if (config.oneTickFlick()) {
            oneTickFlick();
        } else {
            autoPrayer();
        }
    }

    private void oneTickFlick() {
        if (PrayerUtil.isPrayerActive(shouldPray)) {
            PrayerUtil.togglePrayer(shouldPray);
        }
        PrayerUtil.togglePrayer(shouldPray);
    }

    private void autoPrayer() {
        if (!PrayerUtil.isPrayerActive(shouldPray)) {
            PrayerUtil.togglePrayer(shouldPray);
        }
    }

    @Subscribe
    private void onNpcSpawned(NpcSpawned event) {
        if (event.getNpc().getName().toLowerCase().contains("jad")) {
            jadMap.put(event.getNpc(), 0);
        }
    }


    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        if (!(event.getActor() instanceof NPC)) {
            return;
        }

        if (event.getActor().getName() == null) {
            return;
        }

        if (event.getActor().getName().toLowerCase().contains("jad")) {
            this.setupJadPrayers((NPC) event.getActor());
        }
    }

    private void setupJadPrayers(NPC npc) {
        int animationID = EthanApiPlugin.getAnimation(npc);
        if (animationID == 2656 || animationID == 7592) {
            if (jadMap.containsKey(npc)) {
                jadMap.replace(npc, 0);
            }
            nextPrayers.add(Prayer.PROTECT_FROM_MAGIC);
        } else if (animationID == 2652 || animationID == 7593) {
            if (jadMap.containsKey(npc)) {
                jadMap.replace(npc, 0);
            }
            nextPrayers.add(Prayer.PROTECT_FROM_MISSILES);
        }
    }

    private boolean inFight() {
        return NpcUtil.nameContainsNoCase("-jad").nearestToPlayer().isPresent();
    }
}
