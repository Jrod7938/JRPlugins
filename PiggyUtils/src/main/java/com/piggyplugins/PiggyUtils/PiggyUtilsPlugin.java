package com.piggyplugins.PiggyUtils;

import com.example.EthanApiPlugin.Collections.GrandExchangeInventory;
import com.example.EthanApiPlugin.Collections.TradeInventory;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.RuneLite;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;

@PluginDescriptor(name = "<html><font color=\"#FF9DF9\">[PP]</font> PiggyUtils</html>",
                description = "Utility Plugin for PiggyPlugins",
                tags = {"piggy","ethan"})
@Slf4j
public class PiggyUtilsPlugin extends Plugin {
    @Inject
    EventBus eventBus;

    @Override
    protected void startUp() throws Exception {
        log.info("[PiggyUtils] Piggy Utils started");
        eventBus.register(RuneLite.getInjector().getInstance(GrandExchangeInventory.class));
        eventBus.register(RuneLite.getInjector().getInstance(TradeInventory.class));
    }
}
