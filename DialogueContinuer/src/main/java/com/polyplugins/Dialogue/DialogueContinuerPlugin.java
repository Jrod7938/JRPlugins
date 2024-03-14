package com.polyplugins.Dialogue;


import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.Widgets;
import com.example.InteractionApi.InventoryInteraction;
import com.example.InteractionApi.NPCInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.util.Optional;

@PluginDescriptor(
        name = "<html><font color=\"#7ecbf2\">[PJ]</font>Misc Handler</html>",
        description = "Handles miscellaneous stuff in the game",
        enabledByDefault = false,
        tags = {"poly", "plugin"}
)
@Slf4j
public class DialogueContinuerPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private DialogueContinuerConfig config;

    public int timeout = 0;

    @Override
    protected void startUp() throws Exception {
        timeout = 0;
    }

    @Override
    protected void shutDown() throws Exception {
        timeout = 0;
    }

    @Provides
    DialogueContinuerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(DialogueContinuerConfig.class);
    }


    @Subscribe
    private void onGameTick(GameTick event) {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        if (timeout > 0) {
            timeout--;
            return;
        }
        if (config.continueDialogue() && continueDialogue()) {
            log.info("continue 1");
            return; //do not dismiss randoms until dialogue is finished
        }

        if (config.dismissRandoms()) {
            dismissRandoms();
        }

        Inventory.search().onlyUnnoted().withName("Vial").filter(v -> config.dropEmptyVials()).first().ifPresent(vial -> {
            InventoryInteraction.useItem(vial, "Drop");
        });

        Inventory.search().onlyUnnoted().withName("Jug").filter(b -> config.dropEmptyWineJugs()).first().ifPresent(bone -> {
            InventoryInteraction.useItem(bone, "Drop");
        });

    }

    private void dismissRandoms() {
        NPCs.search().alive().walkable().withAction("Dismiss").interactingWithLocal().first().ifPresent(npc -> {
//            log.info("Dismissing random: " + npc.getName());
            timeout = 2;
            NPCInteraction.interact(npc, "Dismiss");
        });
    }

    private boolean continueDialogue() {
        Optional<Widget> questHelperOpt = Widgets.search().withParentId(14352385).withTextContains("[").withTextContains("]").first();
        if (questHelperOpt.isPresent()) {
            Widget qhWidget = questHelperOpt.get();
            String text = qhWidget.getText();
            int index = text.indexOf("]");
            String option = text.substring(index - 1, index).trim();
//            log.info("Dialogue option: " + option);
            MousePackets.queueClickPacket();
            WidgetPackets.queueResumePause(qhWidget.getId(), Integer.parseInt(option));
            timeout = 0;
            return true;
        }
//        Widgets.search().withParentId(14352385).withTextContains("[").withTextContains("]").first().ifPresent(widget -> {
//            String text = widget.getText();
//            int index = text.indexOf("]");
//            String option = text.substring(index - 1, index).trim();
//            log.info("Dialogue option: " + option);
//            MousePackets.queueClickPacket();
//            WidgetPackets.queueResumePause(widget.getId(), Integer.parseInt(option));
//            timeout = 0;
//        });
        Optional<Widget> mainContinueOpt = Widgets.search().withTextContains("Click here to continue").first();
        if (mainContinueOpt.isPresent()) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueResumePause(mainContinueOpt.get().getId(), -1);
            timeout = 0;
            return true;
        }

        Optional<Widget> continue1Opt = Widgets.search().withId(12648448).hiddenState(false).first();
        if (continue1Opt.isPresent()) {
            log.info("continue 1");
            MousePackets.queueClickPacket();
            WidgetPackets.queueResumePause(continue1Opt.get().getId(), 1);
            return true;
        }
        Optional<Widget> continue2Opt = Widgets.search().withId(41484288).hiddenState(false).first();
        if (continue2Opt.isPresent()) {
            log.info("continue 2");
            MousePackets.queueClickPacket();
            WidgetPackets.queueResumePause(continue2Opt.get().getId(), 1);
            return true;
        }
//        log.info("ret false");
        return false;
    }

}