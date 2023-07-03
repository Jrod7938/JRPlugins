package com.piggyplugins.SpeedDartMaker;

import com.piggyplugins.EthanApiPlugin.Collections.Inventory;
import com.piggyplugins.EthanApiPlugin.EthanApiPlugin;
import com.piggyplugins.PacketUtils.PacketUtilsPlugin;
import com.piggyplugins.Packets.MousePackets;
import com.piggyplugins.Packets.WidgetPackets;
import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.HotkeyListener;

@PluginDescriptor(
        name = "Speed Dart Maker",
        description = "Fletches every type of dart in your inventory X amount of times per tick.",
        enabledByDefault = false,
        tags = {"ethan", "piggy"}
)
@PluginDependency(EthanApiPlugin.class)
@PluginDependency(PacketUtilsPlugin.class)
@Slf4j
public class SpeedDartMakerPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private MousePackets mousePackets;
    @Inject
    private WidgetPackets widgetPackets;
    @Inject
    private EthanApiPlugin api;
    @Inject
    private SpeedDartMakerConfig config;
    @Inject
    private KeyManager keyManager;

    private boolean started = false;

    @Provides
    private SpeedDartMakerConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(SpeedDartMakerConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        keyManager.registerKeyListener(toggle);
    }

    @Override
    protected void shutDown() throws Exception {
        keyManager.unregisterKeyListener(toggle);
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (client.getGameState() != GameState.LOGGED_IN
            || !started
            || !hasDarts()
            || !hasFeather()) {
            return;
        }

        fletchDarts();
    }

    private void fletchDarts() {
        Widget feather = Inventory.search().nameContains("Feather").first().get();
        int dartTypes = Inventory.search().nameContains("dart tip").result().size();

        for (int i = 0; i < calcPerTick(dartTypes); i++) {
            Inventory.search().nameContains("dart tip").result().forEach(item -> {
                MousePackets.queueClickPacket();
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetOnWidget(item, feather);
            });
        }
    }

    // waaay too crazy if you go any higher than this
    // i wouldn't risk it, but have fun if you want
    // better to be hardcoded than inside the config.
    private int calcPerTick(int types) {
        if (types > 4) {
            return 1;
        } else if (types > 2) {
            return 3;
        } else {
            return 4;
        }
    }

    private boolean hasDarts() {
        return Inventory.search().nameContains("dart tip").first().isPresent();
    }

    private boolean hasFeather() {
        return Inventory.search().nameContains("Feather").first().isPresent();
    }

    private final HotkeyListener toggle = new HotkeyListener(() -> config.toggle()) {
        @Override
        public void hotkeyPressed() {
            toggle();
        }
    };

    public void toggle() {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        started = !started;
    }
}
