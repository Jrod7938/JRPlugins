package com.piggyplugins.AutoChop

import com.example.EthanApiPlugin.Collections.*
import com.example.EthanApiPlugin.EthanApiPlugin
import com.example.InteractionApi.BankInventoryInteraction
import com.example.InteractionApi.NPCInteraction
import com.example.InteractionApi.TileObjectInteraction
import com.example.Packets.MousePackets
import com.example.Packets.WidgetPackets
import com.example.PathingTesting.PathingTesting
import com.google.inject.Inject
import com.google.inject.Provides
import com.piggyplugins.PiggyUtils.BreakHandler.ReflectBreakHandler
import net.runelite.api.Client
import net.runelite.api.GameState
import net.runelite.api.coords.WorldArea
import net.runelite.api.coords.WorldPoint
import net.runelite.api.events.GameTick
import net.runelite.client.config.ConfigManager
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor

@PluginDescriptor(
    name = "<html><font color=\"#FF9DF9\">[PP]</font> Auto Chop </html>",
    description = "Choppy Choppy",
    tags = ["jc"],
    enabledByDefault = false
)
class AutoChop : Plugin() {
    @Inject
    private lateinit var client: Client
    @Inject
    private lateinit var autoChopConfig: AutoChopConfig
    @Inject
    private lateinit var breakHandler: ReflectBreakHandler

    private lateinit var state: State

    private var lastPlayerLocation: WorldPoint? = null

    private lateinit var bankingArea: WorldArea
    private lateinit var treeArea: WorldArea
    private lateinit var bankDestination: WorldPoint
    private lateinit var treeDestination: WorldPoint

    private var ticksToWaitBeforeNextAction = 0

    @Provides
    private fun getConfig(configManager: ConfigManager): AutoChopConfig {
        return configManager.getConfig(AutoChopConfig::class.java)
    }

    @Throws(Exception::class)
    override fun startUp() {
        breakHandler.registerPlugin(this);
        breakHandler.startPlugin(this);
        changeStateTo(State.IDLE)
    }

    @Throws(Exception::class)
    override fun shutDown() {
        breakHandler.stopPlugin(this);
        breakHandler.unregisterPlugin(this);
    }

    @Subscribe
    fun onGameTick(e: GameTick) {
        if (breakHandler.shouldBreak(this)) {
            breakHandler.startBreak(this)
        }

        if (client.gameState != GameState.LOGGED_IN) {
            return
        }

        bankingArea = WorldArea(autoChopConfig.bankAreaXY().width, autoChopConfig.bankAreaXY().height, autoChopConfig.bankAreaWH().width, autoChopConfig.bankAreaWH().height, autoChopConfig.bankAreaPlane()-1)
        treeArea = WorldArea(autoChopConfig.treeAreaXY().width, autoChopConfig.treeAreaXY().height, autoChopConfig.treeAreaWH().width, autoChopConfig.treeAreaWH().height, autoChopConfig.treeAreaPlane()-1)
        bankDestination = WorldPoint(autoChopConfig.bankLocation().width, autoChopConfig.bankLocation().height, 0)
        treeDestination = WorldPoint(autoChopConfig.treeLocation().width, autoChopConfig.treeLocation().height, 0)

        when (state) {
            State.IDLE -> handleIdleState()
            State.SEARCHING -> handleSearchingState()
            State.ANIMATING -> handleAnimatingState()
            State.WALKING_TO_BANK -> handleWalkingToBankState()
            State.BANKING -> handleBankingState()
            State.WALKING_TO_TREES -> handleWalkingToTreesState()
        }
    }

    private fun handleWalkingToTreesState() {
        if (treeArea.contains(client.localPlayer.worldLocation)){
            ticksToWaitBeforeNextAction = 1
            changeStateTo(State.IDLE)
        } else {
            if (!EthanApiPlugin.isMoving() || !treeArea.contains(client.localPlayer.worldLocation)) {
                PathingTesting.walkTo(treeDestination)
            }
        }
    }

    private fun handleWalkingToBankState() {
        if (bankingArea.contains(client.localPlayer.worldLocation)){
            ticksToWaitBeforeNextAction = 1
            changeStateTo(State.BANKING)
        } else {
            if (!EthanApiPlugin.isMoving() || EthanApiPlugin.playerPosition().distanceTo(bankDestination) > 3 && !bankingArea.contains(client.localPlayer.worldLocation)) {
                PathingTesting.walkTo(bankDestination)
            }
            if (bankingArea.contains(client.localPlayer.worldLocation) && !EthanApiPlugin.isMoving()){
                ticksToWaitBeforeNextAction = 1
                changeStateTo(State.BANKING)
            }
        }
    }

    private fun handleBankingState() {
        if (ticksToWaitBeforeNextAction > 0) {
            ticksToWaitBeforeNextAction--
            return
        }

        if (!Bank.isOpen() && !EthanApiPlugin.isMoving() && Inventory.full()){
            NPCs.search().nameContains("Banker").withAction("Bank").nearestToPlayer().ifPresent { banker ->
                NPCInteraction.interact(banker, "Bank")
            }
            ticksToWaitBeforeNextAction = 1
            return
        }
        if (Bank.isOpen()){
            BankInventory.search().nameContains("ogs").withAction("Deposit-All").first().ifPresent { log ->
                BankInventoryInteraction.useItem(log, "Deposit-All")
            }
            changeStateTo(State.IDLE)
        }
    }

    private fun handleAnimatingState() {
        if (ticksToWaitBeforeNextAction > 0) {
            ticksToWaitBeforeNextAction--
            return
        }

        if (!EthanApiPlugin.isMoving() && client.localPlayer.animation == -1) {
            if (Inventory.full()) {
                changeStateTo(State.WALKING_TO_BANK)
            } else {
                ticksToWaitBeforeNextAction = 1
                changeStateTo(State.IDLE)
            }
        }
    }

    private fun handleSearchingState() {
        TileObjects.search().nameContains(autoChopConfig.treeName()).withAction(autoChopConfig.treeAction()).nearestToPlayer().ifPresent { tree ->
            TileObjectInteraction.interact(tree, autoChopConfig.treeAction())
            ticksToWaitBeforeNextAction = 1
            changeStateTo(State.ANIMATING)
        }
    }

    private fun handleIdleState() {
        if (ticksToWaitBeforeNextAction > 0) {
            ticksToWaitBeforeNextAction--
            return
        }

        if(runIsOff() && client.energy >= 20 * 100){
            MousePackets.queueClickPacket()
            WidgetPackets.queueWidgetActionPacket(1, 10485787, -1, -1)
        }

        if (Inventory.full()) {
            if (bankingArea.contains(client.localPlayer.worldLocation)){
                changeStateTo(State.BANKING)
            } else {
                changeStateTo(State.WALKING_TO_BANK)
            }
        } else {
            if (!treeArea.contains(client.localPlayer.worldLocation)) {
                changeStateTo(State.WALKING_TO_TREES)
            } else {
                changeStateTo(State.SEARCHING)
            }
        }
    }

    private fun changeStateTo(stateName: State) {
        state = stateName
        println("State: ${state.name}")
    }

    private fun runIsOff(): Boolean {
        return EthanApiPlugin.getClient().getVarpValue(173) == 0
    }
}