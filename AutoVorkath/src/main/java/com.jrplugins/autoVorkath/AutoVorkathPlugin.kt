package com.jrplugins.autoVorkath

import com.example.EthanApiPlugin.Collections.*
import com.example.EthanApiPlugin.EthanApiPlugin
import com.example.InteractionApi.*
import com.example.Packets.MousePackets
import com.example.Packets.MovementPackets
import com.example.Packets.WidgetPackets
import com.google.inject.Provides
import com.piggyplugins.PiggyUtils.BreakHandler.ReflectBreakHandler
import lombok.Setter
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
import java.util.*
import java.util.stream.Collectors
import javax.inject.Inject


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

    var botState: State? = null
    var tickDelay: Int = 0
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

    private val lootQueue: MutableList<ItemStack> = mutableListOf()
    private var acidPools: HashSet<WorldPoint> = hashSetOf()

    private var initialAcidMove = false

    @Setter
    private var lootNames: List<String>? = null

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
        THINKING,
        NONE
    }

    override fun startUp() {
        println("Auto Vorkath Plugin Activated")
        botState = State.THINKING
        running = client.gameState == GameState.LOGGED_IN
        breakHandler.registerPlugin(this)
        breakHandler.startPlugin(this)
        overlayManager.add(autoVorkathOverlay)
    }

    override fun shutDown() {
        println("Auto Vorkath Plugin Deactivated")
        running = false
        botState = null
        drankAntiFire = false
        drankRangePotion = false
        lootQueue.clear()
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
        items.stream().filter { item: ItemStack ->
            val comp: ItemComposition = itemManager.getItemComposition(item.id)
            getLootNames()!!.contains(comp.name)
        }.forEach { it: ItemStack? ->
            if (it != null) {
                lootQueue.add(it)
            }
        }
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
            whiteProjectileId -> {
                drinkPrayer()
                eat(75)
                Inventory.search().nameContains(config.SLAYERSTAFF().name).first().ifPresent { staff ->
                    InventoryInteraction.useItem(staff, "Wield")
                }
                changeStateTo(State.SPAWN)
            }
            acidRedProjectileId -> changeStateTo(State.ACID)
            rangeProjectileId, magicProjectileId, purpleProjectileId, blueProjectileId -> activatePrayers(true)
            redProjectileId -> changeStateTo(State.RED_BALL)
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
                State.NONE -> println("None State")
                null -> println("Null State")
            }
        }
    }

    private fun lootingState() {
        if (lootQueue.isEmpty()) {
            changeStateTo(State.WALKING_TO_BANK, 1)
            return
        }
        if (Inventory.search().nameContains(config.CROSSBOW().toString()).result().isNotEmpty()) {
            Inventory.search().nameContains(config.CROSSBOW().toString()).first().ifPresent { crossbow ->
                InventoryInteraction.useItem(crossbow, "Wield")
            }
        }
        val itemStack: ItemStack = lootQueue[0]
        TileItems.search().withId(itemStack.id).first().ifPresent { item: ETileItem ->
            val comp = itemManager.getItemComposition(item.getTileItem().id)
            if (comp.isStackable || comp.note != -1) {
                if (hasStackableLoot(comp)) {
                    item.interact(false)
                }
            }
            if (!Inventory.full()) {
                item.interact(false)
            } else {
                EthanApiPlugin.sendClientMessage("Inventory full, going to bank.")
                changeStateTo(State.WALKING_TO_BANK)
            }
        }
        lootQueue.removeAt(0)
        if (isMoving()) tickDelay = 4
        return
    }

    private fun acidState() {
        if (!runIsOff()) enableRun()
        activatePrayers(false)
        if (!inVorkathArea()) {
            acidPools.clear()
            changeStateTo(State.THINKING)
            return
        }

        val vorkath = NPCs.search().nameContains("Vorkath").first().get().worldLocation
        val swPoint = WorldPoint(vorkath.x + 1, vorkath.y - 11, 0)

        fun findSafeTiles(): WorldPoint? {
            val wooxWalkArea = WorldArea(swPoint, 5, 1)
            //println("Woox Walk Area: ${wooxWalkArea.toWorldPointList()}")

            // Function to check if a tile has an acid pool
            fun isTileSafe(tile: WorldPoint): Boolean = tile !in acidPools
                    && WorldPoint(tile.x, tile.y + 1, tile.plane) !in acidPools
                    && WorldPoint(tile.x, tile.y + 2, tile.plane) !in acidPools
                    && WorldPoint(tile.x, tile.y + 3, tile.plane) !in acidPools
                    && WorldPoint(tile.x, tile.y + 4, tile.plane) !in acidPools
                    && WorldPoint(tile.x, tile.y + 5, tile.plane) !in acidPools
                    && WorldPoint(tile.x, tile.y + 6, tile.plane) !in acidPools

            return wooxWalkArea.toWorldPointList().firstOrNull { isTileSafe(it) }
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

        if (playerLocation.distanceTo(safeTile) <= 3) {
            // Attack Vorkath if the player close to the safe tile
            NPCs.search().nameContains("Vorkath").first().ifPresent { vorkath ->
                NPCInteraction.interact(vorkath, "Attack")
                //println("Attacked Vorkath")
            }
        } else {
            // Move to the safe tile if the player is not close enough
            MousePackets.queueClickPacket()
            //println("Moving to safe tile: $safeTile")
            //println("Player location: $playerLocation")
            MovementPackets.queueMovement(safeTile)
        }
    }

    private fun redBallState() {
        drinkPrayer()
        eat(75)
        MousePackets.queueClickPacket()
        MovementPackets.queueMovement(
            WorldPoint(
                client.localPlayer.worldLocation.x + 2,
                client.localPlayer.worldLocation.y,
                0
            )
        )
        changeStateTo(State.FIGHTING, 2)
    }

    private fun spawnState() {
        if (!inVorkathArea()) {
            changeStateTo(State.THINKING)
            return
        }
        NPCs.search().nameContains("Zombified Spawn").first().ifPresent { spawn ->
            NPCInteraction.interact(spawn, "Attack")
        }
    }

    private fun fightingState() {
        if (runIsOff()) enableRun()
        acidPools.clear()
        if (!inVorkathArea()) {
            changeStateTo(State.THINKING)
            return
        } else {
            val vorkath = NPCs.search().nameContains("Vorkath").first().get().worldLocation
            val middle = WorldPoint(vorkath.x + 3, vorkath.y - 5, 0)
            if (isVorkathAsleep()) {
                changeStateTo(State.WALKING_TO_BANK)
                return
            }
            if (client.localPlayer.interacting == null) {
                NPCs.search().nameContains("Vorkath").first().ifPresent { vorkath ->
                    NPCInteraction.interact(vorkath, "Attack")
                }
                return
            }
            if (client.localPlayer.worldLocation != middle) {
                if (!isMoving()) {
                    MousePackets.queueClickPacket()
                    MovementPackets.queueMovement(middle)
                }
            }
            eat(75)
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
                    } else {
                        NPCs.search().nameContains("Jack").nearestToPlayer().ifPresent { bank ->
                            NPCInteraction.interact(bank, "Bank")
                        }
                        tickDelay = 1
                        return
                    }
                } else {
                    bank()
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
            if (client.getBoostedSkillLevel(Skill.HITPOINTS) <= 98 || client.getBoostedSkillLevel(Skill.PRAYER) <= 98) {
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
                changeStateTo(State.WALKING_TO_BANK)
                return
            }
        }
    }

    private fun prepareState() {
        if (!drankRangePotion) {
            Inventory.search().nameContains(config.RANGEPOTION().toString()).first().ifPresent { rangingPotion ->
                InventoryInteraction.useItem(rangingPotion, "Drink")
            }
            drankRangePotion = true
            tickDelay = 2
            return
        }
        if (!drankAntiFire) {
            Inventory.search().nameContains(config.ANTIFIRE().toString()).first().ifPresent { antiFire ->
                InventoryInteraction.useItem(antiFire, "Drink")
            }
            drankAntiFire = true
            tickDelay = 2
            return
        }

        drinkPrayer()

        if (Equipment.search().nameContains("Serpentine helm").result().isEmpty()) {
            Inventory.search().nameContains("Anti-venom").first().ifPresent {
                InventoryInteraction.useItem("Anti-venom", "Drink")
            }
        }
        isPrepared = drankAntiFire && drankRangePotion && !inventoryHasLoot()
        if (isPrepared) {
            changeStateTo(State.THINKING)
            return
        } else {
            changeStateTo(State.WALKING_TO_BANK)
            return
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
        getLootNames()?.forEach { lootName ->
            if (BankInventory.search().nameContains(lootName).result().isNotEmpty()) {
                BankInventoryInteraction.useItem(lootName, "Deposit-All")
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
        if (!Inventory.full()) {
            for (i in 1..config.FOODAMOUNT().width - Inventory.getItemAmount(config.FOOD())) {
                withdraw(config.FOOD(), 1)
            }
        }
        changeStateTo(State.THINKING)
    }

    private fun inVorkathArea(): Boolean =
        NPCs.search().nameContains("Vorkath").result().isNotEmpty() && client.isInInstancedRegion

    private fun isVorkathAsleep(): Boolean = NPCs.search().withId(8059).result().isNotEmpty()
    private fun inHouse(): Boolean = TileObjects.search().nameContains(config.PORTAL().toString()).result().isNotEmpty()

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
        getLootNames()?.forEach { lootName ->
            if (Inventory.search().nameContains(lootName).result().isNotEmpty()) {
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

    fun hasItem(name: String): Boolean = Inventory.search().nameContains(name).result().isNotEmpty()
    fun withdraw(name: String, amount: Int) {
        Bank.search().nameContains(name).first().ifPresent { item ->
            BankInteraction.withdrawX(item, amount)
        }
    }

    private fun runIsOff(): Boolean = EthanApiPlugin.getClient().getVarpValue(173) == 0

    private fun enableRun() {
        MousePackets.queueClickPacket()
        WidgetPackets.queueWidgetActionPacket(1, 10485787, -1, -1)
    }

    private fun getLootNames(): List<String>? {
        if (lootNames == null) lootNames =
            Arrays.stream<String>(config.LOOTNAMES().split(",".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()).map<String> { obj: String -> obj.trim { it <= ' ' } }
                .collect(Collectors.toList<String>())
        return lootNames
    }

    private fun hasStackableLoot(comp: ItemComposition): Boolean {
        val name = comp.name
        val itemQry = Inventory.search().withName(name)
        if (itemQry.first().isEmpty) {
            return false
        }
        return itemQry.onlyNoted().first().isPresent || itemQry.quantityGreaterThan(1).first().isPresent
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
