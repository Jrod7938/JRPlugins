package com.piggyplugins.HerbCleaner;

import com.example.EthanApiPlugin.Collections.Bank;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.Collections.query.TileObjectQuery;
import com.example.InteractionApi.BankInteraction;
import com.example.InteractionApi.InventoryInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.WidgetPackets;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.piggyplugins.PiggyUtils.BreakHandler.ReflectBreakHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Skill;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@PluginDescriptor(
        name = "<html><font color=\"#FF9DF9\">[PP]</font> Herb Cleaner</html>",
        description = "Automatically banks & cleans 10 herbs per game tick",
        enabledByDefault = false,
        tags = {"ethan", "piggy"}
)
@Slf4j
public class HerbCleanerPlugin extends Plugin {
    private static final Set<Integer> cleanHerbs = Set.of(ItemID.GUAM_LEAF, ItemID.MARRENTILL, ItemID.TARROMIN, ItemID.HARRALANDER, ItemID.RANARR_WEED, ItemID.TOADFLAX, ItemID.IRIT_LEAF,
            ItemID.AVANTOE, ItemID.KWUARM, ItemID.SNAPDRAGON, ItemID.CADANTINE, ItemID.LANTADYME, ItemID.DWARF_WEED, ItemID.TORSTOL);
    private static final Map<Integer, Integer> grimyHerbLevels = new HashMap<>();
    static {
                grimyHerbLevels.put(ItemID.GRIMY_GUAM_LEAF, 3);
                grimyHerbLevels.put(ItemID.GRIMY_MARRENTILL, 5);
                grimyHerbLevels.put(ItemID.GRIMY_TARROMIN, 11);
                grimyHerbLevels.put(ItemID.GRIMY_HARRALANDER, 20);
                grimyHerbLevels.put(ItemID.GRIMY_RANARR_WEED, 25);
                grimyHerbLevels.put(ItemID.GRIMY_TOADFLAX, 30);
                grimyHerbLevels.put(ItemID.GRIMY_IRIT_LEAF, 40);
                grimyHerbLevels.put(ItemID.GRIMY_AVANTOE, 48);
                grimyHerbLevels.put(ItemID.GRIMY_KWUARM, 54);
                grimyHerbLevels.put(ItemID.GRIMY_SNAPDRAGON, 59);
                grimyHerbLevels.put(ItemID.GRIMY_CADANTINE, 65);
                grimyHerbLevels.put(ItemID.GRIMY_LANTADYME, 67);
                grimyHerbLevels.put(ItemID.GRIMY_DWARF_WEED, 70);
                grimyHerbLevels.put(ItemID.GRIMY_TORSTOL, 75);
    }

    @Inject
    private Client client;
    @Inject
    private HerbCleanerConfig config;
    @Inject
    private KeyManager keyManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private HerbCleanerOverlay overlay;
    @Inject
    private ReflectBreakHandler breakHandler;
    @Getter
    private boolean started;


    @Override
    protected void startUp() throws Exception {
        keyManager.registerKeyListener(toggle);
        overlayManager.add(overlay);
        breakHandler.registerPlugin(this);
    }

    @Override
    protected void shutDown() throws Exception {
        keyManager.unregisterKeyListener(toggle);
        overlayManager.remove(overlay);
        breakHandler.unregisterPlugin(this);
    }

    @Provides
    private HerbCleanerConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(HerbCleanerConfig.class);
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (client.getGameState() != GameState.LOGGED_IN
            || !started
            || breakHandler.isBreakActive(this)) {
            return;
        }

        if (breakHandler.shouldBreak(this)) {
            breakHandler.startBreak(this);
            return;
        }

        if (client.getLocalPlayer().getAnimation() != -1) {
            return;
        }

        if (hasHerbsInInventory()) {
            if (isBankOpen()) {
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(client.getLocalPlayer().getWorldLocation());
                return;
            }

            handleCleanHerb();
        } else {
            if (!isBankOpen()) {
                findBank();
                return;
            }
            handleBank();
        }
    }

    private void findBank() {
        TileObjects.search()
                .filter(tileObject -> {
                    ObjectComposition objectComposition = TileObjectQuery.getObjectComposition(tileObject);
                    return getName().toLowerCase().contains("bank") ||
                            Arrays.stream(objectComposition.getActions()).anyMatch(action -> action != null && action.toLowerCase().contains("bank"));
                })
                .nearestToPlayer()
                .ifPresent(tileObject -> {
                            TileObjectInteraction.interact(tileObject, "Use", "Bank");
                        });
    }

    private void handleBank() {
        if (!hasHerbsInBank()) {
            return;
        }

        Optional<Widget> cleanHerb =
                Inventory.search().filter(item -> cleanHerbs.contains(item.getItemId())).first();
        if (cleanHerb.isPresent()) {
            Widget widget = client.getWidget(WidgetInfo.BANK_DEPOSIT_INVENTORY);
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(widget, "Deposit", "Deposit inventory");
            return;
        }

        Bank.search()
                .nameContains(config.herbType().getItemName())
                .filter(item -> {
                    int herbLvl = client.getBoostedSkillLevel(Skill.HERBLORE);
                    int maxHerb = grimyHerbLevels.get(item.getItemId());
                   return herbLvl >= maxHerb;
                })
                .first()
                .ifPresent(item -> BankInteraction.useItem(item, "Withdraw-All"));
    }

    private void handleCleanHerb() {
        List<Widget> itemList = Inventory.search()
                .nameContains(config.herbType().getItemName())
                .result();

        for (int i = 0; i < config.herbAmount(); i++) {
            InventoryInteraction.useItem(itemList.get(i), "Clean");
        }
    }

    private boolean hasHerbsInInventory() {
        return Inventory.search().nameContains(config.herbType().getItemName()).first().isPresent();
    }

    private boolean hasHerbsInBank() {
        return Bank.search().nameContains(config.herbType().getItemName()).first().isPresent();
    }

    private boolean isBankOpen() {
        Widget bank = client.getWidget(WidgetInfo.BANK_CONTAINER);
        return bank != null && !bank.isHidden();
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
        if (started) {
            breakHandler.startPlugin(this);
        } else {
            breakHandler.stopPlugin(this);
        }
    }
}
