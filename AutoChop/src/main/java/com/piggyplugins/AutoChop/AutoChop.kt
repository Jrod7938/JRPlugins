package com.piggyplugins.AutoChop

import com.example.EthanApiPlugin.Collections.*
import com.example.EthanApiPlugin.EthanApiPlugin
import com.example.InteractionApi.BankInventoryInteraction
import com.example.InteractionApi.InventoryInteraction
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
import net.runelite.api.NPC
import net.runelite.api.VarPlayer
import net.runelite.api.coords.WorldArea
import net.runelite.api.coords.WorldPoint
import net.runelite.api.events.GameTick
import net.runelite.client.config.ConfigManager
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.ui.overlay.OverlayManager
import java.awt.event.KeyEvent
import java.util.*

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

    @Inject
    private lateinit var autoChopOverlay: AutoChopOverlay

    @Inject
    private lateinit var overlayManager: OverlayManager

    lateinit var state: State

    private lateinit var bankingArea: WorldArea
    private lateinit var treeArea: WorldArea
    private lateinit var bankDestination: WorldPoint
    private lateinit var treeDestination: WorldPoint

    var tickDelay = 0

    @Provides
    private fun getConfig(configManager: ConfigManager): AutoChopConfig {
        return configManager.getConfig(AutoChopConfig::class.java)
    }

    @Throws(Exception::class)
    override fun startUp() {
        if (client.gameState != GameState.LOGGED_IN) return
        breakHandler.registerPlugin(this);
        breakHandler.startPlugin(this);
        changeStateTo(State.IDLE)
    }

    @Throws(Exception::class)
    override fun shutDown() {
        breakHandler.stopPlugin(this);
        breakHandler.unregisterPlugin(this);
        overlayManager.remove(autoChopOverlay)
    }

    @Subscribe
    fun onGameTick(e: GameTick) {
        if (!hasAxe()) {
            EthanApiPlugin.sendClientMessage("No axe found, stopping plugin")
            shutDown()
        }
        if (tickDelay > 0) { // Tick delay
            tickDelay--
            return
        }

        if (autoChopConfig.displayOverlay()) { // Toggle overlay
            overlayManager.add(autoChopOverlay)
        } else {
            overlayManager.remove(autoChopOverlay)
        }

        if (breakHandler.shouldBreak(this)) { // Break handler
            breakHandler.startBreak(this)
        }

        if (client.gameState != GameState.LOGGED_IN) { // Check if logged in
            return
        }


        // Set up areas and destinations
        bankingArea = WorldArea(
            autoChopConfig.TREEANDLOCATION().bankAreaXY().width,
            autoChopConfig.TREEANDLOCATION().bankAreaXY().height,
            autoChopConfig.TREEANDLOCATION().bankAreaWH().width,
            autoChopConfig.TREEANDLOCATION().bankAreaWH().height,
            client.plane
        )
        treeArea = WorldArea(
            autoChopConfig.TREEANDLOCATION().treeAreaXY().width,
            autoChopConfig.TREEANDLOCATION().treeAreaXY().height,
            autoChopConfig.TREEANDLOCATION().treeAreaWH().width,
            autoChopConfig.TREEANDLOCATION().treeAreaWH().height,
            client.plane
        )
        bankDestination = WorldPoint(
            autoChopConfig.TREEANDLOCATION().bankWalkLocation().width,
            autoChopConfig.TREEANDLOCATION().bankWalkLocation().height,
            0
        )
        treeDestination = WorldPoint(
            autoChopConfig.TREEANDLOCATION().treeWalkLocation().width,
            autoChopConfig.TREEANDLOCATION().treeWalkLocation().height,
            0
        )


        when (state) { // State machine
            State.IDLE -> handleIdleState()
            State.SEARCHING -> handleSearchingState()
            State.CUTTING -> handleCuttingState()
            State.WALKING_TO_BANK -> handleWalkingToBankState()
            State.BANKING -> handleBankingState()
            State.WALKING_TO_TREES -> handleWalkingToTreesState()
            State.BURN_LOGS -> handleBurnLogsState()
            State.TREE_ROOT -> handleTreeRootState()
            State.FOX_TRAP -> handleFoxTrapState()
            State.RAINBOW -> handleRainbowState()
            State.BEE_HIVE -> handleBeeHiveState()
            State.PHEASANT -> handlePheasantState()
            State.RITUAL_CIRCLES -> handleRitualCirclesState()
            State.ENTLING -> handleEntlingState()
        }
    }

    private fun handleEntlingState() {
        if (!EthanApiPlugin.isMoving() && client.localPlayer.animation == -1) {
            if (entlingExists()) {
                val (entling, action) = findEntlingToPrune() ?: return
                NPCInteraction.interact(entling, action)
                return
            } else {
                changeStateTo(State.IDLE)
            }
        }
    }

    private fun handleRitualCirclesState() {
        if (!EthanApiPlugin.isMoving() && client.localPlayer.animation == -1) {
            if (ritualCircleExists()) {
                if (client.localPlayer.worldLocation != findUniqueRitualCircle()!!.worldLocation) {
                    PathingTesting.walkTo(findUniqueRitualCircle()!!.worldLocation)
                    tickDelay = 1
                    return
                }
            } else {
                changeStateTo(State.IDLE)
            }
        }
    }

    private fun handlePheasantState() {
        if (!EthanApiPlugin.isMoving() && client.localPlayer.animation == -1) {
            if (Inventory.search().nameContains("Pheasant egg").result().isNotEmpty()) {
                if (NPCs.search().nameContains("Freaky Forester").result().isEmpty()) {
                    changeStateTo(State.IDLE)
                }
                NPCs.search().nameContains("Freaky Forester").nearestToPlayer().ifPresent { forester ->
                    NPCInteraction.interact(forester, "Talk-to")
                }
                return
            }
            if (pheasantExists()) {
                val pheasantNPCLocations = NPCs.search().withName("Pheasant").result().map { it.worldLocation }
                TileObjects.search().nameContains("Pheasant Nest").withAction("Retrieve-egg").withinDistance(15)
                    .filter { nest -> !pheasantNPCLocations.contains(nest.worldLocation) }
                    .nearestToPlayer().ifPresent { pheasant ->
                        TileObjectInteraction.interact(pheasant, "Retrieve-egg")
                    }
                return
            } else {
                changeStateTo(State.IDLE, 3)
            }
        }
    }

    private fun handleBeeHiveState() {
        if (!EthanApiPlugin.isMoving() && client.localPlayer.animation == -1) {
            if (beeHiveExists() && Inventory.search().nameContains("ogs").result().isNotEmpty()) {
                if (Widgets.search().withTextContains("How many logs would you like to add").result().isNotEmpty()) {
                    sendKey(KeyEvent.VK_SPACE)
                    tickDelay = 3
                    return
                }
                NPCs.search().nameContains("nfinished Beehive").withAction("Build").nearestToPlayer()
                    .ifPresent { beeHive ->
                        NPCInteraction.interact(beeHive, "Build")
                    }
                return
            } else {
                changeStateTo(State.IDLE)
            }
        }
    }

    private fun handleRainbowState() {
        if (!EthanApiPlugin.isMoving() && client.localPlayer.animation == -1) {
            if (rainbowExists()) {
                if (client.localPlayer.worldLocation != rainbowLocation()) {
                    PathingTesting.walkTo(rainbowLocation())
                    tickDelay = 1
                    return
                }
            } else {
                changeStateTo(State.IDLE)
            }
        }
    }

    private fun handleFoxTrapState() {
        if (!EthanApiPlugin.isMoving() && client.localPlayer.animation == -1) {
            if (foxTrapExists()) {
                NPCs.search().nameContains("Fox trap").withAction("Disarm").nearestToPlayer().ifPresent { foxTrap ->
                    NPCInteraction.interact(foxTrap, "Disarm")
                }
                return
            } else {
                changeStateTo(State.IDLE, 3)
            }
        }
    }

    private fun handleTreeRootState() {
        if (!EthanApiPlugin.isMoving() && client.localPlayer.animation == -1) {
            if (treeRootExists()) {
                useSpecial()
                TileObjects.search().nameContains("infused Tree root").withAction("Chop down").withinDistance(15)
                    .nearestToPlayer().ifPresent { treeRoot ->
                        TileObjectInteraction.interact(treeRoot, "Chop down")
                    }
                return
            } else {
                changeStateTo(State.IDLE)
            }
        }
    }

    private fun handleBurnLogsState() {
        if (!EthanApiPlugin.isMoving() && client.localPlayer.animation == -1) {
            if (Widgets.search().withTextContains("What would you like to burn").result().isNotEmpty()) {
                sendKey(KeyEvent.VK_SPACE)
                return
            }
            if (Inventory.search().nameContains(autoChopConfig.TREEANDLOCATION().logName()).result().isNotEmpty()) {
                val campFire = TileObjects.search().nameContains("Campfire").withAction("Tend-to").withinDistance(15)
                    .nearestToPlayer()
                if (campFire.isPresent) {
                    TileObjectInteraction.interact(campFire.get(), "Tend-to")
                    return
                } else {
                    changeStateTo(State.WALKING_TO_BANK)
                }
            }
            if (Inventory.search().nameContains(autoChopConfig.TREEANDLOCATION().logName()).result().isEmpty()) {
                changeStateTo(State.IDLE)
            }
        }
        checkEvents() // Check for events
    }

    private fun handleWalkingToTreesState() {
        if (treeArea.contains(client.localPlayer.worldLocation)) {
            changeStateTo(State.IDLE)
        } else {
            if (!EthanApiPlugin.isMoving() || !treeArea.contains(client.localPlayer.worldLocation)) {
                PathingTesting.walkTo(treeDestination)
            }
        }
    }

    private fun handleWalkingToBankState() {
        if (bankingArea.contains(client.localPlayer.worldLocation)) {
            changeStateTo(State.BANKING)
        } else {
            if (!EthanApiPlugin.isMoving()
                || EthanApiPlugin.playerPosition().distanceTo(bankDestination) > 3
                && !bankingArea.contains(client.localPlayer.worldLocation)
            ) {
                PathingTesting.walkTo(bankDestination)
            }
            if (bankingArea.contains(client.localPlayer.worldLocation) && !EthanApiPlugin.isMoving()) {
                changeStateTo(State.BANKING)
            }
        }
    }

    private fun handleBankingState() {
        if (!Bank.isOpen() && !EthanApiPlugin.isMoving() && Inventory.full()) {
            var bank = TileObjects.search().nameContains("Bank").withAction("Bank").nearestToPlayer()
            if (bank.isPresent) {
                TileObjectInteraction.interact(bank.get(), "Bank")
            } else {
                TileObjects.search().nameContains("Bank").withAction("Use").nearestToPlayer().ifPresent { bank ->
                    TileObjectInteraction.interact(bank, "Use")
                }
            }
            tickDelay = 1
            return
        }
        if (Bank.isOpen()) {
            BankInventory.search().nameContains("ogs").withAction("Deposit-All").first().ifPresent { log ->
                BankInventoryInteraction.useItem(log, "Deposit-All")
            }
            changeStateTo(State.IDLE)
        }
    }

    private fun handleCuttingState() {
        checkEvents() // Check for events
        if (!EthanApiPlugin.isMoving() && client.localPlayer.animation == -1) {
            if (Inventory.full()) {
                if (autoChopConfig.burnLogs()) changeStateTo(State.BURN_LOGS) // Burn logs if campfire exists else walk to bank
                else changeStateTo(State.WALKING_TO_BANK)
            } else {
                changeStateTo(State.IDLE) // Change state to idle if inventory is not full
            }
        }
    }

    private fun handleSearchingState() {
        TileObjects.search().nameContains(autoChopConfig.TREEANDLOCATION().treeName())
            .withAction(autoChopConfig.TREEANDLOCATION().treeAction())
            .withinDistance(10).nearestToPoint(getObjectWMostPlayers()).ifPresent { tree ->
                useSpecial()
                TileObjectInteraction.interact(tree, autoChopConfig.TREEANDLOCATION().treeAction())
                changeStateTo(State.CUTTING)
            }
    }

    private fun handleIdleState() {
        if (runIsOff() && client.energy >= 20 * 100) {
            MousePackets.queueClickPacket()
            WidgetPackets.queueWidgetActionPacket(1, 10485787, -1, -1)
        }

        if (Inventory.full()) {
            if (autoChopConfig.burnLogs()) {
                if (campFireExists()) changeStateTo(State.BURN_LOGS) // Burn logs if campfire exists
                else changeStateTo(State.WALKING_TO_BANK) // else walk to bank
            } else {
                if (bankingArea.contains(client.localPlayer.worldLocation)) changeStateTo(State.BANKING) // Bank if inventory is full
                else changeStateTo(State.WALKING_TO_BANK) // Walk to bank if inventory is full
            }
        } else {
            if (!treeArea.contains(client.localPlayer.worldLocation)) changeStateTo(State.WALKING_TO_TREES) // Walk to trees if not in tree area
            else if (!checkEvents()) changeStateTo(State.SEARCHING) // Check for events and change state to searching if none
        }
    }

    private fun getObjectWMostPlayers(): WorldPoint {
        val objectName: String = autoChopConfig.TREEANDLOCATION().treeName()
        val playerCounts: MutableMap<WorldPoint, Int> = HashMap()
        var mostPlayersTile: WorldPoint? = null
        var highestCount = 0
        val objects = TileObjects.search().withName(objectName).result()

        val players = Players.search().notLocalPlayer().result()

        for (tree in objects) {
            for (player in players) {
                if (player.worldLocation.distanceTo(tree.worldLocation) <= 2) {
                    val playerTile = player.worldLocation
                    playerCounts[playerTile] = playerCounts.getOrDefault(playerTile, 0) + 1
                    if (playerCounts[playerTile]!! > highestCount) {
                        highestCount = playerCounts[playerTile]!!
                        mostPlayersTile = playerTile
                    }
                }
            }
        }

        return mostPlayersTile ?: client.localPlayer.worldLocation
    }

    private fun findUniqueRitualCircle(): NPC? {
        val ritualCircles = NPCs.search().nameContains("Ritual circle").result()
            .filter { it.name != null }

        // Count occurrences of each distinct Ritual Circle name
        val nameCountMap = mutableMapOf<String, Int>()
        ritualCircles.forEach { circle ->
            val name = circle.name!!
            nameCountMap[name] = nameCountMap.getOrDefault(name, 0) + 1
        }

        // Find the name with a count of 1 (the unique one)
        val uniqueName = nameCountMap.entries.find { it.value == 1 }?.key

        // Return the NPC corresponding to the unique name
        return ritualCircles.find { it.name == uniqueName }
    }





    private fun foxTrapExists(): Boolean = NPCs.search().nameContains("Fox trap").result().isNotEmpty()
    private fun treeRootExists(): Boolean =
        TileObjects.search().nameContains("infused Tree root").withinDistance(15).result().isNotEmpty()

    private fun rainbowExists(): Boolean =
        TileObjects.search().nameContains("ainbow").withinDistance(15).result().isNotEmpty()

    private fun beeHiveExists(): Boolean =
        NPCs.search().nameContains("nfinished Beehive").result().isNotEmpty()

    private fun rainbowLocation(): WorldPoint =
        TileObjects.search().nameContains("ainbow").withinDistance(15).nearestToPlayer().get().worldLocation

    private fun campFireExists(): Boolean =
        TileObjects.search().nameContains("Campfire").withinDistance(15).result().isNotEmpty()

    private fun pheasantExists(): Boolean =
        TileObjects.search().nameContains("Pheasant Nest").withAction("Retrieve-egg").withinDistance(15).result()
            .isNotEmpty()
    private fun ritualCircleExists(): Boolean = NPCs.search().nameContains("Ritual circle").result().isNotEmpty()
    private fun entlingExists(): Boolean = NPCs.search().nameContains("Entling").result().isNotEmpty()
    private fun hasAxe(): Boolean =
        !Equipment.search().nameContains("axe").empty() || !Equipment.search().nameContains("axe").empty()

    private fun runIsOff(): Boolean = EthanApiPlugin.getClient().getVarpValue(173) == 0


    private fun checkEvents(): Boolean {
        if (treeRootExists()) {
            breakPlayersAnimation()
            changeStateTo(State.TREE_ROOT)
            return true
        }
        if (foxTrapExists()) {
            breakPlayersAnimation()
            changeStateTo(State.FOX_TRAP)
            return true
        }
        if (rainbowExists()) {
            breakPlayersAnimation()
            changeStateTo(State.RAINBOW)
            return true
        }
        if (beeHiveExists() && Inventory.search().nameContains("ogs").result().count() >= 2) {
            breakPlayersAnimation()
            changeStateTo(State.BEE_HIVE)
            return true
        }
        if (pheasantExists()) {
            breakPlayersAnimation()
            changeStateTo(State.PHEASANT)
            return true
        }
        if (ritualCircleExists()) {
            breakPlayersAnimation()
            changeStateTo(State.RITUAL_CIRCLES, 1)
            return true
        }
        if (findEntlingToPrune() != null) {
            breakPlayersAnimation()
            changeStateTo(State.ENTLING)
            return true
        }
        return false
    }

    data class PruningAction(val entling: NPC, val action: String)

    private fun findEntlingToPrune(): PruningAction? {
        val playerLocation = client.localPlayer.worldLocation

        val entlings = NPCs.search().nameContains("Entling").result()
            .filter { it.overheadText != null }
            .sortedBy { it.worldLocation.distanceTo(playerLocation) }

        for (entling in entlings) {
            val request = entling.overheadText

            val action = when (request) {
                "Breezy at the back!" -> "Prune-back"
                "A leafy mullet!" -> "Prune-top"
                "Short back and sides!" -> "Prune-back"
                "Short on top!" -> "Prune-top"
                else -> continue
            }

            return PruningAction(entling, action)
        }

        return null // No entling found that needs pruning
    }



    private fun breakPlayersAnimation(): Boolean {
        if (EthanApiPlugin.isMoving() || client.localPlayer.animation != -1) {
            if (Inventory.search().nameContains("ogs").result().isNotEmpty()) {
                Inventory.search().nameContains("ogs").first().ifPresent { log ->
                    InventoryInteraction.useItem(log, "Drop")
                }
                return true
            }
        }
        return false
    }

    private fun useSpecial() {
        if (client.getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT) == 1000) {
            if (!Equipment.search().matchesWildCardNoCase("*Dragon axe*").empty()
                || !Equipment.search().matchesWildCardNoCase("*infernal axe*").empty()
                || !Equipment.search().matchesWildCardNoCase("*Dragon felling axe*").empty()
            ) {
                MousePackets.queueClickPacket()
                WidgetPackets.queueWidgetActionPacket(1, 38862884, -1, -1)
            }
        }
    }

    private fun changeStateTo(stateName: State, ticksToDelay: Int = 0) {
        state = stateName
        tickDelay = ticksToDelay
        // println("State : $stateName")
    }

    private fun sendKey(key: Int) {
        keyEvent(KeyEvent.KEY_PRESSED, key)
        keyEvent(KeyEvent.KEY_RELEASED, key)
    }

    private fun keyEvent(id: Int, key: Int) {
        val e = KeyEvent(
            client.getCanvas(),
            id,
            System.currentTimeMillis(),
            0,
            key,
            KeyEvent.CHAR_UNDEFINED
        )
        client.getCanvas().dispatchEvent(e)
    }

}