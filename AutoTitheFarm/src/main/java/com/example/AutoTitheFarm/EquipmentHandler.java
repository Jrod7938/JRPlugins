package com.example.AutoTitheFarm;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.InteractionApi.InventoryInteraction;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.widgets.Widget;

import java.util.List;

@Slf4j
public class EquipmentHandler {

    AutoTitheFarmConfig config;

    private final String gearName;

    private final String action;


    public EquipmentHandler(String gearName, AutoTitheFarmConfig config) {
        this.gearName = gearName;
        this.config = config;
        this.action = "Wear";
    }

    private List<Widget> getGear() {
        return Inventory.search().nameContains(this.gearName).result();
    }

    public boolean isInInventory() {
        if (!config.switchGearDuringHarvestingPhase()) {
            return false;
        }
        return !getGear().isEmpty();
    }

    public void gearSwitch() {
        getGear().forEach(itm -> {
            InventoryInteraction.useItem(itm, this.action);
            log.info("Switching to: " + this.gearName);
        });
    }
}