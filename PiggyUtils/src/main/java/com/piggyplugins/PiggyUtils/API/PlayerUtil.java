package com.piggyplugins.PiggyUtils.API;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.query.NPCQuery;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.VarPlayer;
import net.runelite.api.Varbits;

@Slf4j
public class PlayerUtil {
    @Inject
    private Client client;

    public boolean hasItem(String name) {
        return Inventory.getItemAmount(name) > 0;
    }

    public boolean hasItem(int id) {
        return Inventory.getItemAmount(id) > 0;
    }

    /**
     * Run energy the way we'd use it
     *
     * @return
     */
    public int runEnergy() {
        return client.getEnergy() / 100;
    }

    public boolean isStaminaActive() {
        return client.getVarbitValue(Varbits.RUN_SLOWED_DEPLETION_ACTIVE) == 1;
    }

    public boolean isRunning() {
        return client.getVarpValue(173) == 0;
    }

    public boolean inMulti() {
        return client.getVarbitValue(Varbits.MULTICOMBAT_AREA) == 1;
    }

    public boolean isInteracting() {
        return client.getLocalPlayer().isInteracting();
    }

    public boolean isBeingInteracted() {
        return NPCs.search().interactingWithLocal().first().isPresent();
    }

    public boolean isBeingInteracted(String name) {
        return NPCs.search().withNameIgnoreCase(name).interactingWithLocal().first().isPresent();
    }

    public NPCQuery getBeingInteracted(String name) {
        return NPCs.search().withNameIgnoreCase(name).interactingWithLocal();
    }

    /**
     * Slayer task count
     *
     * @return
     */
    public int getTaskCount() {
        return client.getVarpValue(VarPlayer.SLAYER_TASK_SIZE);
    }

}
