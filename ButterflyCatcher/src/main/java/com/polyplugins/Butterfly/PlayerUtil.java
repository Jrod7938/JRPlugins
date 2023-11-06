package com.polyplugins.Butterfly;


import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.google.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.VarPlayer;
import net.runelite.api.Varbits;


public class PlayerUtil {
//
//    @Inject
//    static Client client;

    /**
     * Run energy the way we'd use it
     *
     * @return
     */
    public static int runEnergy(Client client) {
        return client.getEnergy() * 100;
    }

    public static boolean isStaminaActive(Client client) {
        return client.getVarbitValue(Varbits.RUN_SLOWED_DEPLETION_ACTIVE) == 1;
    }

    public static boolean isRunning(Client client) {
        return client.getVarpValue(173) == 0;
    }

    public static boolean inMulti(Client client) {
        return client.getVarbitValue(Varbits.MULTICOMBAT_AREA) == 1;
    }

    public static boolean isInteracting(Client client) {
        return client.getLocalPlayer().isInteracting();
    }

    public static boolean isBeingInteracted() {
        return NPCs.search().interactingWithLocal().first().isPresent();
    }

    /**
     * Slayer task count
     *
     * @return
     */
    public static int getTaskCount(Client client) {
        return client.getVarpValue(VarPlayer.SLAYER_TASK_SIZE);
    }

}