package com.polyplugins.Chompy;


import com.example.EthanApiPlugin.Collections.Equipment;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.Collections.query.ItemQuery;
import com.example.EthanApiPlugin.Collections.query.NPCQuery;
import com.example.EthanApiPlugin.Collections.query.TileObjectQuery;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.Packets.*;
import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.concurrent.ThreadLocalRandom;

@PluginDescriptor(
        name = "<html><font color=\"#FF9DF9\">[PP]</font> AutoChompy</html>",
        description = "Auto Chompy Killer",
        enabledByDefault = false,
        tags = {"poly", "plugin"}
)
@Slf4j
public class AutoChompyPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private AutoChompyConfig config;
    @Inject
    private AutoChompyOverlay overlay;
    @Inject
    private KeyManager keyManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ClientThread clientThread;
    public boolean started = false;
    public int timeout = 0;
    public State state = State.WAITING;
    public NPCQuery swampToads;
    public NPCQuery bloatedToads;
    public ItemQuery bloatedToadsItem;
    public NPCQuery birds;
    private int ammoId = -1;

    @Provides
    private AutoChompyConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoChompyConfig.class);
    }

    private int tickDelay() {
        return config.tickDelay() ? ThreadLocalRandom.current().nextInt(config.tickDelayMin(), config.tickDelayMax()) : 3;
    }

    @Override
    protected void startUp() throws Exception {
        keyManager.registerKeyListener(toggle);
        overlayManager.add(overlay);
        timeout = 0;
        clientThread.invoke(this::setVals);
    }

    @Override
    protected void shutDown() throws Exception {
        keyManager.unregisterKeyListener(toggle);
        overlayManager.remove(overlay);
        timeout = 0;
        started = false;
        unsetVals();
    }


    @Subscribe
    private void onGameTick(GameTick event) {
        if (client.getGameState() != GameState.LOGGED_IN || !started) {
            return;
        }
        if (timeout > 0) {
            timeout--;
            return;
        }
        setVals();
        determineNextState();
        doChompy();
    }

    private void unsetVals() {
        swampToads = null;
        bloatedToads = null;
        bloatedToadsItem = null;
        birds = null;
        ammoId = -1;
    }

    public void setVals() {
        if (ammoId < 0) {
            Equipment.search().filter(item -> {
                String name = item.getName();
                return name.contains("gre arrow") || name.contains("brutal");
            }).first().ifPresentOrElse(item -> {
                ammoId = item.getEquipmentItemId();
            }, () -> {
                EthanApiPlugin.sendClientMessage("No Ogre arrows or Brutal arrows found");
                EthanApiPlugin.stopPlugin(this);
            });
        }
        swampToads = NPCs.search().nameContains("wamp toad").withAction("Inflate");
        bloatedToads = NPCs.search().nameContains("loated Toad");
        bloatedToadsItem = Inventory.search().nameContains("loated toad");
        birds = NPCs.search().alive().nameContains("ompy bird").withAction("Attack");
    }

    private void doChompy() {
        checkRunEnergy();
        log.info(state.toString());
//        log.info("Ammo id: {}", ammoId);
//        log.info(getNearestFreeTile().toString());
        if (Equipment.search().withId(ammoId).empty()) {
            EthanApiPlugin.sendClientMessage("No Ogre arrows or Brutal arrows left");
            EthanApiPlugin.stopPlugin(this);
        }
        switch (state) {
            case KILL_BIRD:
                handleKillBird();
                break;
            case FILL_BELLOWS:
                handleFillBellows();
                break;
            case DROP_TOAD:
                handleDropToad();
                break;
            case INFLATE_TOAD:
                handleInflateToad();
                break;
            case WAITING:
                timeout = tickDelay();
                break;
            default:
                determineNextState();
                break;
        }
    }

    private void determineNextState() {
        if (!birds.empty()) {
            state = State.KILL_BIRD;
        } else if (!hasFilledBellows() && !TileObjects.search().nameContains("wamp bubble").empty()) {
            state = State.FILL_BELLOWS;
        } else if (!bloatedToadsItem.empty()) {
            state = State.DROP_TOAD;
        } else if (bloatedToadsItem.empty() && hasFilledBellows()) {
            state = State.INFLATE_TOAD;
        } else {
            state = State.WAITING;
        }
    }

    private void handleKillBird() {
        birds.first().ifPresent(npc -> {
            MousePackets.queueClickPacket();
            NPCPackets.queueNPCAction(npc, "Attack");
        });
    }

    private Bubbles lastVisitedBubble = null;

    private void handleFillBellows() {
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
        log.info("Player Location: " + playerLocation);

        Bubbles bubble = Bubbles.getNearestBubble(playerLocation, 10, lastVisitedBubble);
        if (bubble != null) {
            log.info("Found bubble within radius at location: " + bubble.getLocation());

            TileObjectQuery query = new TileObjectQuery(TileObjects.search().result());
            Optional<TileObject> tileObject = query.nameContains("Swamp bubble").nearestToPoint(bubble.getLocation());

            if (tileObject.isPresent()) {
                log.info("Found TileObject: " + tileObject.get().getId());

                if (!client.getLocalPlayer().isInteracting()) {
                    log.info("Player is not interacting. Interacting with TileObject.");
                    MousePackets.queueClickPacket();
                    TileObjectInteraction.interact(tileObject.get(), "Suck");
                    timeout = tickDelay();
                    lastVisitedBubble = bubble; // Update last visited bubble
                } else {
                    log.info("Player is already interacting.");
                }
            } else {
                log.info("TileObject not found.");
            }
        } else {
            log.info("No bubble found within radius.");
        }
    }


    public boolean hasFilledBellows() {
        return Inventory.search().nameContains("bellows (").first().isPresent();
    }

    public boolean isStandingOnToad() {
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
        return NPCs.search().nameContains("loated Toad").result().stream()
                .anyMatch(toad -> toad.getWorldLocation().equals(playerLocation));
    }

    private void handleDropToad() {
        if (isStandingOnToad()) {
            MousePackets.queueClickPacket();
            MovementPackets.queueMovement(getNearestFreeTile().get());
            timeout = tickDelay();
        } else {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(bloatedToadsItem.first().get(), "Drop");
            timeout = tickDelay();
        }
    }

    private Optional<WorldPoint> getNearestFreeTile() {
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
        List<WorldPoint> surroundingTiles = new ArrayList<>();
        for (int dx = -5; dx <= 5; dx++) {
            for (int dy = -5; dy <= 5; dy++) {
                if (dx != 0 || dy != 0) { // Exclude the player's current tile
                    surroundingTiles.add(playerLocation.dx(dx).dy(dy));
                }
            }
        }
        List<WorldPoint> toadLocations = NPCs.search().nameContains("loated toad")
                .result().stream().map(NPC::getWorldLocation).collect(Collectors.toList());
        List<WorldPoint> freeTiles = surroundingTiles.stream().filter(tile -> !toadLocations.contains(tile))
                .collect(Collectors.toList());
        return freeTiles.stream().min(Comparator.comparingInt(tile -> tile.distanceTo(playerLocation)));
    }

    private void handleInflateToad() {
        swampToads.nearestToPlayer().ifPresent(npc -> {
            MousePackets.queueClickPacket();
            NPCPackets.queueNPCAction(npc, "Inflate");
            log.info("Inflating Toad");
            timeout = tickDelay();
            log.info(String.valueOf(timeout));
        });
    }

    private void checkRunEnergy() {
        if (runIsOff() && client.getEnergy() >= 30 * 100) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetActionPacket(1, 10485787, -1, -1);
        }
    }

    public enum State {
        FILL_BELLOWS, INFLATE_TOAD, DROP_TOAD, WAITING, KILL_BIRD, STOPPED
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
            state = State.STOPPED;
            return;
        }
        started = !started;
    }
}
