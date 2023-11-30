package com.piggyplugins.CannonReloader;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.InventoryInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.google.inject.Provides;
import com.piggyplugins.PiggyUtils.API.InventoryUtil;
import com.piggyplugins.PiggyUtils.API.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.TileObject;
import net.runelite.api.VarPlayer;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.Widget;
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
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
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
        tileOverlay = new CannonReloaderTileOverlay(client, this, config);
        overlayManager.add(tileOverlay);
        remainingCannonballs = 0;
        timeout = 0;
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(tileOverlay);
        remainingCannonballs = 0;
        timeout = 0;
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        playerLocation = client.getLocalPlayer().getWorldLocation();
        safespotTile = getCoords(config.SafespotCoords());
        cannonSpot = getCoords(config.CannonCoords());

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
        if (config.UseSafespot() && !isStandingOnSafespot()) {
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
                remainingCannonballs <= config.CannonLowAmount();
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

    private WorldPoint getCoords(String coords) {
        List<Integer> configCoords = Arrays.stream(coords.split(",")).map(Integer::parseInt).collect(Collectors.toList());
        return new WorldPoint(configCoords.get(0), configCoords.get(1), client.getLocalPlayer().getWorldLocation().getPlane());
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged varbitChanged) {
        if (varbitChanged.getVarpId() == VarPlayer.CANNON_AMMO) {
            remainingCannonballs = varbitChanged.getValue();
        }
    }
}
