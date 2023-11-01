package com.polyplugins.AutoCombat.helper;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.google.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;

import java.util.Optional;


public class SlayerHelper {
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;

    //do stuff like check name against slayer npcs that require items to finish off
    //get slayer task?
    //call slayer master to get task? prob not its autocb not slayer

}