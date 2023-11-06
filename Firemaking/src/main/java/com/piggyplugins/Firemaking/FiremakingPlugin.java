package com.piggyplugins.Firemaking;

import com.example.EthanApiPlugin.Collections.*;
import com.example.EthanApiPlugin.Collections.query.ItemQuery;
import com.example.EthanApiPlugin.Collections.query.NPCQuery;
import com.example.EthanApiPlugin.Collections.query.TileObjectQuery;
import com.example.EthanApiPlugin.Collections.query.WidgetQuery;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.BankInteraction;
import com.example.InteractionApi.InteractionHelper;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.PacketUtils.WidgetInfoExtended;
import com.example.Packets.*;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.piggyplugins.PiggyUtils.API.InventoryUtil;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

import java.awt.event.KeyEvent;
import java.util.*;
import java.util.stream.Collectors;

@PluginDescriptor(
        name = "AutoFiremaking",
        description = "",
        enabledByDefault = false,
        tags = {"", ""}
)
@Slf4j
public class FiremakingPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private FiremakingConfig config;
    @Inject
    private KeyManager keyManager;
    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ClientThread clientThread;

    private boolean started = false;

    @Provides
    private FiremakingConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(FiremakingConfig.class);
    }

    public int timeout = 0;

    private ArrayList<WorldPoint> startTiles;
    private int lastStartTile = -1;
    private boolean firstFire = true;

    @Override
    protected void startUp() throws Exception {
        keyManager.registerKeyListener(toggle);
        timeout = 0;
        lastStartTile++;
        startTiles = new ArrayList<>(Arrays.asList(
                new WorldPoint(3194, 3491, 0), new WorldPoint(3194, 3490, 0),
                new WorldPoint(3194, 3489, 0), new WorldPoint(3194, 3488, 0))
        );
    }

    @Override
    protected void shutDown() throws Exception {
        keyManager.unregisterKeyListener(toggle);
        started = false;
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (client.getGameState() != GameState.LOGGED_IN || !started || EthanApiPlugin.isMoving() || client.getLocalPlayer().getAnimation() != -1) {
            return;
        }
        if (timeout > 0) {
            timeout--;
            return;
        }

        if (!hasLogs() || !hasTinderbox()) {
            Optional<NPC> banker = NPCs.search().nameContains("Banker").withAction("Bank").nearestToPlayer();
            if (!Bank.isOpen()) {
                if (banker.isPresent()) {
                    MousePackets.queueClickPacket();
                    NPCPackets.queueNPCAction(banker.get(), "Bank");
                    timeout = 2;
                    return;
                }
            }
            if (!hasTinderbox()) {
                Bank.search().withName("Tinderbox").first().ifPresent(item -> {
                    MousePackets.queueClickPacket();
                    BankInteraction.withdrawX(item, 1);
                    timeout = 2;
                });
            }
            if (!hasLogs()) {
                Bank.search().withName(config.getLogs()).first().ifPresentOrElse(item -> {
                    MousePackets.queueClickPacket();

                    BankInteraction.useItem(item, "Withdraw-all");
//                    BankInteraction.withdrawX(item, 27);
                    timeout = 2;
                }, () -> {
                    started = false;
                    client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Out of logs", null);
                });
            }
            if (!firstFire) {
                lastStartTile = lastStartTile >= startTiles.size() - 1 ? 0 : lastStartTile + 1;
                firstFire = true;
            }

        }
        if (firstFire) {
            MousePackets.queueClickPacket();
            MovementPackets.queueMovement(startTiles.get(lastStartTile));
//            timeout = 2;
            handleStartFire();
        } else {
            handleStartFire();
        }


    }


    public boolean hasTinderbox() {
        return Inventory.search().nameContains("inderbox").first().isPresent();
    }

    public boolean hasLogs() {
        return Inventory.search().nameContains(config.getLogs()).first().isPresent();
    }


    public boolean isStandingOnFire() {
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
        return TileObjects.search().nameContains("Fire").result().stream()
                .anyMatch(toad -> toad.getWorldLocation().equals(playerLocation));
    }

    private void handleStartFire() {

        if (isStandingOnFire()) {
            Optional<WorldPoint> freeTile = getNearestFreeTileInLine();
            if (freeTile.isPresent()) {
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(freeTile.get());
            }
            timeout = 3;
        }
        Widget tinderbox = Inventory.search().nameContains("inderbox").first().get();
        Widget logs = Inventory.search().nameContains(config.getLogs()).first().get();
        MousePackets.queueClickPacket();
        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetOnWidget(tinderbox, logs);
        firstFire = false;
    }

    private Optional<WorldPoint> getNearestFreeTileInLine() {
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
        TileObjectQuery fires = TileObjects.search().nameContains("Fire");
        List<WorldPoint> fireLocations = fires.result().stream().map(TileObject::getWorldLocation).collect(Collectors.toList());

        for (WorldPoint startPoint : startTiles) {
            if (startPoint.getY() != playerLocation.getY()) {
                continue; // Skip this startPoint and move to the next one
            }

            // Check westward for up to 27 tiles
            for (int i = 0; i < 27; i++) {
                WorldPoint checkPoint = startPoint.dx(-i); // Move westward
                if (!fireLocations.contains(checkPoint)) {
                    log.info("Free tile: " + checkPoint);
                    return Optional.of(checkPoint);
                }
            }
        }
        return Optional.empty(); // No free tile found
    }

    private void pressEsc() {
        KeyEvent keyPress = new KeyEvent(client.getCanvas(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_ESCAPE);
        client.getCanvas().dispatchEvent(keyPress);
    }

    private void pressSpace() {
        KeyEvent keyPress = new KeyEvent(client.getCanvas(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_SPACE);
        client.getCanvas().dispatchEvent(keyPress);
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
