package com.polyplugins.AutoCombat.util;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.query.NPCQuery;
import com.google.inject.Inject;
import com.polyplugins.AutoCombat.AutoCombatConfig;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.NPC;
import net.runelite.api.Skill;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;

import java.util.Optional;

@Slf4j
public class Util {
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private AutoCombatConfig config;

    /**
     * Finds and returns the nearest npc
     *
     * @param name Name of npc (uses contains)
     * @return The nearest npc, or null if none are found
     */
    public NPC findNpc(String name) {
        NPCQuery npc = NPCs.search().alive().withName(name).withAction("Attack").notInteracting();
//        log.info("Found npcs:" + npc.result().size());
        return npc.nearestToPlayer().orElse(null);
    }

    public boolean isInteracting() {
        return client.getLocalPlayer().isInteracting();
    }

    public boolean isBeingInteracted() {
        return NPCs.search().interactingWithLocal().first().isPresent();
    }

    public NPC getBeingInteracted() {
        Optional<NPC> npc = NPCs.search().interactingWithLocal().first();
        log.info("NPC: " + npc.get().getName());
        return npc.orElse(null);
    }

}
