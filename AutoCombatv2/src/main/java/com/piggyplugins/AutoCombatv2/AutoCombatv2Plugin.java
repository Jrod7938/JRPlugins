package com.piggyplugins.AutoCombatv2;


import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.*;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.piggyplugins.AutoCombatv2.tasks.CheckCombatStatus;
import com.piggyplugins.AutoCombatv2.tasks.attackNPC;
import com.piggyplugins.PiggyUtils.API.PlayerUtil;
import com.piggyplugins.PiggyUtils.strategy.AbstractTask;
import com.piggyplugins.PiggyUtils.strategy.TaskManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

@PluginDescriptor(
        name = "<html><font color=\"#ff4d00\">[BS]</font> Auto Combat</html>",
        description = "",
        enabledByDefault = false,
        tags = {"BS", "piggy", "PP", "plugin"}
)
@Slf4j
public class AutoCombatv2Plugin extends Plugin {
    @Inject
    @Getter
    private Client client;
    @Inject
    private AutoCombatv2Config config;
    @Inject
    private AutoCombatv2Overlay overlay;
    @Inject
    private KeyManager keyManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    @Getter
    private ClientThread clientThread;
    public boolean started = false;
    public int timeout = 0;
    public TaskManager taskManager = new TaskManager();
    public boolean inCombat;
    public int idleTicks = 0;
    @Inject
    PlayerUtil playerUtil;

    @Provides
    private AutoCombatv2Config getConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoCombatv2Config.class);
    }

    @Override
    protected void startUp() throws Exception {
        overlayManager.add(overlay);
        timeout = 0;
        inCombat = false;
        keyManager.registerKeyListener(toggle);
    }

    @Override
    protected void shutDown() throws Exception {
        inCombat = false;
        timeout = 0;
        idleTicks = 0;
        started = false;
        keyManager.unregisterKeyListener(toggle);
        overlayManager.remove(overlay);
    }


    @Subscribe
    private void onGameTick(GameTick event) {
        if (client.getGameState() != GameState.LOGGED_IN || !started) {
            return;
        }

        if (playerUtil.isInteracting() || client.getLocalPlayer().getAnimation() == -1) {
            idleTicks++;
        } else {
            idleTicks = 0;
        }

        if (timeout > 0) {
            timeout--;
            return;
        }

        log.info("inCombat? {}", inCombat);
        checkRunEnergy();
        if (taskManager.hasTasks()) {
            for (AbstractTask t : taskManager.getTasks()) {
                if (t.validate()) {
                    t.execute();
//                    return;
                }
            }
        }
    }

    private void checkRunEnergy() {
        if (runIsOff() && client.getEnergy() >= 30 * 100) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetActionPacket(1, 10485787, -1, -1);
        }
    }

    private boolean runIsOff() {
        return EthanApiPlugin.getClient().getVarpValue(173) == 0;
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
        if (started) {
            taskManager.addTask(new CheckCombatStatus(this, config));
            taskManager.addTask(new attackNPC(this, config));
        } else {
            taskManager.clearTasks();
        }
    }
}