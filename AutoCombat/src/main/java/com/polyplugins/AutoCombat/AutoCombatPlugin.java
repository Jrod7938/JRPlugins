package com.polyplugins.AutoCombat;


import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.TileItems;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.InventoryInteraction;
import com.example.Packets.*;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.polyplugins.AutoCombat.helper.LootHelper;
import com.polyplugins.AutoCombat.helper.SlayerHelper;
import com.polyplugins.AutoCombat.util.SuppliesUtil;
import com.polyplugins.AutoCombat.util.Util;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.StatChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStack;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

@PluginDescriptor(
        name = "AutoCombat",
        description = "Kills shit",
        enabledByDefault = false,
        tags = {"poly", "plugin"}
)
@Slf4j
public class AutoCombatPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private AutoCombatConfig config;
    @Inject
    private AutoCombatOverlay overlay;
    @Inject
    private KeyManager keyManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    public ItemManager itemManager;
    @Inject
    private ClientThread clientThread;
    public boolean started = false;
    public int timeout = 0;

    @Provides
    private AutoCombatConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoCombatConfig.class);
    }

    @Inject
    public SuppliesUtil supplies;
    @Inject
    public Util util;
    @Inject
    public LootHelper lootHelper;
    @Inject
    public SlayerHelper slayerHelper;
    public Queue<ItemStack> lootQueue = new LinkedList<>();

    public int lootTrigger = 0;
    private boolean hasFood = false;
    private boolean hasPrayerPot = false;
    private boolean hasCombatPot = false;
    private boolean hasBones = false;
    public boolean isSlayerNpc = false;
    public SlayerNpc slayerInfo = null;
    public int idleTicks = 0;
    public NPC targetNpc = null;
    public Player player = null;

    @Override
    protected void startUp() throws Exception {
        keyManager.registerKeyListener(toggle);
        overlayManager.add(overlay);
        timeout = 0;
        lootTrigger = config.numPiles();
    }

    @Override
    protected void shutDown() throws Exception {
        keyManager.unregisterKeyListener(toggle);
        overlayManager.remove(overlay);
        resetEverything();
    }

    public void resetEverything() {
        timeout = 0;
        lootTrigger = 0;
        started = false;
        hasBones = false;
        hasCombatPot = false;
        hasPrayerPot = false;
        hasFood = false;
        idleTicks = 0;
        lootQueue.clear();
        targetNpc = null;
        player = null;
        slayerInfo = null;
        isSlayerNpc = false;
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        player = client.getLocalPlayer();
        targetNpc = util.findNpc(config.targetName());
        isSlayerNpc = slayerHelper.isSlayerNPC(config.targetName());
        if(isSlayerNpc)slayerInfo=slayerHelper.getSlayerInfo(targetNpc.getName());
        if (timeout > 0) {
            timeout--;
            return;
        }
        if (client.getGameState() != GameState.LOGGED_IN || EthanApiPlugin.isMoving() || !started) {
            return;
        }
        if (!util.isInteracting() || player.getAnimation() == -1) idleTicks++;
        else idleTicks = 0;
        checkRunEnergy();
        hasFood = supplies.findFood() != null;
        hasPrayerPot = supplies.findPrayerPotion() != null;
        hasCombatPot = supplies.findCombatPotion() != null;
        hasBones = supplies.findBone() != null;
        //do later,copilot suggest so may as well
//        if (hasBones) {
//            InventoryInteraction.useItem(supplies.findBone(), "Bury");
//            timeout = 1;
//        }
        if (!lootQueue.isEmpty()) {
            lootTrigger = 0;
            ItemStack itemStack = lootQueue.poll();

            TileItems.search().withId(itemStack.getId()).first().ifPresent(item -> {
                log.info("Looting: " + item.getTileItem().getId());
                ItemComposition comp = itemManager.getItemComposition(item.getTileItem().getId());
                if (comp.isStackable() || comp.getNote() != -1) {
                    if (lootHelper.hasStackableLoot(comp)) {
                        log.info("Has stackable loot");
                        item.interact(false);
                        timeout = 5;
                    }
                    if (Inventory.full()) {
                        handleFullInventory();
                    }
                }
                if (!Inventory.full()) {
                    item.interact(false);
                    timeout = 5;
                }
                if (lootQueue.isEmpty())
                    lootTrigger = config.numPiles();
            });
        }

        if ((lootTrigger > 0 && lootQueue.isEmpty()) || idleTicks > 25) {
            if (util.isInteracting() || util.isBeingInteracted()) {
                timeout = 5;
                return;
            }
            if (isSlayerNpc && slayerInfo.getDisturbAction() != null) {
                Optional<NPC> npc = NPCs.search().nameContains(slayerInfo.getUndisturbedName()).first();
                if (npc.isPresent()) {
                    MousePackets.queueClickPacket();
                    NPCPackets.queueNPCAction(npc.get(), slayerInfo.getDisturbAction());
                    timeout = 5;
                    idleTicks = 0;
                }
//                if (targetNpc != null) {
////                log.info("Should fight, found npc");
//                    MousePackets.queueClickPacket();
//                    NPCPackets.queueNPCAction(targetNpc, "Attack");
//                    timeout = 4;
//                    idleTicks = 0;
//                }
            }
        }
    }

    private void handleFullInventory() {

    }

    private void handleCombatPot() {
        if (hasCombatPot) {
            InventoryInteraction.useItem(supplies.findCombatPotion(), "Drink");
            timeout = 1;
        }
    }

    private void handlePrayerPot() {
        if (hasPrayerPot) {
            InventoryInteraction.useItem(supplies.findPrayerPotion(), "Drink");
            timeout = 1;
        }
    }

    private void handleEating() {
        if (hasFood) {
            InventoryInteraction.useItem(supplies.findFood(), "Eat");
            timeout = 2;
        }
    }

    @Subscribe
    public void onNpcLootReceived(NpcLootReceived event) {
        Collection<ItemStack> items = event.getItems();
        items.stream().filter(item -> {
            ItemComposition comp = itemManager.getItemComposition(item.getId());
            return lootHelper.getLootNames().contains(comp.getName());
        }).forEach(it -> {
            log.info("Adding to lootQueue: " + it.getId());
            lootQueue.add(it);
            lootTrigger--;
        });
    }

    @Subscribe
    public void onStatChanged(StatChanged event) {
        switch (event.getSkill()) {
            case HITPOINTS:
                if (client.getBoostedSkillLevel(Skill.HITPOINTS) <= config.eatAt()) {
                    handleEating();
                }
                break;
            case PRAYER:
                if (client.getBoostedSkillLevel(Skill.PRAYER) <= config.usePrayerPotAt()) {
                    handlePrayerPot();
                }
                break;
            case STRENGTH:
                if (client.getBoostedSkillLevel(Skill.STRENGTH) <= config.useCombatPotAt()) {
                    handleCombatPot();
                }
                break;
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        int bid = event.getVarbitId();
        int pid = event.getVarpId();
        if (pid == VarPlayer.SLAYER_TASK_SIZE) {
            if (event.getValue() <= 0 && config.shutdownOnTaskDone()) {
                InventoryInteraction.useItem(supplies.findTeleport(), "Break");
                EthanApiPlugin.stopPlugin(this);
            }
        }
        if (bid == Varbits.BOSS_HEALTH_CURRENT) {
            if (!slayerHelper.isSlayerNPC(targetNpc.getName()))
                return;
            SlayerNpc info = slayerHelper.getSlayerInfo(targetNpc.getName());
            Inventory.search().nameContains(info.getItemName()).first().ifPresent(item -> {
                log.info("Using item: " + item.getName());
                MousePackets.queueClickPacket();
                MousePackets.queueClickPacket();
                NPCPackets.queueWidgetOnNPC(targetNpc, item);
                timeout = 3;
            });
            int curHp = event.getVarbitId();
            log.info("NPC HP: " + curHp);
            if (curHp <= info.getUseHp()) {

            }
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals("AutoCombatConfig"))
            return;
        if (event.getKey().equals("lootNames")) {
            lootHelper.setLootNames(null);
            lootHelper.getLootNames();
        }
    }


    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        GameState state = event.getGameState();
        if (state == GameState.HOPPING || state == GameState.LOGGED_IN) return;
        EthanApiPlugin.stopPlugin(this);
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