package com.ozplugins.AutoMLM;

import com.example.EthanApiPlugin.Collections.*;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.BankInventoryInteraction;
import com.example.InteractionApi.InteractionHelper;
import com.example.InteractionApi.InventoryInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.PacketUtils.PacketUtilsPlugin;
import com.example.PacketUtils.WidgetInfoExtended;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

import java.time.Instant;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static com.ozplugins.AutoMLM.AutoMLMState.*;

@PluginDependency(EthanApiPlugin.class)
@PluginDependency(PacketUtilsPlugin.class)
@PluginDescriptor(
        name = "MLM",
        enabledByDefault = false,
        description = "[OZ]Plugins AutoMLM with modifications. Renamed to MLM in plugin list for my own sake",
        tags = {"Oz", "Ethan"}
)
@Slf4j
public class AutoMLMPlugin extends Plugin {
    protected static final Random random = new Random();
    Instant botTimer;
    boolean enablePlugin;

    @Inject
    Client client;
    @Inject
    PluginManager pluginManager;
    @Inject
    AutoMLMConfiguration config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ConfigManager configManager;
    @Inject
    private AutoMLMOverlay overlay;
    @Inject
    private ClientThread clientThread;
    @Inject
    EthanApiPlugin api;
    @Inject
    private KeyManager keyManager;
    @Inject
    private ChatMessageManager chatMessageManager;
    AutoMLMState state;
    int timeout = 0;
    int pouches = 0;
    boolean depositOres = false;
    String uiSetting = "";
    WorldPoint UpperPoint1 = new WorldPoint(3760, 5671, 0);
    WorldPoint UpperPoint2 = new WorldPoint(3752, 5679, 0);
    WorldPoint LowerPoint1 = new WorldPoint(3746, 5652, 0);
    WorldPoint LowerPoint2 = new WorldPoint(3734, 5667, 0);
    WorldPoint MinePoint;

    @Provides
    AutoMLMConfiguration provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoMLMConfiguration.class);
    }

    @Override
    protected void startUp() {
        timeout = 0;
        pouches = 0;
        enablePlugin = false;
        depositOres = false;
        botTimer = Instant.now();
        state = null;
        uiSetting = config.UISettings().getName();
        keyManager.registerKeyListener(pluginToggle);
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() {
        resetVals();
    }

    private void resetVals() {
        overlayManager.remove(overlay);
        state = null;
        timeout = 0;
        pouches = 0;
        enablePlugin = false;
        depositOres = false;
        keyManager.unregisterKeyListener(pluginToggle);
        uiSetting = null;
        botTimer = null;
    }

    public AutoMLMState getState() {
        Player player = client.getLocalPlayer();

        if (player == null) {
            return UNHANDLED_STATE;
        }
        if (timeout > 0) {
            return TIMEOUT;
        }
        if (api.isMoving()) {
            return MOVING;
        }
        if (isBankPinOpen()) {
            overlay.infoStatus = "Bank Pin";
            return BANK_PIN;
        }


        if (client.getLocalPlayer().getAnimation() == 6752) {
            overlay.infoStatus = "Mining";
            return ANIMATING;
        }
        needsToDepositOres();

        if (depositOres) {
            return DEPOSIT_BANK;
        } else if (!Inventory.full()) {
            return MINE;
        } else if (Inventory.full()) {
            return DEPOSIT_HOPPER;
        }

        return UNHANDLED_STATE;
    }

    private int idleTicks = 0;
    private int ticksTilSpec = -1;

    @Subscribe
    private void onGameTick(GameTick tick) {
        uiSetting = config.UISettings().getName();
        idleTicks = (client.getLocalPlayer().getAnimation() == -1 && !EthanApiPlugin.isMoving()) ? idleTicks + 1 : 0;
        if (!enablePlugin) {
            return;
        }
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }


        state = getState();
        switch (state) {
            case TIMEOUT:
                timeout--;
                break;

            case FIND_BANK:
                openNearestBank();
                timeout = tickDelay();
                break;

            case MINE:
                handleGem();
                if (config.useSpec())
                    handleSpec();
                switch (config.MineArea()) {
                    case UPPER_1:
                        MinePoint = UpperPoint1;
                        if (!isOnUpperFloor()) {
                            handleLadder();
                        } else {
                            handleMineOre();
                        }
                        break;

                    case UPPER_2:
                        MinePoint = UpperPoint2;
                        if (!isOnUpperFloor()) {
                            handleLadder();
                        } else {
                            handleMineOre();
                        }
                        break;

                    case LOWER_1:
                        MinePoint = LowerPoint1;
                        if (isOnUpperFloor()) {
                            handleLadder();
                        } else {
                            handleMineOre();
                        }
                        break;

                    case LOWER_2:
                        MinePoint = LowerPoint2;
                        if (isOnUpperFloor()) {
                            handleLadder();
                        } else {
                            handleMineOre();
                        }
                        break;
                }
                break;

            case DEPOSIT_HOPPER:
                timeout = 2;
                overlay.infoStatus = "Depositing ore";
                if (isOnUpperFloor()) {
                    handleLadder();
                } else {
                    handleHopper();
                }
                break;

            case DEPOSIT_BANK:
                timeout = 2;
                if (isOnUpperFloor()) {
                    handleLadder();
                } else {
                    handleDepositOres();
                }

                break;

            case UNHANDLED_STATE:
                overlay.infoStatus = "Shit, I did a fuck";
                break;

            case MOVING:
            case ANIMATING:
            case BANK_PIN:
            case IDLE:
                timeout = tickDelay();
                break;
        }
    }

    private void handleSpec() {
        if (ticksTilSpec > 0) {
            ticksTilSpec--;
        }
        if (hasSpec()) {
            if (ticksTilSpec == -1) {
                ticksTilSpec = (int) randomDelay(false, config.tickDelaySpecMin(), config.tickDelaySpecMax(),
                        config.tickDelaySpecDeviation(), config.tickDelaySpecTarget());
            }
            if (ticksTilSpec == 0) {
                useSpec();
                ticksTilSpec = -1;
            }
        }
    }

    private void useSpec() {
        if (!Equipment.search().matchesWildCardNoCase("*Dragon pickaxe*").empty() || !Equipment.search().matchesWildCardNoCase("*infernal pickaxe*").empty()) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetActionPacket(1, 38862884, -1, -1);
        }
    }

    private boolean hasSpec() {
        return client.getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT) == 1000;
    }

    public void handleDepositOres() {
//        if (isBankOpen()) {
        if (isDepositBoxOpen()) {
//            Optional<Widget> deposit = BankInventory.search().result().stream().filter(
//                    item -> item.getItemId() != ItemID.HAMMER && !item.getName().contains("pickaxe") && item.getItemId() != ItemID.PAYDIRT).findAny();
            //TODO can prolly simplify this with a list of item ids

            overlay.infoStatus = "Banking ores";
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(client.getWidget(12582916),
                    "Deposit inventory");
            if (client.getVarbitValue(Varbits.SACK_NUMBER) > 0) {
                handleSack();
            } else {
                depositOres = false;
            }
        } else {
            if (Inventory.getItemAmount(12011) > 1) {
                handleHopper();
            } else if (Inventory.search().filter(x -> x.getName().contains("ore") || x.getName().contains("Coal")).first().isPresent()) {
//                openNearestBank();
                openDepositBox();
            } else if (!isBankOpen() && Inventory.search().filter(x -> x.getName().contains("ore") || x.getName().contains("Coal")).first().isEmpty()
                    && client.getVarbitValue(Varbits.SACK_NUMBER) > 0) {
                handleSack();
            }
        }
    }

    public void handleSack() {
        Optional<TileObject> sack = TileObjects.search().withName("Sack").withAction("Search").nearestToPlayer();
        if (sack.isPresent() && !Inventory.full()) {
            overlay.infoStatus = "Searching sack";
            TileObjectInteraction.interact(sack.get(), "Search");
        }
    }

    public void handleGem() {
        Optional<Widget> uncut = Inventory.search().filter(gem -> gem.getName().contains("Uncut")).first();
        overlay.infoStatus = "Dropping gem";
        uncut.ifPresent(widget -> InventoryInteraction.useItem(widget, "Drop"));
    }

    public void handleMineOre() {
        if (idleTicks < 5) return;
        Optional<TileObject> ore = TileObjects.search().withName("Ore vein").withAction("Mine").nearestToPoint(MinePoint);
        overlay.infoStatus = "Mine ore";
        ore.ifPresent(tileObject -> TileObjectInteraction.interact(tileObject, "Mine"));
    }

    public void handleHopper() {
        Optional<TileObject> hopper = TileObjects.search().withName("Hopper").withAction("Deposit").nearestToPlayer();
        Optional<TileObject> brokenWheel = TileObjects.search().withAction("Hammer").nearestToPlayer();

        if (config.fixWheels() && brokenWheel.isPresent() && Inventory.getItemAmount(ItemID.HAMMER) == 1) {
            overlay.infoStatus = "Fixing wheel";
            TileObjectInteraction.interact(brokenWheel.get(), "Hammer");
        } else if (hopper.isPresent()) {
            overlay.infoStatus = "Deposit hopper";
            TileObjectInteraction.interact(hopper.get(), "Deposit");
        }
    }

    public void handleLadder() {
        overlay.infoStatus = "Climb ladder";
        Optional<TileObject> ladder = TileObjects.search().withName("Ladder").withAction("Climb").nearestToPlayer();
        ladder.ifPresent(tileObject -> TileObjectInteraction.interact(tileObject, "Climb"));
    }

    public void needsToDepositOres() {
        if (81 - (client.getVarbitValue(Varbits.SACK_NUMBER) + Inventory.getItemAmount(12011)) <= 0 //Prolly gotta change the == 0 part
                || Inventory.search().filter(x -> x.getName().contains("ore") || x.getName().contains("Coal")).first().isPresent()) {
            depositOres = true;
        }
    }

    public boolean isOnUpperFloor() {
        return (client.getVarbitValue(2086) == 1);
    }

    private final HotkeyListener pluginToggle = new HotkeyListener(() -> config.toggle()) {
        @Override
        public void hotkeyPressed() {
            togglePlugin();
        }
    };

    public void togglePlugin() {
        enablePlugin = !enablePlugin;
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        if (!enablePlugin) {
            // sendGameMessage("Auto MLM disabled.");
        } else {
            //sendGameMessage("Auto MLM enabled.");
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (!enablePlugin) {
            return;
        }

        ChatMessageType chatMessageType = event.getType();

        if (chatMessageType != ChatMessageType.GAMEMESSAGE && chatMessageType != ChatMessageType.SPAM) {
            return;
        }
    }

    //TODO Move all of this back into API
    private int tickDelay() {
        int tickLength = (int) randomDelay(config.tickDelayWeightedDistribution(), config.tickDelayMin(), config.tickDelayMax(), config.tickDelayDeviation(), config.tickDelayTarget());
        log.debug("tick delay for {} ticks", tickLength);
        return tickLength;
    }

    public long randomDelay(boolean weightedDistribution, int min, int max, int deviation, int target) {
        if (weightedDistribution) {
            return (long) clamp((-Math.log(Math.abs(random.nextGaussian()))) * deviation + target, min, max);
        } else {
            /* generate a normal even distribution random */
            return (long) clamp(Math.round(random.nextGaussian() * deviation + target), min, max);
        }
    }

    private double clamp(double val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    public void openDepositBox() {
        Optional<TileObject> depositBox = TileObjects.search().withName("Bank deposit box").nearestToPlayer();

        //Opens bank
        if (!isDepositBoxOpen()) {
            if (depositBox.isPresent()) {
                overlay.infoStatus = "Deposit box";
                TileObjectInteraction.interact(depositBox.get(), "Deposit");
            } else {
                overlay.infoStatus = "Bank not found";
            }
        }
    }

    public void openNearestBank() {
        Optional<TileObject> bank = TileObjects.search().withName("Bank chest").nearestToPlayer();

        //Opens bank
        if (!isBankOpen()) {
            if (bank.isPresent()) {
                overlay.infoStatus = "Banking";
                TileObjectInteraction.interact(bank.get(), "Use");
            } else {
                overlay.infoStatus = "Bank not found";
            }
        }
    }

    public int getRandomIntBetweenRange(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public void sendGameMessage(String message) {
        String chatMessage = new ChatMessageBuilder()
                .append(ChatColorType.HIGHLIGHT)
                .append(message)
                .build();

        chatMessageManager
                .queue(QueuedMessage.builder()
                        .type(ChatMessageType.CONSOLE)
                        .runeLiteFormattedMessage(chatMessage)
                        .build());
    }

    public boolean isDepositBoxOpen() {
        return client.getWidget(12582912) != null && !client.getWidget(12582912).isHidden();
    }

    public boolean isBankOpen() {
        return (client.getWidget(WidgetInfo.BANK_CONTAINER) != null);
    }

    public boolean isBankPinOpen() {
        return (client.getWidget(WidgetInfo.BANK_PIN_CONTAINER) != null);
    }

}
