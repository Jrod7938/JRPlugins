package com.piggyplugins.VardorvisHelper;

import com.example.EthanApiPlugin.Collections.NPCs;
import com.google.inject.Provides;
import com.piggyplugins.PiggyUtils.API.PrayerUtil;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.Prayer;
import net.runelite.api.SpriteID;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

@Slf4j
@PluginDescriptor(
        name = "<html><font color=\"#FF9DF9\">[PP]</font>  Vardorvis Helper</html>",
        description = "Tells you what to pray against or auto prays at vardovis"
)
public class VardorvisHelperPlugin extends Plugin {

    private static final String VARDOVIS = "Vardorvis";
    private static final String VARDOVIS_HEAD = "Vardorvis' Head";

    private int rangeTicks = 0;
    private int mageTicks = 0;

    @Inject
    private Client client;
    @Inject
    private SpriteManager spriteManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private VardorvisHelperOverlay overlay;
    @Inject
    private VardorvisHelperConfig config;

    @Provides
    private VardorvisHelperConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(VardorvisHelperConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(overlay);
    }

    private void handleGameTick() {
        if (rangeTicks > 0) {
            if (!PrayerUtil.isPrayerActive(Prayer.PROTECT_FROM_MISSILES)) {
                PrayerUtil.togglePrayer(Prayer.PROTECT_FROM_MISSILES);
            }
        } else {
            if (!PrayerUtil.isPrayerActive(Prayer.PROTECT_FROM_MELEE)) {
                PrayerUtil.togglePrayer(Prayer.PROTECT_FROM_MELEE);
            }
        }
    }

    private void handleAwakenedGameTick() {
        if (mageTicks > 0) {
            if (!PrayerUtil.isPrayerActive(Prayer.PROTECT_FROM_MAGIC)) {
                PrayerUtil.togglePrayer(Prayer.PROTECT_FROM_MAGIC);
            }
        } else if (rangeTicks > 0) {
            if (!PrayerUtil.isPrayerActive(Prayer.PROTECT_FROM_MISSILES)) {
                PrayerUtil.togglePrayer(Prayer.PROTECT_FROM_MISSILES);
            }
        } else {
            if (!PrayerUtil.isPrayerActive(Prayer.PROTECT_FROM_MELEE)) {
                PrayerUtil.togglePrayer(Prayer.PROTECT_FROM_MELEE);
            }
        }
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (client.getGameState() != GameState.LOGGED_IN || !isInFight()) {
            return;
        }

        if (mageTicks > 0) {
            mageTicks--;
        }

        if (rangeTicks > 0) {
            rangeTicks--;
        }

        if (config.autoPray()) {
            if (config.awakened()) {
                handleAwakenedGameTick();
            } else {
                handleGameTick();
            }
        }
    }

    @Subscribe
    private void onNpcSpawned(NpcSpawned event) {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        if (event.getActor() instanceof NPC) {
            if (event.getActor().getName() == null) return;
            if (event.getActor().getName().equals(VARDOVIS_HEAD)) {
                if (config.awakened()) {
                    if (mageTicks == 0) {
                        mageTicks = 4;
                    } else {
                        rangeTicks = 4;
                    }
                } else {
                    rangeTicks = 4;
                }
            }
        }
    }


    public int getPrayerSkill() {
        if (rangeTicks > 0) {
            return SpriteID.PRAYER_PROTECT_FROM_MISSILES;
        }

        return SpriteID.PRAYER_PROTECT_FROM_MELEE;
    }

    public Prayer getCorrectPrayer() {
        if (mageTicks > 0) {
            return Prayer.PROTECT_FROM_MAGIC;
        }

        if (rangeTicks > 0) {
            return Prayer.PROTECT_FROM_MISSILES;
        }

        return Prayer.PROTECT_FROM_MELEE;
    }

    public boolean isInFight() {
        return client.isInInstancedRegion() && NPCs.search().nameContains(VARDOVIS).nearestToPlayer().isPresent();
    }
}
