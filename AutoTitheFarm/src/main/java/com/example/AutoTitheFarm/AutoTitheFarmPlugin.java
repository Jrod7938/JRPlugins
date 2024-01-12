package com.example.AutoTitheFarm;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.Collections.Widgets;
import com.example.EthanApiPlugin.Collections.query.ItemQuery;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.InventoryInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.PacketUtils.PacketUtilsPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.ObjectPackets;
import com.example.Packets.WidgetPackets;
import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.apache.commons.lang3.RandomUtils;

import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.EthanApiPlugin.Collections.query.TileObjectQuery.getObjectComposition;
import static com.example.EthanApiPlugin.EthanApiPlugin.sendClientMessage;
import static com.example.EthanApiPlugin.EthanApiPlugin.stopPlugin;
import static com.example.PacketUtils.PacketReflection.client;

@Slf4j
@PluginDependency(PacketUtilsPlugin.class)
@PluginDependency(EthanApiPlugin.class)
@PluginDescriptor(name =
        "<html><font color=\"#FF9DF9\">[PP]</font> AutoTitheFarm</html>",
        description = "Will do Tithe Farm for you. Made by Lunatik",
        enabledByDefault = false,
        tags = {"lunatik"})
public class AutoTitheFarmPlugin extends Plugin {

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private AutoTitheFarmConfig config;

    @Inject
    private ClientThread clientThread;

    AutoTitheFarmOverlay overlay;

    @Override
    public void startUp() {
        log.info("Plugin started");
        overlay = new AutoTitheFarmOverlay(client, this, config);
        overlayManager.add(overlay);
        initValues();
    }

    @Override
    public void shutDown() {
        log.info("Plugin shutdown");
        resetValues();
        overlayManager.remove(overlay);
    }

    @Provides
    public AutoTitheFarmConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoTitheFarmConfig.class);
    }

    private static final int EMPTY_PATCH = 27383;

    private static final int WATERING_ANIMATION = 2293;

    private static final int PLANTING_ANIMATION = 2291;

    private static final int DIGGING_ANIMATION = 830;

    private static final String FILLED_WATERING_CAN = "Watering can(";

    private static final int REGULAR_WATERING_CAN_MAX_CHARGES = 8;

    private int totalAmountOfPatches;

    public final Set<TileObject> emptyPatches = new LinkedHashSet<>();

    private final List<TileObject> firstPhaseObjectsToFocus = new ArrayList<>();

    private final List<TileObject> secondPhaseObjectsToFocus = new ArrayList<>();

    private final List<TileObject> thirdPhaseObjectsToFocus = new ArrayList<>();

    private final List<TileObject> fourthPhaseObjectsToFocus = new ArrayList<>();

    @Getter(AccessLevel.PACKAGE)
    private boolean waitForAction;

    private boolean isHarvestingPhase;

    @Getter(AccessLevel.PACKAGE)
    private boolean needToRestoreRunEnergy;

    @Setter(AccessLevel.PACKAGE)
    @Getter(AccessLevel.PACKAGE)
    private static int farmingLevel;

    private int[][] patchLayout;

    private int gricollersChargesUsed;

    private int randomCount = 1;

    private boolean foundBlightedPlant;

    private int lastActionTimer;

    private WorldPoint defaultStartingPos;

    private boolean pluginJustEnabled;

    private int runEnergyDeviation;

    private final IntegerRandomizer randomCanCount = new IntegerRandomizer(2, 9);

    private void initValues() {
        setFarmingLevel(getGetPlayerFarmingLevel());
        patchLayout = config.patchLayout().getLayout();
        defaultStartingPos = config.patchLayout().getStartingPoint();
        randomCount = randomCanCount.getRandomInteger();
        clientThread.invoke(() -> Inventory.search().withId(ItemID.GRICOLLERS_CAN).first().ifPresent(itm -> InventoryInteraction.useItem(itm, "Check")));
        pluginJustEnabled = true;
        // IntegerRandomizer is only useful when a random integer is looked for more frequently. In this case it isnt, but is still used.
        runEnergyDeviation = new IntegerRandomizer(config.minRunEnergyToIdleUnder(), config.minRunEnergyToIdleUnder() + 10).getRandomInteger();
    }

    private void resetValues() {
        emptyPatches.clear();
        firstPhaseObjectsToFocus.clear();
        secondPhaseObjectsToFocus.clear();
        thirdPhaseObjectsToFocus.clear();
        fourthPhaseObjectsToFocus.clear();
        waitForAction = false;
        needToRestoreRunEnergy = false;
        defaultStartingPos = null;
        pluginJustEnabled = false;
        lastActionTimer = 0;
        randomCanCount.getOldValues().clear();
    }

    private boolean pluginStartedDuringARun() {
        if (pluginJustEnabled && !startingNewRun()) {
            sendClientMessage("Incorrect plugin startup state: start the plugin when the patches are completely empty.");
            return true;
        }
        return false;
    }

    private boolean gotRequiredItems() {
        int regularCansNeeded = totalAmountOfPatches == 25 ? 10 : 9;
        Optional<Widget> seedDibber = Inventory.search().withId(ItemID.SEED_DIBBER).first();
        Optional<Widget> spade = Inventory.search().withId(ItemID.SPADE).first();
        boolean canCheck = isGricollersCanFound() || getAllRegularWateringCan().result().size() >= regularCansNeeded;
        boolean mainCondition = canCheck && seedDibber.isPresent() && spade.isPresent();

        if (isInsideTitheFarm() ? mainCondition && getSeed() != null : mainCondition) {
            return true;
        }
        sendClientMessage("Starting requirements not met. " +
                "Please make sure you have all the required items in the inventory: spade, seed dibber, " +
                "at least " + regularCansNeeded + " regular watering cans, or gricoller's can.");
        return false;
    }

    private String getObjectAction(TileObject object) {
        ObjectComposition objectComposition = getObjectComposition(object);
        String getAction = null;
        for (String action : objectComposition.getActions()) {
            if (action == null) {
                continue;
            }
            getAction = action;
        }
        return getAction;
    }

    private ItemQuery getAllRegularWateringCan() {
        return Inventory.search().matchesWildCardNoCase("Watering*");
    }

    private Widget getAppropriateWateringCan() {
        return Inventory.search().withId(ItemID.GRICOLLERS_CAN).first().orElseGet(() -> getAllRegularWateringCan().first().orElse(null));

    }

    private Widget getFilledRegularWateringCan() {
        return Inventory.search().nameContains(FILLED_WATERING_CAN).first().orElse(null);
    }

    private Widget getSeed() {
        return Inventory.search().nameContains("seed").first().orElse(null);
    }

    private boolean isGricollersCanFound() {
        return getAppropriateWateringCan() != null && getAppropriateWateringCan().getName().contains("Gricoller's");
    }

    private boolean isNeedToRefillGricollersCan() {
        if (!isGricollersCanFound()) {
            return false;
        }
        return randomCount == gricollersChargesUsed || gricollersChargesUsed > randomCount;
    }

    public boolean startingNewRun() {
        return emptyPatches.size() == totalAmountOfPatches;
    }

    private int getGetPlayerFarmingLevel() {
        return client.getRealSkillLevel(Skill.FARMING);
    }

    private void openFarmDoor() {
        if (waitForAction) {
            return;
        }
        TileObjects.search().nameContains("Farm door").first().ifPresent(obj -> TileObjectInteraction.interact(obj, "Open"));
        waitForAction = true;
    }

    private WorldPoint playerDirection() {
        WorldPoint worldPoint;
        int playerOrientation = client.getLocalPlayer().getCurrentOrientation();
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();

        switch (playerOrientation) {
            case 151: worldPoint = playerLocation.dx(-1).dy(-2); break;
            case 360: worldPoint = playerLocation.dx(-2).dy(-1); break;
            case 663: worldPoint = playerLocation.dx(-2).dy(1); break;
            case 872: worldPoint = playerLocation.dx(-1).dy(2); break;
            case 1176: worldPoint = playerLocation.dx(1).dy(2); break;
            case 1385: worldPoint = playerLocation.dx(2).dy(1); break;
            case 1688: worldPoint = playerLocation.dx(2).dy(-1); break;
            case 1897: worldPoint = playerLocation.dx(1).dy(-2); break;
            default: worldPoint = null;
        }

        return worldPoint;
    }

    private int amountOfGrowingPatchesLeft() {
        return TileObjects.search().withAction("Water").result().size();
    }

    private boolean isInsideTitheFarm() {
        if (client.isInInstancedRegion()) {
            return true;
        }
        resetValues();
        return false;
    }

    private void doAction(List<TileObject> collection) {
        TileObject patch = collection.get(0);
        //log.info("Wait for action: " + waitForAction);
        if (waitForAction) {
            return;
        }
        TileObjectInteraction.interact(patch, "Water", "Harvest");
        waitForAction = true;
        log.info(getObjectAction(patch) + "ing");
    }

    private void useItemOnObject(Widget widget, TileObject tileObject) {
        //log.info("Wait for action: " + waitForAction);
        Optional<Widget> optionalWidget = Optional.of(widget);
        if (waitForAction) {
            return;
        }
        optionalWidget.ifPresent(itm -> ObjectPackets.queueWidgetOnTileObject(itm, tileObject));
        waitForAction = true;
    }

    private void captureEmptyPatches() {
        totalAmountOfPatches = patchLayout.length;

        if (!emptyPatches.isEmpty()) {
            emptyPatches.clear();
        }
        for (int[] point : patchLayout) {
            WorldPoint worldPoint = WorldPoint.fromScene(client, point[0], point[1], 0);
            TileObjects.search().withId(EMPTY_PATCH).atLocation(worldPoint).first().ifPresent(emptyPatches::add);
        }
    }

    private List<Widget> herbBox() {
        return Inventory.search().withId(ItemID.HERB_BOX).result();
    }

    private void openHerbBox() {
        Consumer<? super Widget> actionPredicate = itm -> InventoryInteraction.useItem(itm, "Bank-all");
        if (config.oneTickBankAllHerbBoxes()) {
            herbBox().forEach(actionPredicate);
        } else {
            herbBox().stream().findFirst().ifPresent(actionPredicate);
        }
    }

    private void getLastActionTimer() {
        if (waitForAction) {
            lastActionTimer++;
        } else {
            lastActionTimer = 0;
        }
    }

    private boolean isCurrentSeedMatchingFarmingLevel() {
        String currentSeed;
        Pattern pattern = Pattern.compile("<col=ff9040>(\\w+)");
        Matcher matcher = null;

        try {
            matcher = pattern.matcher(getSeed().getName());
        } catch (NullPointerException e) {
            log.info(e.getMessage());
        }

        assert matcher != null;
        currentSeed = matcher.find() ? matcher.group(1) : null;

        String compareString = null;
        switch (Objects.requireNonNull(Plants.getNeededPlant())) {
            case BOLOGANO: compareString = "Bologano"; break;
            case GOLOVANOVA: compareString = "Golovanova"; break;
            case LOGAVANO: compareString = "Logavano"; break;
        }

        // current seed in the inventory should never be null either way
        assert currentSeed != null;
        return currentSeed.equals(compareString);
    }

    private void handleMinigame() {
        Optional<TileObject> waterBarrel = TileObjects.search().nameContains("Water Barrel").nearestToPlayer();
        int runEnergy = client.getEnergy() / 100;
        Optional<Widget> fruit = Inventory.search().nameContains("fruit").first();
        List<Widget> regularWateringCansToRefill = Inventory.search().idInList(List.of(ItemID.WATERING_CAN, ItemID.WATERING_CAN1,
                ItemID.WATERING_CAN2, ItemID.WATERING_CAN3, ItemID.WATERING_CAN4, ItemID.WATERING_CAN5, ItemID.WATERING_CAN6,
                ItemID.WATERING_CAN7)).result();

        if (!isCurrentSeedMatchingFarmingLevel()) {
            if (fruit.isEmpty()) {
                openFarmDoor();
            } else {
                stopPlugin(this);
            }
            return;
        }

        dePopulateList(firstPhaseObjectsToFocus);
        dePopulateList(secondPhaseObjectsToFocus);
        dePopulateList(thirdPhaseObjectsToFocus);
        dePopulateList(fourthPhaseObjectsToFocus);


        // if 10 ticks have passed and no actions have been made within time limit then something went horribly wrong.
        if (lastActionTimer > (startingNewRun() ? 2 : 10) && !EthanApiPlugin.isMoving() && waitForAction) {
            waitForAction = false;
        }

        if (runEnergy == 100) {
            needToRestoreRunEnergy = false;
        }

        if (startingNewRun()) {
            isHarvestingPhase = false;
            foundBlightedPlant = false;
            pluginJustEnabled = false;

            // in case we've for whatever reason used almost all seeds. We shouldn't be close to the smallest amount either way... >_>
            if (getSeed().getItemQuantity() < 100) {
                stopPlugin(this);
                return;
            }

            if (fruit.isPresent() && config.stopIfReachedFruitAmountFarmed() && fruit.get().getItemQuantity() >= config.maxFruitToFarm()) {
                stopPlugin(this);
                return;
            }

            if (isNeedToRefillGricollersCan()) {
                log.info("Need to refill Gricoller's can");
                useItemOnObject(getAppropriateWateringCan(), waterBarrel.orElse(null));
                return;
            }

            if (getRegularCansCount() != -1 && getRegularCansCount() < (getAllRegularWateringCan().result().size() * REGULAR_WATERING_CAN_MAX_CHARGES)) {
                log.info("Need to refill regular cans");
                regularWateringCansToRefill.forEach(itm -> ObjectPackets.queueWidgetOnTileObject(itm, waterBarrel.orElse(null)));
                return;
            }

            // whether to trigger earlier if running.
            int runEnergyExtraDeviation = EthanApiPlugin.isMoving() ? runEnergyDeviation + 5 : runEnergyDeviation;
            if (runEnergy <= runEnergyExtraDeviation) {
                needToRestoreRunEnergy = true;
            }

            if (needToRestoreRunEnergy) {
                return;
            }

            if (defaultStartingPos != null && !client.getLocalPlayer().getWorldLocation().equals(defaultStartingPos)) {
                if (!EthanApiPlugin.isMoving()) {
                    MousePackets.queueClickPacket();
                    MovementPackets.queueMovement(defaultStartingPos);
                    log.info("Moving to starting position");
                }
                return;
            }
        }

        if (!firstPhaseObjectsToFocus.isEmpty()) {
            useItemOnObject(isGricollersCanFound() ? getAppropriateWateringCan() : getFilledRegularWateringCan(), firstPhaseObjectsToFocus.get(0));
            return;
        }

        List<TileObject> convertedListPatches = new ArrayList<>(emptyPatches);
        if (!emptyPatches.isEmpty() && !isHarvestingPhase && !foundBlightedPlant) {
            useItemOnObject(getSeed(), convertedListPatches.get(0));
            log.info("Planting");
            return;
        }

        List<List<TileObject>> phases = List.of(secondPhaseObjectsToFocus, thirdPhaseObjectsToFocus, fourthPhaseObjectsToFocus);
        for (List<TileObject> phase : phases) {
            if (phase.isEmpty()) {
                continue;
            }
            doAction(phase);
            if (phase != phases.get(phases.size() - 1)) {
                return;
            }
        }
    }

    private void dePopulateList(List<TileObject> list) {
        if (client.getLocalPlayer().getAnimation() == WATERING_ANIMATION || client.getLocalPlayer().getAnimation() == DIGGING_ANIMATION) {
            list.removeIf(tileObject -> playerDirection() != null && playerDirection().equals(tileObject.getWorldLocation()));
        }
    }

    private void handleLobby() {
        if (!herbBox().isEmpty()) {
            openHerbBox();
            return;
        }

        int firstChatOptionId = 0;
        Optional<Widget> firstChatWindowId = Widgets.search().withTextContains("kind of crop").hiddenState(false).first();
        Optional<Widget> secondChatWindowId = Widgets.search().withTextContains("How many seeds").hiddenState(false).first();

        switch (Objects.requireNonNull(Plants.getNeededPlant())) {
            case GOLOVANOVA: firstChatOptionId = 1; break;
            case BOLOGANO: firstChatOptionId = 2; break;
            case LOGAVANO: firstChatOptionId = 3; break;
        }

        if (getSeed() == null) {
            if (secondChatWindowId.isPresent()) {
                client.setVarcStrValue(VarClientStr.INPUT_TEXT, Integer.toString(10000));

                KeyEvent keyPress = new KeyEvent(client.getCanvas(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_ENTER);
                client.getCanvas().dispatchEvent(keyPress);
                return;
            }
            if (firstChatWindowId.isEmpty()) {
                TileObjects.search().withId(ObjectID.SEED_TABLE).first().ifPresent(obj -> TileObjectInteraction.interact(obj, "Search"));
                return;
            }
            WidgetPackets.queueResumePause(14352385, firstChatOptionId);
            return;
        }
        openFarmDoor();
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        log.info("List size: " + randomCanCount.getOldValues().size());
        log.info("Refill can at amount: " + randomCount);
        log.info(String.valueOf(runEnergyDeviation));
        getLastActionTimer();

        if (!gotRequiredItems()) {
            stopPlugin(this);
            return;
        }

        if (!isInsideTitheFarm()) {
            handleLobby();
            return;
        }

        captureEmptyPatches();

        if (pluginStartedDuringARun()) {
            stopPlugin(this);
            return;
        }

        handleMinigame();
    }

    @Subscribe
    private void onAnimationChanged(AnimationChanged event) {
        Actor actor = event.getActor();
        int animationId = actor.getAnimation();

        if (!(actor instanceof Player)) {
            return;
        }

        if (animationId == -1 && (!isInsideTitheFarm() || startingNewRun())) {
            waitForAction = false;
        }
    }

    private void populateList(List<TileObject> list, TileObject tileObject) {
        if (!list.contains(tileObject)) {
            list.add(tileObject);
        }
    }

    private void removeObjectFromListIfBlighted(List<TileObject> list, GameObject blightedObject) {
        list.removeIf(tileObject -> blightedObject.getWorldLocation().equals(tileObject.getWorldLocation()));
    }

    @Subscribe
    private void onGameObjectSpawned(GameObjectSpawned event) {
        GameObject gameObject = event.getGameObject();
        int objectId = gameObject.getId();
        Plants plant = Plants.getNeededPlant();

        if (gameObject.getWorldLocation().equals(playerDirection()) || startingNewRun()) {
            waitForAction = false;
        }

        if (objectId == plant.getFirstStageId()) {
            populateList(firstPhaseObjectsToFocus, gameObject);
        } else if (objectId == plant.getSecondStageId()) {
            populateList(secondPhaseObjectsToFocus, gameObject);
        } else if (objectId == plant.getThirdStageId()) {
            populateList(thirdPhaseObjectsToFocus, gameObject);
        } else if (objectId == plant.getFourthStageId()) {
            populateList(fourthPhaseObjectsToFocus, gameObject);
        }

        String objectName = getObjectComposition(gameObject).getName();
        List<List<TileObject>> lists = List.of(firstPhaseObjectsToFocus, secondPhaseObjectsToFocus, thirdPhaseObjectsToFocus, fourthPhaseObjectsToFocus);
        if (!objectName.contains("Blighted")) {
            return;
        }

        foundBlightedPlant = true;
        waitForAction = false;

        for (List<TileObject> list : lists) {
            removeObjectFromListIfBlighted(list, gameObject);
        }
    }

    @Subscribe
    private void onGameObjectDespawned(GameObjectDespawned event) {
        GameObject gameObject = event.getGameObject();
        int objectId = gameObject.getId();
        Plants plant = Plants.getNeededPlant();

        if (objectId == plant.getFourthStageId() && amountOfGrowingPatchesLeft() == 0) {
            isHarvestingPhase = true;
        }
    }

    private int getRegularCansCount() {
        if (isGricollersCanFound()) {
            return -1;
        }
        int stack = 0;
        Pattern pattern = Pattern.compile("<col=ff9040>Watering can\\((\\d+)\\)</col>");
        for (Widget itm : getAllRegularWateringCan().result()) {
            Matcher matcher = pattern.matcher(itm.getName());
            if (matcher.find()) {
                stack += Integer.parseInt(matcher.group(1));
            }
        }
        return stack;
    }

    private int getGricollersCanCount(String message) {
        Matcher matcher = Pattern.compile("\\d+").matcher(message);
        int intValue = matcher.find() ? (Integer.parseInt(matcher.group()) / 10) : -1;

        switch (intValue) {
            case 1: return 9;
            case 2: return 8;
            case 3: return 7;
            case 4: return 6;
            case 5: return 5;
            case 6: return 4;
            case 7: return 3;
            case 8: return 2;
            case 9: return 1;
            case 10: return 0;
            default: return -1;
        }
    }

    @Subscribe
    private void onChatMessage(ChatMessage event) {
        String message = event.getMessage();

        if (message.contains("Watering can charges") && !isNeedToRefillGricollersCan()) {
            gricollersChargesUsed = getGricollersCanCount(message);
        }

        if (message.contains("You fill the watering can")) {
            randomCount = randomCanCount.getRandomInteger();
            gricollersChargesUsed = 0;
        }

        if (message.contains("can is already full")) {
            randomCount = randomCanCount.getRandomInteger();
        }
    }

    @Subscribe
    private void onGameStateChanged(GameStateChanged event) {
        GameState gameState = event.getGameState();

        switch (gameState) {
            case CONNECTION_LOST:
            case LOGIN_SCREEN:
            case LOGIN_SCREEN_AUTHENTICATOR: stopPlugin(this); break;
        }
    }

}