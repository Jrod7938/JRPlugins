package com.piggyplugins.EthanApiPlugin.Collections;

import com.piggyplugins.EthanApiPlugin.Collections.query.NPCQuery;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.events.GameTick;
import net.runelite.client.RuneLite;
import net.runelite.client.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class NPCs {
    static Client client = RuneLite.getInjector().getInstance(Client.class);
    private static final List<NPC> npcList = new ArrayList<>();

    public static NPCQuery search() {
        return new NPCQuery(npcList);
    }

    public static boolean isNpcWithinRange(String name) {
        return search().nameContainsNoCase(name).nearestToPlayer().isPresent();
    }

    public static NPC getNearestNpcToPlayer(String name) {
        return search().nameContainsNoCase(name).nearestToPlayer().get();
    }

    @Subscribe(priority = 10000)
    public void onGameTick(GameTick e) {
        npcList.clear();
        for (NPC npc : client.getNpcs()) {
            if (npc == null)
                continue;
            if (npc.getId() == -1)
                continue;
            npcList.add(npc);
        }
    }
}
