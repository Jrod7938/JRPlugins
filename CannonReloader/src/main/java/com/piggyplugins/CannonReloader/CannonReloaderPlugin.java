package com.piggyplugins.CannonReloader;

import com.example.EthanApiPlugin.Collections.Bank;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.BankInteraction;
import com.example.InteractionApi.InventoryInteraction;
import com.example.InteractionApi.NPCInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.WidgetPackets;
import com.google.inject.Provides;
import com.piggyplugins.PiggyUtils.API.InventoryUtil;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import com.google.inject.Inject;
import net.runelite.client.ui.overlay.OverlayManager;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@PluginDescriptor(
        name = "<html><font color=\"#FF9DF9\">[PP]</font> Cannon Reloader</html>",
        description = "Automatically reloads your cannon when out of cannonballs",
        enabledByDefault = false,
        tags = {"ethan", "piggy"}
)
@Slf4j
public class CannonReloaderPlugin extends Plugin {

    @Inject
    private ItemManager itemManager;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    CannonReloaderConfig config;
    @Inject
    private OverlayManager overlayManager;

    CannonReloaderTileOverlay tileOverlay;

    WorldPoint playerLocation;
    int timeout = 0;
    boolean cannonFired;
    WorldPoint cannonSpot;
    WorldPoint safespotTile;
    private int remainingCannonballs;

    @Provides
    CannonReloaderConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(CannonReloaderConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        if (client.getGameState() != GameState.LOGGED_IN) {
            clientThread.invoke(() -> {
                EthanApiPlugin.stopPlugin(this);
            });
            return;
        }
        tileOverlay = new CannonReloaderTileOverlay(client, this, config);
        overlayManager.add(tileOverlay);
        remainingCannonballs = 0;
        timeout = 0;
        clientThread.invokeLater(() -> {
            EthanApiPlugin.sendClientMessage("[Cannon Reloader] Make sure you set your cannon and safespot locations!");
            EthanApiPlugin.sendClientMessage("[Cannon Reloader] Right click the cannon and the safe spot to set your desired locations.");
        });
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(tileOverlay);
        remainingCannonballs = 0;
        timeout = 0;
        cannonSpot = null;
        safespotTile = null;

    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        if (client.getGameState() != GameState.LOGGED_IN)
            return;
        if (event.getOption().equals("Fire")) {
            MenuEntry cannonEntry = client.createMenuEntry(-1).setOption("Set Cannon Location")
                    .setTarget("").setType(MenuAction.RUNELITE)
                    .onClick(e -> {
                        Tile target = client.getSelectedSceneTile();
                        if (target != null)
                            cannonSpot = target.getWorldLocation();
                    });
        }
        if (event.getOption().equals("Walk here")) {
            MenuEntry safespotEntry = client.createMenuEntry(-2)
                    .setOption("Set Safespot Location")
                    .setTarget("")
                    .setType(MenuAction.RUNELITE)
                    .onClick(e -> {
                        Tile target = client.getSelectedSceneTile();
                        if (target != null)
                            safespotTile = target.getWorldLocation();

                    });
        }
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        playerLocation = client.getLocalPlayer().getWorldLocation();
        if (cannonSpot == null || (config.useSafespot() && safespotTile == null)) {
            return;
        }
//        safespotTile = getCoords(config.SafespotCoords());
//        log.info("canon spot: " + cannonSpot);
//        log.info("safespot: " + safespotTile);
//        cannonSpot = getCoords(config.CannonCoords());

        if (timeout > 0) {
            timeout--;
            return;
        }
        //Out of cannonballs, pick up cannon
        if (Inventory.search().withName("Cannonball").first().isEmpty() && isCannonSetUp()) {
            handlePickUpCannon();
            return;
        }
        //Cannon has not been fired for the first time yet or needs to repair/reload
        if (needsToRepairOrReloadCannon() || !cannonFired) {
            handleCannon();
            return;
        }
        //if safespot is activated, handle walking to safespot
        if (config.useSafespot() && !isStandingOnSafespot()) {
            handleSafespot();
        }
    }

    private void handleSafespot() {
        if (safespotTile != null && !EthanApiPlugin.isMoving()) {
            MousePackets.queueClickPacket();
            MovementPackets.queueMovement(safespotTile);
            timeout = 3;
        }
    }

    private void handleCannon() {
        if (TileObjects.search().atLocation(cannonSpot).withName("Dwarf multicannon").first().isEmpty()) {
            if (playerLocation.distanceTo(cannonSpot) != 0) {
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(cannonSpot);
                return;
            }
            Inventory.search().withAction("Set-up").withName("Cannon base").first().ifPresent(x -> {
                InventoryInteraction.useItem(x, "Set-up");
                cannonFired = false;
                timeout = 13;
            });
            return;
        }
        if (!cannonFired) { // Fires cannon first time
            TileObjects.search().atLocation(cannonSpot).withName("Dwarf multicannon").withAction("Fire").first().ifPresent(x -> {
                TileObjectInteraction.interact(x, "Fire");
                cannonFired = true;
                timeout = 3;
            });
            return;
        }

        TileObjects.search().atLocation(cannonSpot).withName("Dwarf multicannon").withAction("Repair").first().ifPresent(x -> {
            TileObjectInteraction.interact(x, "Repair");
            timeout = 3;
        });

        TileObjects.search().atLocation(cannonSpot).withName("Dwarf multicannon").withAction("Fire").first().ifPresent(x -> {
            TileObjectInteraction.interact(x, "Fire");
            timeout = 3;
        });
    }

    private boolean isStandingOnSafespot() {
        return playerLocation.distanceTo(safespotTile) == 0;
    }


    private boolean needsToRepairOrReloadCannon() {
        return TileObjects.search().atLocation(cannonSpot).withName("Dwarf multicannon").withAction("Repair").first().isPresent() ||
                remainingCannonballs <= config.cannonLowAmount();
    }

    private boolean isCannonSetUp() {
        return TileObjects.search().atLocation(cannonSpot).withName("Dwarf multicannon").withAction("Repair").first().isPresent() ||
                TileObjects.search().atLocation(cannonSpot).withName("Dwarf multicannon").withAction("Fire").first().isPresent();
    }

    private void handlePickUpCannon() {
        if (Inventory.getEmptySlots() >= 4) {
            TileObjects.search().atLocation(cannonSpot).withName("Dwarf multicannon").withAction("Pick-up").first().ifPresent(x -> {
                TileObjectInteraction.interact(x, "Pick-up");
            });
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged varbitChanged) {
        if (varbitChanged.getVarpId() == VarPlayer.CANNON_AMMO) {
            remainingCannonballs = varbitChanged.getValue();
        }
    }
}
