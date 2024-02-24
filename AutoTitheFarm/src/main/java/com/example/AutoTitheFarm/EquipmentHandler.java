package com.example.AutoTitheFarm;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.InteractionApi.InventoryInteraction;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.widgets.Widget;
import net.runelite.client.util.Text;

import java.util.List;

@Slf4j
public class EquipmentHandler {

    ActionDelayHandler actionDelayHandler;

    AutoTitheFarmConfig config;

    private final String gearName;

    private final String action;


    public EquipmentHandler(String gearName, AutoTitheFarmConfig config, ActionDelayHandler actionDelayHandler) {
        this.gearName = gearName;
        this.config = config;
        this.action = "Wear";
        this.actionDelayHandler = actionDelayHandler;
    }

    private List<Widget> getGear() {
        return Inventory.search().nameContains(this.gearName).result();
    }

    public boolean isInInventory() {
        return config.switchGearDuringHarvestingPhase() && !getGear().isEmpty();
    }

    public void gearSwitch() {
        if (actionDelayHandler.isWaitForAction()) {
            return;
        }
        getGear().forEach(itm -> {
            InventoryInteraction.useItem(itm, this.action);
            log.info("Equipping " + Text.removeTags(itm.getName()));
        });
    }
}
