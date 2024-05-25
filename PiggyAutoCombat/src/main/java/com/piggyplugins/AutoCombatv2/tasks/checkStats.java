package com.piggyplugins.AutoCombatv2.tasks;

import com.example.EthanApiPlugin.Collections.Bank;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.InteractionApi.InventoryInteraction;
import com.piggyplugins.AutoCombatv2.AutoCombatv2Config;
import com.piggyplugins.AutoCombatv2.AutoCombatv2Plugin;
import com.piggyplugins.PiggyUtils.strategy.AbstractTask;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Skill;
import net.runelite.api.widgets.Widget;

import java.util.Optional;

@Slf4j
public class checkStats extends AbstractTask<AutoCombatv2Plugin, AutoCombatv2Config> {

    public checkStats(AutoCombatv2Plugin plugin, AutoCombatv2Config config) {
        super(plugin, config);
    }

    @Override
    public boolean validate() {
        return true; // Always return true as we want to check it every tick
    }

    @Override
    public void execute() {
        consumeFood();
    }

    public void consumeFood() {
        if (plugin.getClient().getBoostedSkillLevel(Skill.HITPOINTS) <= config.EatAt()) {
            InventoryInteraction.useItem(config.foodToEat(), "Eat");
            log.info("Attempting to consume food for health recovery.");
        } else {
//            log.info("Failed to eat food: {}", config.foodToEat());
        }
    }

    public Widget findFood() {
        Optional<Widget> food = Inventory.search()
                .onlyUnnoted()
                .withAction("Eat")
                .matchesWildCardNoCase(config.foodToEat().toLowerCase())
                .first();

        return food.orElse(null);
    }
}
