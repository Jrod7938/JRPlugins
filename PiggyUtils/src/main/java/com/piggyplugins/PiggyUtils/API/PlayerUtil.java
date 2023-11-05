package com.piggyplugins.PiggyUtils.API;

import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.google.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.VarPlayer;
import net.runelite.api.Varbits;


public class PlayerUtil {

    @Inject
    static Client client;

    /**
     * Run energy the way we'd use it
     *
     * @return
     */
    public static int runEnergy() {
        return client.getEnergy() * 100;
    }

    public static boolean isStaminaActive() {
        return client.getVarbitValue(Varbits.RUN_SLOWED_DEPLETION_ACTIVE) == 1;
    }

    public static boolean isRunning() {
        return client.getVarpValue(173) == 0;
    }

    public static boolean inMulti() {
        return client.getVarbitValue(Varbits.MULTICOMBAT_AREA) == 1;
    }

    public static boolean isInteracting() {
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
    public static int getTaskCount() {
        return client.getVarpValue(VarPlayer.SLAYER_TASK_SIZE);
    }

}
