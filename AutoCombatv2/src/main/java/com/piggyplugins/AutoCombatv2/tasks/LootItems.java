package com.piggyplugins.AutoCombatv2.tasks;

import com.example.EthanApiPlugin.Collections.Bank;
import com.example.EthanApiPlugin.Collections.ETileItem;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.TileItemPackets;
import com.piggyplugins.AutoCombatv2.AutoCombatv2Config;
import com.piggyplugins.PiggyUtils.strategy.AbstractTask;
import com.piggyplugins.AutoCombatv2.AutoCombatv2Plugin;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import java.util.Optional;

@Slf4j
public class LootItems extends AbstractTask<AutoCombatv2Plugin, AutoCombatv2Config> {

    public LootItems(AutoCombatv2Plugin plugin, AutoCombatv2Config config) {
        super(plugin, config);
    }

    @Override
    public boolean validate() {
        return !plugin.getLootQueue().isEmpty();
    }

    @Override
    public void execute() {
        Pair<TileItem, Tile> lootPair = plugin.getLootQueue().poll();
        if (lootPair != null) {
            TileItem loot = lootPair.getLeft();
            Tile lootTile = lootPair.getRight();
            log.info("Processing loot: {} at {}", plugin.getItemManager().getItemComposition(loot.getId()).getName(), lootTile.getWorldLocation());

                MousePackets.queueClickPacket();
                TileItemPackets.queueTileItemAction(new ETileItem(lootTile.getWorldLocation(), loot), false);
            }
        }
    }
