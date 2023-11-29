package com.polyplugins.Dialogue;


import com.example.EthanApiPlugin.Collections.Widgets;
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
        name = "<html><font color=\"#7ecbf2\">[PJ]</font>Dialogue Continuer</html>",
        description = "Continues conversation and automates quest helper dialogue",
        enabledByDefault = false,
        tags = {"poly", "plugin"}
)
@Slf4j
public class DialogueContinuerPlugin extends Plugin {
    @Inject
    private Client client;
    public int timeout = 0;

    @Override
    protected void startUp() throws Exception {
        timeout = 0;
    }

    @Override
    protected void shutDown() throws Exception {
        timeout = 0;
    }


    @Subscribe
    private void onGameTick(GameTick event) {
        if (timeout > 0) {
            timeout--;
            return;
        }
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        Widgets.search().withParentId(14352385).withTextContains("[").withTextContains("]").first().ifPresent(widget -> {
            String text = widget.getText();
            int index = text.indexOf("]");
            String option = text.substring(index - 1, index).trim();
            log.info("Dialogue option: " + option);
            MousePackets.queueClickPacket();
            WidgetPackets.queueResumePause(widget.getId(), Integer.parseInt(option));
            timeout = 0;
        });
        Widgets.search().withTextContains("Click here to continue").first().ifPresent(widget -> {

            MousePackets.queueClickPacket();
            WidgetPackets.queueResumePause(widget.getId(), -1);
            timeout = 0;
        });

    }

}