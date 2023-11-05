package com.polyplugins.Butterfly;


import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.*;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.piggyplugins.PiggyUtils.API.PlayerUtil;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.HotkeyListener;

import java.util.List;
import java.util.Optional;

@PluginDescriptor(
        name = "Butterfly Catcher",
        description = "Catches and releases butterflies",
        enabledByDefault = false,
        tags = {"poly", "plugin"}
)
@Slf4j
public class ButterflyPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private ButterflyConfig config;
    @Inject
    private KeyManager keyManager;
    private boolean started = false;
    public int timeout = 0;

    @Provides
    private ButterflyConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(ButterflyConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        keyManager.registerKeyListener(toggle);
        timeout = 0;
    }

    @Override
    protected void shutDown() throws Exception {
        keyManager.unregisterKeyListener(toggle);
        timeout = 0;
        started = false;
    }


    @Subscribe
    private void onGameTick(GameTick event) {
        if (timeout > 0) {
            timeout--;
            return;
        }
        if (client.getGameState() != GameState.LOGGED_IN || !started) {
            return;
        }
        doButterfly();

    }

    private void doButterfly() {
        Optional<NPC> butterfly = NPCs.search().withName(config.butterfly()).withAction("Catch").nearestToPlayer();
        List<Widget> filledJars = Inventory.search().withAction("Release").withName("Ruby harvest").result();
        Optional<Widget> emptyJar = Inventory.search().withName("Butterfly jar").first();

        checkRunEnergy();

        if (!filledJars.isEmpty()) {
            filledJars.forEach(jar -> {
                log.info("RELEASING BUTTERFLY");
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetAction(jar, "Release");
            });
        }

        if (client.getLocalPlayer().getInteracting() == null && emptyJar.isPresent()) {
            if (butterfly.isPresent()) {
                log.info("CATCHING BUTTERFLY");
                MousePackets.queueClickPacket();
                NPCPackets.queueNPCAction(butterfly.get(), "Catch");
            }
        }
        //1 tick delay after sipping stamina
        if (timeout == 0)
            timeout = config.tickDelay();
    }


    private void checkRunEnergy() {
        if (PlayerUtil.isRunning() && PlayerUtil.runEnergy() <= 10) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetActionPacket(1, 10485787, -1, -1);
        }
        checkStamina();
    }

    private void checkStamina() {
        if (!PlayerUtil.isStaminaActive() && PlayerUtil.runEnergy() <= 70) {
            Inventory.search().onlyUnnoted().nameContains("Stamina pot").withAction("Drink").first().ifPresent(stamina -> {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetAction(stamina, "Drink");
                timeout = 1;
            });
        }
    }

    private final HotkeyListener toggle = new HotkeyListener(() -> config.toggle()) {
        @Override
        public void hotkeyPressed() {
            toggle();
        }
    };

    public void toggle() {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        started = !started;
    }
}