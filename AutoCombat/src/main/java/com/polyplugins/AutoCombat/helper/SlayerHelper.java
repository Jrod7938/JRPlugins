package com.polyplugins.AutoCombat.helper;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.google.inject.Inject;
import com.polyplugins.AutoCombat.SlayerNpc;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;

import java.util.Arrays;
import java.util.Optional;


public class SlayerHelper {
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;

    //do stuff like check name against slayer npcs that require items to finish off
    //get slayer task?
    //call slayer master to get task? prob not its autocb not slayer

    public boolean isSlayerNPC(NPC npc) {
        String name = npc.getName();
        for (SlayerNpc snpc : SlayerNpc.values()) {
            if (snpc.getNpcName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public SlayerNpc getSlayerInfo(NPC npc) {
        String name = npc.getName();
        if (!isSlayerNPC(npc))
            return null;
        return Arrays.stream(SlayerNpc.values()).filter(snpc ->
                snpc.getNpcName().equals(name)).findFirst().orElse(null);

    }

}