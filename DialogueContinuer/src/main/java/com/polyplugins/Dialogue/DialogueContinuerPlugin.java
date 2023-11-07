package com.polyplugins.Dialogue;


import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.*;
import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

@PluginDescriptor(
        name = "DialogueContinuer",
        description = "Continues conversation and automates quest helper dialogue",
        enabledByDefault = false,
        tags = {"poly", "plugin"}
)
@Slf4j
public class DialogueContinuerPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private DialogueContinuerConfig config;
    @Inject
    private DialogueContinuerOverlay overlay;
    @Inject
    private KeyManager keyManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ClientThread clientThread;
    private boolean started = false;
    public int timeout = 0;

    @Provides
    private DialogueContinuerConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(DialogueContinuerConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        keyManager.registerKeyListener(toggle);
        overlayManager.add(overlay);
        timeout = 0;
    }
    @Override
    protected void shutDown() throws Exception {
        keyManager.unregisterKeyListener(toggle);
        overlayManager.remove(overlay);
        timeout = 0;
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

    }

    private void checkRunEnergy() {
        if (runIsOff() && client.getEnergy() >= 30 * 100) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetActionPacket(1, 10485787, -1, -1);
        }
    }

    private boolean runIsOff() {
        return EthanApiPlugin.getClient().getVarpValue(173) == 0;
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