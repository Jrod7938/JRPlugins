package org.example.twotickthreetickteaks;

import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.example.twotickthreetickteaks.config.ChopMode;
import org.example.twotickthreetickteaks.config.TickChopperConfig;
import org.example.twotickthreetickteaks.strategy.ThreeTickStrategy;
import org.example.twotickthreetickteaks.strategy.TwoTickStrategy;


@PluginDescriptor(
        name = "<html><font color=\"#FF9DF9\">[PP]</font> Tick Teaks</html>",
        description = "Supports 2T (Prif) & 3T Teaks (Anywhere)",
        enabledByDefault = false
)
@Slf4j
public class TickChopper extends Plugin {
    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;

    @Inject
    private TickChopperOverlay overlay;
    @Inject
    private TickChopperConfig config;
    @Inject
    private TwoTickStrategy twoTickStrategy;
    @Inject
    private ThreeTickStrategy threeTickStrategy;

    public static long timeBegan;

    @Provides
    TickChopperConfig getConfig(ConfigManager manager) {
        return manager.getConfig(TickChopperConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        if (client.getGameState() == GameState.LOGGED_IN) {
            timeBegan = System.currentTimeMillis();

            if (overlayManager != null) {
                overlayManager.add(overlay);
            }
        }

        super.startUp();
    }

    @Subscribe
    public void onGameTick(GameTick gameTick) throws Exception {
        if (config.chopMode() == ChopMode.NOT_SET) {
            super.shutDown();
        }

        if (config.chopMode() == ChopMode.TWO_TICK) {
            twoTickStrategy.execute();
        } else {
            threeTickStrategy.execute();
        }
    }

    @Override
    protected void shutDown() throws Exception {
        if (overlay != null) {
            overlayManager.remove(overlay);
        }

        twoTickStrategy.resetState();
        threeTickStrategy.resetState();

        super.shutDown();
    }
}
