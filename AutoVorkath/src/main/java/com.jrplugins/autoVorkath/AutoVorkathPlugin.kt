/*
 * Copyright (c) 2024. By Jrod7938
 *
 */
package com.jrplugins.autoVorkath

import com.example.EthanApiPlugin.Collections.*
import com.example.EthanApiPlugin.EthanApiPlugin
import com.example.InteractionApi.*
import com.example.Packets.MousePackets
import com.example.Packets.MovementPackets
import com.example.Packets.WidgetPackets
import com.google.inject.Provides
import com.piggyplugins.PiggyUtils.BreakHandler.ReflectBreakHandler
import net.runelite.api.*
import net.runelite.api.coords.WorldArea
import net.runelite.api.coords.WorldPoint
import net.runelite.api.events.*
import net.runelite.client.config.ConfigManager
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.events.NpcLootReceived
import net.runelite.client.game.ItemManager
import net.runelite.client.game.ItemStack
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.ui.overlay.OverlayManager
import java.awt.event.KeyEvent
import javax.inject.Inject
import kotlin.math.abs


@PluginDescriptor(
    name = "<html><font color=\"#9ddbff\">[JR]</font> Auto Vorkath </html>",
    description = "JR - Auto vorkath",
    tags = ["vorkath", "auto", "auto prayer"],
    enabledByDefault = false
)
class AutoVorkathPlugin : Plugin() {
    @Inject
    private lateinit var client: Client

    @Inject
    private lateinit var breakHandler: ReflectBreakHandler

    @Inject
    private lateinit var overlayManager: OverlayManager

    @Inject
    private lateinit var autoVorkathOverlay: AutoVorkathOverlay

    @Inject
    private lateinit var config: AutoVorkathConfig

    @Inject
    private lateinit var itemManager: ItemManager

    @Provides
    fun getConfig(configManager: ConfigManager): AutoVorkathConfig {
        return configManager.getConfig(AutoVorkathConfig::class.java)
    }

    var botState: State = State.NONE
    var tickDelay: Int = 0
    var killCount: Int = 0
    private var running = false
    private val rangeProjectileId = 1477
    private val magicProjectileId = 393
    private val purpleProjectileId = 1471
    private val blueProjectileId = 1479
    private val redProjectileId = 1481
    private val acidProjectileId = 1483
    private val acidRedProjectileId = 1482
    private val whiteProjectileId = 395

    private var isPrepared = false
    private var drankAntiFire = false
    private var drankRangePotion = false
    private var lastDrankAntiFire: Long = 0
    private var lastDrankRangePotion: Long = 0

    private val lootQueue: MutableList<ItemStack> = mutableListOf()
    private var lootNames: MutableSet<String> = mutableSetOf()
    private var acidPools: HashSet<WorldPoint> = hashSetOf()

    private var initialAcidMove = false

    private var redBallLocation: WorldPoint = WorldPoint(0, 0, 0)

    private val bankArea: WorldArea = WorldArea(2096, 3911, 20, 11, 0)
    private val bankLocation: WorldPoint = WorldPoint(2099, 3919, 0)
    private val fremennikArea: WorldArea = WorldArea(2627, 3672, 24, 30, 0)

    enum class State {
        WALKING_TO_BANK,
        BANKING,
        WALKING_TO_VORKATH,
        PREPARE,
        POKE,
        FIGHTING,
        ACID,
        SPAWN,
        RED_BALL,
        LOOTING,
        WALKING_TO_GE,
        GETTING_ITEM,
        SELLING,
        MULING,
        THINKING,
        NONE
    }

    override fun startUp() {
        println("Auto Vorkath Plugin Activated")
        botState = State.THINKING
        running = client.gameState == GameState.LOGGED_IN
        lootNames = mutableSetOf()
        breakHandler.registerPlugin(this)
        breakHandler.startPlugin(this)
        overlayManager.add(autoVorkathOverlay)
    }

    override fun shutDown() {
        println("Auto Vorkath Plugin Deactivated")
        running = false
        botState = State.NONE
        drankAntiFire = false
        drankRangePotion = false
        lastDrankAntiFire = 0
        lastDrankRangePotion = 0
        killCount = 0
        lootQueue.clear()
        lootNames.clear()
        acidPools.clear()
        breakHandler.stopPlugin(this)
        breakHandler.unregisterPlugin(this)
        overlayManager.remove(autoVorkathOverlay)
    }

    @Subscribe
    fun onChatMessage(e: ChatMessage) {
        if (e.message.contains("Oh dear, you are dead!")) {
            drankAntiFire = false
            drankRangePotion = false
            isPrepared = false
            activatePrayers(false)
            EthanApiPlugin.stopPlugin(this)
        }
        if (e.message.contains("Your Vorkath kill count is:")) {
            activatePrayers(false)
            drankAntiFire = false
            drankRangePotion = false
            isPrepared = false
        }
        if (e.message.contains("There is no ammo left in your quiver.")) {
            teleToHouse()
            EthanApiPlugin.sendClientMessage("No ammo, stopping plugin.")
            drankAntiFire = false
            drankRangePotion = false
            isPrepared = false
            activatePrayers(false)
            EthanApiPlugin.stopPlugin(this)
        }
    }

    @Subscribe
    fun onNpcLootReceived(event: NpcLootReceived) {
        if (!running) return
        val items = event.items
        items.stream().forEach { item ->
            if (item != null) {
                lootQueue.add(item)
                val comp: ItemComposition = itemManager.getItemComposition(item.id)
                lootNames.add(comp.name)
            }
        }
        killCount++
        changeStateTo(State.LOOTING)
    }

    @Subscribe
    fun onNpcDespawned(e: NpcDespawned) {
        if (e.npc.name == "Zombified Spawn") {
            if (Inventory.search().nameContains(config.CROSSBOW().toString()).result().isNotEmpty()) {
                InventoryInteraction.useItem(config.CROSSBOW().toString(), "Wield")
            }
            changeStateTo(State.FIGHTING)
        }
    }

    @Subscribe
    fun onProjectileMoved(e: ProjectileMoved) {
        when (e.projectile.id) {
            acidProjectileId -> {
                acidPools.add(WorldPoint.fromLocal(client, e.position))
                changeStateTo(State.ACID)
            }

            whiteProjectileId -> changeStateTo(State.SPAWN)
            acidRedProjectileId -> changeStateTo(State.ACID)
            rangeProjectileId, magicProjectileId, purpleProjectileId, blueProjectileId -> activatePrayers(true)
            redProjectileId -> {
                redBallLocation = WorldPoint.fromLocal(client, e.position)
                changeStateTo(State.RED_BALL)
            }
        }
    }

    @Subscribe
    fun onGameObjectDespawned(e: GameObjectDespawned) {
        if (e.gameObject.id == 32000) {
            acidPools.clear()
            changeStateTo(State.FIGHTING)
        }
    }


    @Subscribe
    fun onGameTick(e: GameTick) {
        if (running) {
            if (tickDelay > 0) { // Tick delay
                tickDelay--
                return
            }

            when (botState) {
                State.WALKING_TO_BANK -> walkingToBankState()
                State.BANKING -> bankingState()
                State.WALKING_TO_VORKATH -> walkingToVorkathState()
                State.PREPARE -> prepareState()
                State.POKE -> pokeState()
                State.FIGHTING -> fightingState()
                State.ACID -> acidState()
                State.SPAWN -> spawnState()
                State.RED_BALL -> redBallState()
                State.LOOTING -> lootingState()
                State.THINKING -> thinkingState()
                State.WALKING_TO_GE -> walkingToGEState()
                State.GETTING_ITEM -> gettingItemState()
                State.SELLING -> sellingItemState()
                State.MULING -> mulingState()
                State.NONE -> println("None State")
            }
        }
    }

    private fun mulingState() {
        if (!isMoving()) {
            if (Bank.isOpen()) {
                if (BankInventory.search().withName("Coins").result().isEmpty()) {
                    Bank.search().withName("Coins").first().ifPresent { coins ->
                        BankInteraction.useItem(coins, "Withdraw-All")
                        tickDelay = 1
                        return@ifPresent
                    }
                } else {
                    Players.search().withName(config.MULENAME()).first().ifPresent { mule ->
                        PlayerInteractionHelper.interact(mule, "Trade with")
                        tickDelay = 1
                        return@ifPresent
                    }
                }
            } else {
                if (Widgets.search().hiddenState(false).withTextContains("Other player has accepted.").result()
                        .isNotEmpty()
                ) {
                    Widgets.search().withAction("Accept").hiddenState(false).first().ifPresent { accept ->
                        MousePackets.queueClickPacket()
                        WidgetPackets.queueWidgetAction(accept, "Accept")
                        tickDelay = 1
                        return@ifPresent
                    }
                }
                if (Widgets.search().hiddenState(false).withTextContains("Trading with:").result().isNotEmpty()) {
                    if (TradeInventory.search().withName("Coins").result().isNotEmpty()) {
                        TradeInventory.search().withName("Coins").first().ifPresent { coins ->
                            tickDelay = 4
                            MousePackets.queueClickPacket()
                            WidgetPackets.queueWidgetAction(coins, "Offer-All")
                            return@ifPresent
                        }
                    }
                    Widgets.search().withAction("Accept").hiddenState(false).first().ifPresent { accept ->
                        MousePackets.queueClickPacket()
                        WidgetPackets.queueWidgetAction(accept, "Accept")
                        tickDelay = 1
                        return@ifPresent
                    }
                    return
                }
                if (Inventory.search().withName("Coins").result().isEmpty()) {
                    changeStateTo(State.WALKING_TO_BANK, 1)
                    return
                }
            }
        }
    }

    private fun sellingItemState() {
        if (!isMoving()) {
            if (geIsOpen()) {
                if (geInventoryHasLoot() && Widgets.search().withTextContains("Select an offer slot to set up")
                        .hiddenState(false).result().isNotEmpty()
                ) {
                    val itemToSell = GrandExchangeInventory.search().result()
                        .firstOrNull { !it.name.contains(config.TELEPORT().toString()) }
                    itemToSell?.let {
                        GeInventoryInteraction.offerItem(itemToSell)
                        //println("Offered Item")
                        tickDelay = 1
                        return
                    } ?: run {
                        changeStateTo(State.GETTING_ITEM, 1)
                        return
                    }
                }
                if (Widgets.search().withTextContains("Confirm").hiddenState(false).result().isNotEmpty()) {
                    Widgets.search().withAction("-5%").first().ifPresent { minus ->
                        MousePackets.queueClickPacket()
                        WidgetPackets.queueWidgetAction(minus, "-5%")
                        //println("Minus 5%")
                        MousePackets.queueClickPacket()
                        WidgetPackets.queueWidgetAction(minus, "-5%")
                        //println("Minus 5%")
                    }
                    Widgets.search().withAction("Confirm").first().ifPresent { confirm ->
                        MousePackets.queueClickPacket()
                        WidgetPackets.queueWidgetAction(confirm, "Confirm")
                        //println("Confirm")
                        lootNames.remove(lootNames.toList()[0])
                        tickDelay = 1
                        return@ifPresent
                    }
                    return
                }
                if (Widgets.search().withAction("Collect to inventory").hiddenState(false).result().isNotEmpty()) {
                    Widgets.search().withAction("Collect to inventory").first().ifPresent { collect ->
                        MousePackets.queueClickPacket()
                        WidgetPackets.queueWidgetAction(collect, "Collect to inventory")
                    }
                    NPCs.search().nameContains("Banker").nearestToPlayer().ifPresent { banker ->
                        NPCInteraction.interact(banker, "Bank")
                    }
                    changeStateTo(State.GETTING_ITEM, 1)
                } else {
                    NPCs.search().nameContains("Banker").nearestToPlayer().ifPresent { banker ->
                        NPCInteraction.interact(banker, "Bank")
                    }
                    changeStateTo(State.GETTING_ITEM, 1)
                }
            }
        }
    }

    private fun gettingItemState() {
        if (lootNames.isEmpty()) {
            if (Bank.isOpen()) {
                BankInventory.search().withId(995).first().ifPresent { coins ->
                    BankInventoryInteraction.useItem(coins, "Deposit-All")
                }
                Widgets.search().withAction("Close").first().ifPresent { close ->
                    MousePackets.queueClickPacket()
                    WidgetPackets.queueWidgetAction(close, "Close")
                }
            }
            if (config.MULE() && config.MULENAME()
                    .isNotEmpty()
            ) changeStateTo(State.MULING) else changeStateTo(State.WALKING_TO_BANK)
            return
        }
        if (!isMoving()) {
            if (Bank.isOpen()) {
                if (BankInventory.search().withId(995).result().isNotEmpty()) {
                    BankInventory.search().withId(995).first().ifPresent { coins ->
                        BankInteraction.useItem(coins, "Deposit-All")
                        tickDelay = 1
                        return@ifPresent
                    }
                }
                if (bankInventoryHasLoot()) {
                    NPCs.search().nameContains("Grand Exchange Clerk").nearestToPlayer().ifPresent { clerk ->
                        NPCInteraction.interact(clerk, "Exchange")
                    }
                    changeStateTo(State.SELLING, 1)
                    return
                } else {
                    if (client.getVarbitValue(3958) == 0) {
                        Widgets.search().withId(786456).withAction("Note").first().ifPresent { note ->
                            MousePackets.queueClickPacket()
                            WidgetPackets.queueWidgetAction(note, "Note")
                            return@ifPresent
                        }
                    }
                    Bank.search().withName(lootNames.toList()[0]).first().ifPresent { item ->
                        BankInteraction.useItem(item, "Withdraw-All")
                        tickDelay = 1
                        return@ifPresent
                    }
                }
            } else {
                NPCs.search().nameContains("Banker").nearestToPlayer().ifPresent { banker ->
                    NPCInteraction.interact(banker, "Bank")
                    return@ifPresent
                }
            }
        }
    }

    private fun walkingToGEState() {
        if (!isMoving()) {
            if (inVorkathArea() || !inGE() && !inHouse()) {
                teleToHouse()
                return
            }
            if (inHouse()) {
                TileObjects.search().nameContains("Ornate Jewellery Box").first().ifPresent { box ->
                    TileObjectInteraction.interact(box, "Grand Exchange")
                    return@ifPresent
                }
            }
            if (Bank.isOpen()) {
                BankInventory.search().result().filter { !it.name.contains(config.TELEPORT().toString()) }
                    .forEach { item ->
                        BankInteraction.useItem(item, "Deposit-All")
                    }
                if (BankInventory.search().result().size == 1) { // only has teleport in inventory
                    //println("LootId Before Check: $lootNames")
                    lootNames = lootNames.filter {
                        Bank.search().quantityGreaterThan(1).tradeAble().withName(it).result().isNotEmpty()
                    }.toMutableSet()
                    //println("LootId After Check: $lootNames")
                    changeStateTo(State.GETTING_ITEM, 1)
                    return
                }
            } else {
                NPCs.search().nameContains("Banker").nearestToPlayer().ifPresent { banker ->
                    NPCInteraction.interact(banker, "Bank")
                    return@ifPresent
                }
            }
        }
    }

    private fun lootingState() {
        if (lootQueue.isEmpty() || TileItems.search().empty()) {
            if (killCount % config.SELLAT() == 0 && killCount != 0) changeStateTo(State.WALKING_TO_GE)
            else changeStateTo(State.WALKING_TO_BANK, 1)
            return
        }
        if (Inventory.search().nameContains(config.CROSSBOW().toString()).result().isNotEmpty()) {
            Inventory.search().nameContains(config.CROSSBOW().toString()).first().ifPresent { crossbow ->
                InventoryInteraction.useItem(crossbow, "Wield")
            }
        }
        if (Inventory.search().nameContains("Ruby dragon bolts (e)").result().isNotEmpty()) {
            Inventory.search().nameContains("Ruby dragon bolts (e)").first().ifPresent { bolts ->
                InventoryInteraction.useItem(bolts, "Wield")
            }
        }
        lootQueue.forEach {
            if (!isMoving()) {
                if (!Inventory.full()) {
                    TileItems.search().withId(it.id).first().ifPresent { item ->
                        item.interact(false)
                        lootQueue.removeAt(lootQueue.indexOf(it))
                        tickDelay = if (isMoving()) 3 else 1
                    }
                    return
                } else {
                    EthanApiPlugin.sendClientMessage("Inventory full, going to bank.")
                    lootQueue.clear()
                    if (killCount % config.SELLAT() == 0 && killCount != 0) changeStateTo(State.WALKING_TO_GE)
                    else changeStateTo(State.WALKING_TO_BANK, 1)
                    return
                }
            }
        }
    }

    private fun acidState() {
        if (!runIsOff()) enableRun()
        PrayerInteraction.setPrayerState(Prayer.PROTECT_FROM_MAGIC, false)
        if (!inVorkathArea()) {
            acidPools.clear()
            changeStateTo(State.THINKING)
            return
        }

        val vorkath = NPCs.search().nameContains("Vorkath").first().get().worldLocation
        val swPoint = WorldPoint(vorkath.x + 1, vorkath.y - 8, 0)

        fun findSafeTiles(): WorldPoint? {
            val wooxWalkArea = WorldArea(swPoint, 5, 1)
            //println("Woox Walk Area: ${wooxWalkArea.toWorldPointList()}")

            fun isTileSafe(tile: WorldPoint): Boolean = tile !in acidPools
                    && WorldPoint(tile.x, tile.y + 1, tile.plane) !in acidPools
                    && WorldPoint(tile.x, tile.y + 2, tile.plane) !in acidPools
                    && WorldPoint(tile.x, tile.y + 3, tile.plane) !in acidPools


            val safeTiles = wooxWalkArea.toWorldPointList().filter { isTileSafe(it) }

            // Find the closest safe tile by x-coordinate to the player
            return safeTiles.minByOrNull { abs(it.x - client.localPlayer.worldLocation.x) }
        }

        TileObjects.search().withId(ObjectID.ACID_POOL_32000).result().forEach { tileObject ->
            acidPools.add(tileObject.worldLocation)
        }

        TileObjects.search().withId(ObjectID.ACID_POOL).result().forEach { tileObject ->
            acidPools.add(tileObject.worldLocation)
        }

        TileObjects.search().withId(ObjectID.ACID_POOL_37991).result().forEach { tileObject ->
            acidPools.add(tileObject.worldLocation)
        }

        val safeTile: WorldPoint? = findSafeTiles()
        //println("Acid pools: $acidPools")
        //println("Left Tile: $swPoint")
        //println("Safe tile: $safeTile")

        val playerLocation = client.localPlayer.worldLocation

        safeTile?.let {
            if (playerLocation == safeTile) {
                // Attack Vorkath if the player close to the safe tile
                NPCs.search().nameContains("Vorkath").first().ifPresent { vorkath ->
                    NPCInteraction.interact(vorkath, "Attack")
                    //println("Attacked Vorkath")
                }
            } else {
                eat(config.EATAT())
                // Move to the safe tile if the player is not close enough
                MousePackets.queueClickPacket()
                //println("Moving to safe tile: $safeTile")
                //println("Player location: $playerLocation")
                MovementPackets.queueMovement(safeTile)
            }
        } ?: run {
            EthanApiPlugin.sendClientMessage("NO SAFE TILES! TELEPORTING TF OUT!")
            teleToHouse()
            changeStateTo(State.WALKING_TO_BANK)
        }
    }

    private fun redBallState() {
        drinkPrayer()
        eat(config.EATAT())
        MousePackets.queueClickPacket()
        MovementPackets.queueMovement(WorldPoint(redBallLocation.x + 2, redBallLocation.y, redBallLocation.plane))
        changeStateTo(State.FIGHTING, 2)
    }

    private fun spawnState() {
        if (!inVorkathArea()) {
            changeStateTo(State.THINKING)
            return
        }
        activatePrayers(false)
        drinkPrayer()
        if (Equipment.search().nameContains(config.SLAYERSTAFF().toString()).result().isEmpty()) {
            Inventory.search().nameContains(config.SLAYERSTAFF().toString()).first().ifPresent { staff ->
                InventoryInteraction.useItem(staff, "Wield")
            }
            return
        } else {
            NPCs.search().nameContains("Zombified Spawn").first().ifPresent { spawn ->
                NPCInteraction.interact(spawn, "Attack")
            }
        }
    }

    private fun fightingState() {
        if (runIsOff()) enableRun()
        acidPools.clear()
        if (!inVorkathArea()) {
            changeStateTo(State.THINKING)
            return
        } else {
            val vorkath = NPCs.search().nameContains("Vorkath").first().get()
            val maxHealth = vorkath.healthScale
            val currentHealthRatio = vorkath.healthRatio
            val middle = WorldPoint(vorkath.worldLocation.x + 3, vorkath.worldLocation.y - 5, 0)
            if (isVorkathAsleep()) {
                changeStateTo(State.WALKING_TO_BANK)
                return
            }
            if (client.localPlayer.interacting == null) {
                if (maxHealth != -1 && currentHealthRatio != -1) {
                    val healthPercentage = (currentHealthRatio * 100) / maxHealth
                    //println("HEALTH PERCENTAGE: $healthPercentage")

                    if (healthPercentage < 35 && config.SWITCHBOLTS()) {
                        Inventory.search().nameContains("Diamond dragon bolts (e)").first().ifPresent {
                            InventoryInteraction.useItem(it, "Wield")
                        }
                        NPCInteraction.interact(vorkath, "Attack")
                        return
                    } else {
                        Inventory.search().nameContains("Ruby dragon bolts (e)").first().ifPresent {
                            InventoryInteraction.useItem(it, "Wield")
                        }
                        NPCInteraction.interact(vorkath, "Attack")
                        return
                    }
                }
            }
            if (client.localPlayer.worldLocation != middle) {
                if (!isMoving()) {
                    MousePackets.queueClickPacket()
                    MovementPackets.queueMovement(middle)
                }
            }
            eat(config.EATAT())
            if (Inventory.search().nameContains(config.CROSSBOW().toString()).result().isNotEmpty()) {
                InventoryInteraction.useItem(config.CROSSBOW().toString(), "Wield")
            }

        }
    }

    private fun pokeState() {
        if (isVorkathAsleep()) {
            acidPools.clear()
            lootQueue.clear()
            if (!isMoving()) {
                NPCs.search().withAction("Poke").first().ifPresent { sleepingVorkath ->
                    NPCInteraction.interact(sleepingVorkath, "Poke")
                }
            }
        } else {
            val vorkath = NPCs.search().nameContains("Vorkath").first().get().worldLocation
            val middle = WorldPoint(vorkath.x + 3, vorkath.y - 5, 0)
            MousePackets.queueClickPacket()
            MovementPackets.queueMovement(middle)
            changeStateTo(State.FIGHTING)
            return
        }
    }

    private fun walkingToVorkathState() {
        if (runIsOff()) enableRun()
        activatePrayers(false)
        if (!isMoving()) {
            if (bankArea.contains(client.localPlayer.worldLocation)) {
                if (Widgets.search().withTextContains("Click here to continue").result().isNotEmpty()) {
                    sendKey(KeyEvent.VK_SPACE)
                    return
                }
                if (client.localPlayer.worldLocation != bankLocation) {
                    MousePackets.queueClickPacket()
                    MovementPackets.queueMovement(bankLocation)
                } else {
                    NPCs.search().nameContains("Sirsal Banker").nearestToPlayer().ifPresent { banker ->
                        NPCInteraction.interact(banker, "Talk-to")
                    }
                }
            } else {
                if (inVorkathArea()) {
                    changeStateTo(State.THINKING, 3)
                    return
                }
                if (fremennikArea.contains(client.localPlayer.worldLocation)) {
                    TileObjects.search().withId(29917).withAction("Travel").nearestToPlayer().ifPresent { boat ->
                        TileObjectInteraction.interact(boat, "Travel")
                    }
                } else {
                    if (TileObjects.search().withId(31990).result().isNotEmpty()) {
                        TileObjects.search().withId(31990).first().ifPresent { iceChunk ->
                            TileObjectInteraction.interact(iceChunk, "Climb-over")
                        }
                    } else {
                        changeStateTo(State.WALKING_TO_BANK)
                    }
                }
            }
        }
    }

    private fun bankingState() {
        activatePrayers(false)
        if (bankArea.contains(client.localPlayer.worldLocation)) {
            if (!isMoving()) {
                if (!Bank.isOpen()) {
                    if (client.localPlayer.worldLocation != bankLocation) {
                        MousePackets.queueClickPacket()
                        MovementPackets.queueMovement(bankLocation)
                        return
                    } else {
                        NPCs.search().nameContains("Jack").nearestToPlayer().ifPresent { bank ->
                            NPCInteraction.interact(bank, "Bank")
                        }
                        tickDelay = 1
                        return
                    }
                } else {
                    bank()
                    return
                }
            }
        } else {
            changeStateTo(State.THINKING)
            return
        }
    }

    private fun walkingToBankState() {
        if (runIsOff()) enableRun()
        activatePrayers(false)
        if (breakHandler.shouldBreak(this)) { // Break handler
            breakHandler.startBreak(this)
        }
        if (!isMoving()) {
            if (bankArea.contains(client.localPlayer.worldLocation)) {
                changeStateTo(State.THINKING)
                return
            }
            if (!inHouse()) {
                teleToHouse()
                return
            }
            if (client.getBoostedSkillLevel(Skill.HITPOINTS) < config.POOLDRINK().width || client.getBoostedSkillLevel(
                    Skill.PRAYER
                ) < config.POOLDRINK().height
            ) {
                TileObjects.search().nameContains("pool of").withAction("Drink").first().ifPresent { pool ->
                    TileObjectInteraction.interact(pool, "Drink")
                }
                return
            }
            if (inHouse()) {
                TileObjects.search().nameContains(config.PORTAL().toString()).first().ifPresent { portal ->
                    TileObjectInteraction.interact(portal, config.PORTAL().action())
                }
                return
            }
        }
    }

    private fun thinkingState() {
        if (readyToFight()) { // Check if player has all potions and food
            if (inVorkathArea()) { // Check if player in Vorkath area
                if (isPrepared) { // Has drank potions
                    changeStateTo(State.POKE)
                    return
                } else { // Hasn't drank potions
                    changeStateTo(State.PREPARE) // Drink potions
                    return
                }
            } else { // walk to vorkath
                changeStateTo(State.WALKING_TO_VORKATH)
                return
            }
        } else { // If player doesn't have all potions and food
            drankRangePotion = false
            drankAntiFire = false
            isPrepared = false
            if (bankArea.contains(client.localPlayer.worldLocation)) { // Player is in bank area
                changeStateTo(State.BANKING)
                return
            } else { // Player is not in bank area
                if (killCount == 0) {
                    changeStateTo(State.WALKING_TO_BANK)
                    return
                }
                if (killCount % config.SELLAT() == 0) changeStateTo(State.WALKING_TO_GE) else changeStateTo(State.WALKING_TO_BANK)
                return
            }
        }
    }

    private fun prepareState() {
        val currentTime = System.currentTimeMillis()

        if (!drankRangePotion || currentTime - lastDrankRangePotion > config.RANGEPOTION().time()) {
            Inventory.search().nameContains(config.RANGEPOTION().toString()).first().ifPresent { rangePotion ->
                InventoryInteraction.useItem(rangePotion, "Drink")
                lastDrankRangePotion = System.currentTimeMillis()
                drankRangePotion = true
                tickDelay = 2
            }
            return
        }
        if (!drankAntiFire || currentTime - lastDrankAntiFire > config.ANTIFIRE().time()) {
            Inventory.search().nameContains(config.ANTIFIRE().toString()).first().ifPresent { antiFire ->
                InventoryInteraction.useItem(antiFire, "Drink")
                lastDrankAntiFire = System.currentTimeMillis()
                drankAntiFire = true
                tickDelay = 2
            }
            return
        }
        drinkPrayer()
        drinkAntiVenom()
        isPrepared = drankAntiFire && drankRangePotion && !inventoryHasLoot()
        if (isPrepared) {
            changeStateTo(State.THINKING)
            return
        } else {
            changeStateTo(State.WALKING_TO_BANK)
            return
        }
    }

    private fun drinkAntiVenom() {
        if (Equipment.search().nameContains("Serpentine helm").result().isEmpty()) {
            if (client.getVarbitValue(VarPlayer.POISON) >= -38) {
                Inventory.search().nameContains("Anti-venom").first().ifPresent {
                    InventoryInteraction.useItem(it, "Drink")
                    tickDelay = 2
                }
                return
            }
        }
    }

    private fun drinkPrayer() {
        if (needsToDrinkPrayer()) {
            if (Inventory.search().nameContains(config.PRAYERPOTION().toString()).result().isNotEmpty()) {
                Inventory.search().nameContains(config.PRAYERPOTION().toString()).first().ifPresent { prayerPotion ->
                    InventoryInteraction.useItem(prayerPotion, "Drink")
                }
                tickDelay = 2
                return
            } else {
                isPrepared = false
                drankRangePotion = false
                drankAntiFire = false
                teleToHouse()
                changeStateTo(State.WALKING_TO_BANK)
                return
            }
        }
    }

    private fun bank() {
        lootNames.forEach { item ->
            if (hasItem(item)) {
                BankInventoryInteraction.useItem(item, "Deposit-All")
            }
        }
        if (!hasItem(config.TELEPORT().toString())) {
            withdraw(config.TELEPORT().toString(), 1)
        }
        if (BankInventory.search().nameContains(config.RANGEPOTION().toString()).result().size <= 1) {
            withdraw(config.RANGEPOTION().toString(), 1)
        }
        if (!hasItem(config.SLAYERSTAFF().toString())) {
            withdraw(config.SLAYERSTAFF().toString(), 1)
        }
        if (BankInventory.search().nameContains(config.PRAYERPOTION().toString()).result().size <= 1) {
            withdraw(config.PRAYERPOTION().toString(), 1)
        }
        if (!hasItem("Rune pouch")) {
            withdraw("Rune pouch", 1)
        }
        if (BankInventory.search().nameContains(config.ANTIFIRE().toString()).result().size <= 1) {
            withdraw(config.ANTIFIRE().toString(), 1)
        }
        if (config.SWITCHBOLTS()) {
            if (!hasItem("Diamond dragon bolts (e)")) {
                Bank.search().nameContains("Diamond dragon bolts (e)").first().ifPresent { bolts ->
                    BankInteraction.useItem(bolts, "Withdraw-All")
                }
            }
        }
        if (Equipment.search().nameContains("Serpentine helm").result().isEmpty()) {
            if (!hasItem("Anti-venom")) {
                withdraw("Anti-venom", 1)
            }
        }
        if (!Inventory.full()) {
            for (i in 1..config.FOODAMOUNT().width - Inventory.getItemAmount(config.FOOD())) {
                withdraw(config.FOOD(), 1)
            }
        }
        changeStateTo(State.WALKING_TO_VORKATH)
    }

    private fun inVorkathArea(): Boolean =
        NPCs.search().nameContains("Vorkath").result().isNotEmpty() && client.isInInstancedRegion

    private fun isVorkathAsleep(): Boolean = NPCs.search().withId(8059).result().isNotEmpty()
    private fun inHouse(): Boolean = TileObjects.search().nameContains(config.PORTAL().toString()).result().isNotEmpty()

    private fun inGE(): Boolean = NPCs.search().nameContains("Grand Exchange Clerk").result().isNotEmpty()

    private fun geIsOpen(): Boolean =
        Widgets.search().withTextContains("Grand Exchange").hiddenState(false).result().isNotEmpty()

    private fun isMoving(): Boolean = EthanApiPlugin.isMoving() || client.localPlayer.animation != -1
    private fun needsToDrinkPrayer(): Boolean = client.getBoostedSkillLevel(Skill.PRAYER) <= 70

    private fun readyToFight(): Boolean =
        Inventory.search().nameContains(config.FOOD()).result().size >= config.FOODAMOUNT().height
                && Inventory.search().nameContains(config.ANTIFIRE().toString()).result().isNotEmpty()
                && Inventory.search().nameContains(config.RANGEPOTION().toString()).result().isNotEmpty()
                && Inventory.search().nameContains(config.SLAYERSTAFF().toString()).result().isNotEmpty()
                && Inventory.search().nameContains(config.TELEPORT().toString()).result().isNotEmpty()
                && Inventory.search().nameContains("Rune pouch").result().isNotEmpty()
                && Inventory.search().nameContains(config.PRAYERPOTION().toString()).result().isNotEmpty()
                && !inventoryHasLoot()

    private fun needsToEat(at: Int): Boolean = client.getBoostedSkillLevel(Skill.HITPOINTS) <= at

    private fun eat(at: Int) {
        if (needsToEat(at)) {
            if (Inventory.search().withAction("Eat").result().isNotEmpty()) {
                Inventory.search().withAction("Eat").first().ifPresent { food ->
                    InventoryInteraction.useItem(food, "Eat")
                }
            } else {
                isPrepared = false
                drankRangePotion = false
                drankAntiFire = false
                initialAcidMove = false
                teleToHouse()
                changeStateTo(State.WALKING_TO_BANK)
                return
            }
        }
    }

    private fun inventoryHasLoot(): Boolean {
        lootNames.forEach { item ->
            if (Inventory.search().withName(item).result().isNotEmpty()) {
                return true
            }
        }
        return false
    }

    private fun bankInventoryHasLoot(): Boolean {
        lootNames.forEach { item ->
            if (BankInventory.search().withName(item).result().isNotEmpty()) {
                return true
            }
        }
        return false
    }

    private fun geInventoryHasLoot(): Boolean {
        lootNames.forEach { item ->
            if (GrandExchangeInventory.search().withName(item).result().isNotEmpty()) {
                return true
            }
        }
        return false
    }


    private fun sendKey(key: Int) {
        keyEvent(KeyEvent.KEY_PRESSED, key)
        keyEvent(KeyEvent.KEY_RELEASED, key)
    }

    private fun keyEvent(id: Int, key: Int) {
        val e = KeyEvent(
            client.canvas,
            id,
            System.currentTimeMillis(),
            0,
            key,
            KeyEvent.CHAR_UNDEFINED
        )
        client.canvas.dispatchEvent(e)
    }

    private fun hasItem(name: String): Boolean = BankInventory.search().nameContains(name).result().isNotEmpty()
    private fun withdraw(name: String, amount: Int) {
        Bank.search().nameContains(name).first().ifPresent { item ->
            BankInteraction.withdrawX(item, amount)
        }
    }

    private fun runIsOff(): Boolean = EthanApiPlugin.getClient().getVarpValue(173) == 0

    private fun enableRun() {
        MousePackets.queueClickPacket()
        WidgetPackets.queueWidgetActionPacket(1, 10485787, -1, -1)
    }

    private fun activatePrayers(on: Boolean) {
        if (config.ACTIVATERIGOUR()) {
            PrayerInteraction.setPrayerState(Prayer.RIGOUR, on)
        }
        PrayerInteraction.setPrayerState(Prayer.PROTECT_FROM_MAGIC, on)
    }

    private fun teleToHouse() {
        Inventory.search().nameContains(config.TELEPORT().toString()).first().ifPresent { teleport ->
            InventoryInteraction.useItem(teleport, config.TELEPORT().action())
        }
    }

    private fun changeStateTo(stateName: State, ticksToDelay: Int = 0) {
        botState = stateName
        tickDelay = ticksToDelay
        // println("State : $stateName")
    }
}
