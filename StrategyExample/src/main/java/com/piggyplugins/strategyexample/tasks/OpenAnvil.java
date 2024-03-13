package com.piggyplugins.strategyexample.tasks;

import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.PacketUtils.WidgetInfoExtended;
import com.google.inject.Inject;
import com.piggyplugins.PiggyUtils.strategy.AbstractTask;
import com.piggyplugins.strategyexample.StrategySmithConfig;
import com.piggyplugins.strategyexample.StrategySmithPlugin;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.TileObject;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;

import java.util.Optional;

@Slf4j
public class OpenAnvil extends AbstractTask<StrategySmithPlugin, StrategySmithConfig> {
//    @Inject
//    protected Client client;
//    @Inject
//    protected ClientThread clientThread;
    private Optional<TileObject> anvil;

    public OpenAnvil(StrategySmithPlugin plugin, StrategySmithConfig config) {
        super(plugin, config);
    }

    @Override
    public boolean validate() {
        anvil = TileObjects.search().withName("Anvil").nearestToPlayer();
        Widget smithingInterface = plugin.getClient().getWidget(WidgetInfoExtended.SMITHING_INVENTORY_ITEMS_CONTAINER.getPackedId());
        return anvil.isPresent() && smithingInterface == null && plugin.hasEnoughBars() && plugin.hasHammer();
    }

    @Override
    public void execute() {
        log.info("interacting with anvil");
        boolean action = interactObject(anvil.get(), "Smith");
//        boolean action = TileObjectInteraction.interact(anvil.get(), "Smith");
        if (!action)
            log.info("failed anvil interaction");
        plugin.timeout = config.tickDelay() == 0 ? 1 : config.tickDelay();//must be at least 1
    }
}
