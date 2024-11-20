package com.example.PrayerFlicker;

import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.PacketUtils.PacketUtilsPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Skill;
import net.runelite.api.Varbits;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginInstantiationException;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.util.HotkeyListener;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

@PluginDescriptor(
        name = "<html><font color=\"#FF9DF9\">[PP]</font> Prayer Flicker</html>",
        description = "prayer flicker for quick prayers by Ethan Vann, maintained by Piggy Plugins",
        enabledByDefault = false,
        tags = {"ethan"}
)
@Slf4j
public class EthanPrayerFlickerPlugin extends Plugin {
    public int timeout = 0;
    @Inject
    Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private KeyManager keyManager;
    @Inject
    private PrayerFlickerConfig config;
    @Inject
    PluginManager pluginManager;

    private ExecutorService executorService;

    private final int quickPrayerWidgetID = WidgetInfo.MINIMAP_QUICK_PRAYER_ORB.getPackedId();

    @Provides
    public PrayerFlickerConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(PrayerFlickerConfig.class);
    }

    private void togglePrayer() {
        clientThread.invoke(() -> {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetActionPacket(1, quickPrayerWidgetID, -1, -1);
        });
    }

    @Override
    @SneakyThrows
    public void startUp() {
        executorService = Executors.newSingleThreadExecutor();

        if (client.getRevision() != PacketUtilsPlugin.CLIENT_REV) {
            SwingUtilities.invokeLater(() ->
            {
                try {
                    pluginManager.setPluginEnabled(this, false);
                    pluginManager.stopPlugin(this);
                } catch (PluginInstantiationException ignored) {
                }
            });
            return;
        }
        keyManager.registerKeyListener(prayerToggle);
    }

    @Override
    public void shutDown() {
        executorService.shutdownNow();
        executorService = null;

        log.info("Shutdown");
        keyManager.unregisterKeyListener(prayerToggle);
        toggle = false;
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        clientThread.invoke(() ->
        {
            if (client.getVarbitValue(Varbits.QUICK_PRAYER) == 1) {
                togglePrayer();
            }
        });
    }

    boolean toggle;

    public void switchAndUpdatePrayers(int i) {
        clientThread.invoke(() -> {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetActionPacket(1, 5046276, -1, i);
        });
        updatePrayers();
    }

    public void updatePrayers() {
        executorService.submit(() -> {
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(0, 100));
                togglePrayer();
                Thread.sleep(ThreadLocalRandom.current().nextInt(60, 270));
                togglePrayer();
            } catch (InterruptedException e) {

            }
        });
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        if (toggle) {
            if (event.getParam1() == 5046276) {
                if (event.getMenuOption().equals("Quick Prayer Update")) {
                    updatePrayers();
                    event.consume();
                    return;
                }
                event.consume();
                switchAndUpdatePrayers(event.getParam0());
            }
        }
        if (config.minimapToggle() && event.getId() == 1 && event.getParam1() == WidgetInfo.MINIMAP_QUICK_PRAYER_ORB.getId()) {
            toggleFlicker();
            event.consume();
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) throws NoSuchFieldException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        if (toggle) {
            if (client.getBoostedSkillLevel(Skill.PRAYER) < 1) return;
            if (client.getVarbitValue(Varbits.QUICK_PRAYER) == 1) {
                executorService.submit(() -> {
                    try {
                        Thread.sleep(ThreadLocalRandom.current().nextInt(10, 80));
                        togglePrayer();
                        Thread.sleep(ThreadLocalRandom.current().nextInt(50, 210));
                    } catch (InterruptedException e) {

                    }
                });
            }
            executorService.submit(() -> {
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(10, 80));
                    togglePrayer();
                } catch (InterruptedException e) {

                }
            });
        }
    }

    private final HotkeyListener prayerToggle = new HotkeyListener(() -> config.toggle()) {
        @Override
        public void hotkeyPressed() {
            toggleFlicker();
        }
    };

    public void toggleFlicker() {
        toggle = !toggle;
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        if (!toggle) {
            clientThread.invoke(() ->
            {
                if (client.getVarbitValue(Varbits.QUICK_PRAYER) == 1) {
                    togglePrayer();
                }
            });
        }
    }

    public void toggleFlicker(boolean on) {
        toggle = on;
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        if (!toggle) {
            clientThread.invoke(() ->
            {
                if (client.getVarbitValue(Varbits.QUICK_PRAYER) == 1) {
                    togglePrayer();
                }
            });
        }
    }
}
