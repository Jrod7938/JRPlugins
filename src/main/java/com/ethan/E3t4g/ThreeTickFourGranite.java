package com.ethan.E3t4g;

import com.ethan.EthanApiPlugin.Collections.Equipment;
import com.ethan.EthanApiPlugin.Collections.Inventory;
import com.ethan.EthanApiPlugin.EthanApiPlugin;
import com.ethan.InteractionApi.InventoryInteraction;
import com.ethan.PacketUtils.PacketUtilsPlugin;
import com.ethan.PacketUtils.WidgetInfoExtended;
import com.ethan.Packets.MousePackets;
import com.ethan.Packets.ObjectPackets;
import com.ethan.Packets.WidgetPackets;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.piggyplugins.PiggyUtils.BreakHandler.ReflectBreakHandler;
import lombok.Getter;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ItemID;
import net.runelite.api.VarPlayer;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

import java.util.Optional;

@PluginDescriptor(
        name = "<html><font color=\"#FF9DF9\">[EV]</font> 3t4g</html>",
        enabledByDefault = false
)
@PluginDependency(EthanApiPlugin.class)
@PluginDependency(PacketUtilsPlugin.class)
public class ThreeTickFourGranite extends Plugin {
    @Inject
    Client client;
    @Inject
    private ReflectBreakHandler breakHandler;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private KeyManager keyManager;
    @Inject
    private ThreeTickFourGraniteConfig config;
    @Inject
    private ThreeTickFourGraniteOverlay overlay;
    @Getter
    boolean started;
    int[][] rockPos = new int[][]{{3165, 2908}, {3165, 2909}, {3165, 2910}, {3167, 2911}};
    int timeout = 0;
    int rock = 0;

    @Provides
    private ThreeTickFourGraniteConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(ThreeTickFourGraniteConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        breakHandler.registerPlugin(this);
        keyManager.registerKeyListener(toggle);
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() throws Exception {
        breakHandler.unregisterPlugin(this);
        keyManager.unregisterKeyListener(toggle);
        overlayManager.remove(overlay);
    }

    @Subscribe
    public void onGameTick(GameTick e) {
        if (client.getGameState() != GameState.LOGGED_IN || !started
            || breakHandler.isBreakActive(this)) {
            return;
        }

        if (breakHandler.shouldBreak(this)) {
            breakHandler.startBreak(this);
            return;
        }

        timeout = timeout == 0 ? 2 : timeout - 1;
        if (timeout != 2) return;
        if (client.getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT) == 1000) {
            if (!Equipment.search().matchesWildCardNoCase("*Dragon pickaxe*").empty()||!Equipment.search().matchesWildCardNoCase("*infernal pickaxe*").empty()) {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetActionPacket(1, 38862884, -1, -1);
            }
        }
        int sizeEmpty = Inventory.search().withId(ItemID.WATERSKIN0).result().size();
        int sizeFilled = Inventory.search().nameContains("Waterskin").result().size();
        if (sizeEmpty > 0) {
            if (sizeEmpty == sizeFilled) {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetAction(client.getWidget(WidgetInfoExtended.SPELL_HUMIDIFY.getPackedId()), "Cast");
                timeout = 10;
                return;
            }
        }
        Optional<Widget> guam = Inventory.search().withId(ItemID.GUAM_LEAF).first();
        Optional<Widget> tar = Inventory.search().withId(ItemID.SWAMP_TAR).first();
        Optional<Widget> pestle = Inventory.search().withId(ItemID.PESTLE_AND_MORTAR).first();
        if (guam.isEmpty() || tar.isEmpty() || pestle.isEmpty()) {
            EthanApiPlugin.stopPlugin(this);
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "please make sure you have guam leaf and swamp tar" +
                    " and a pestle and mortar before starting", null);
            return;
        }

        if (Inventory.search().withId(ItemID.GUAM_LEAF).onlyUnnoted().result().size() > 1) {
            EthanApiPlugin.stopPlugin(this);
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "plugin not able to work with more than one " +
                    "cleaned guam in inventory", null);
            return;
        }
        if (tar.get().getItemQuantity() <= 15) {
            EthanApiPlugin.stopPlugin(this);
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "plugin not able to work with less than 15 swamp tar" +
                    " in inventory", null);
            return;
        }
        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetOnWidget(guam.get(), tar.get());
        rock = rock == 3 ? 0 : rock + 1;
        Inventory.search().nameContains("Granite").first().ifPresent(item -> InventoryInteraction.useItem(item, "Drop"));
        MousePackets.queueClickPacket();
        ObjectPackets.queueObjectAction(1, 11387, rockPos[rock][0], rockPos[rock][1], false);
    }

    private final HotkeyListener toggle = new HotkeyListener(() -> config.e3t4gToggle()) {
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

        if (!started) {
            breakHandler.stopPlugin(this);
        } else {
            breakHandler.startPlugin(this);
        }
    }
}