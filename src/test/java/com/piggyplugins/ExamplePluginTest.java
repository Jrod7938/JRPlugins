package com.piggyplugins;

import com.piggyplugins.ChinBreakHandler.ChinBreakHandlerPlugin;
import com.piggyplugins.EthanApiPlugin.EthanApiPlugin;
import com.piggyplugins.HerbCleaner.HerbCleanerPlugin;
import com.piggyplugins.ItemCombiner.ItemCombinerPlugin;
import com.piggyplugins.JadAutoPrayers.JadAutoPrayersPlugin;
import com.piggyplugins.PacketUtils.PacketUtilsPlugin;
import com.piggyplugins.RooftopAgility.RooftopAgilityPlugin;
import com.piggyplugins.SpeedDartMaker.SpeedDartMakerPlugin;
import com.piggyplugins.OneTickSwitcher.OneTickSwitcherPlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ExamplePluginTest {
    public static void main(String[] args) throws Exception {
        ExternalPluginManager.loadBuiltin(EthanApiPlugin.class, PacketUtilsPlugin.class,

                //new stuff
                ChinBreakHandlerPlugin.class, HerbCleanerPlugin.class, ItemCombinerPlugin.class,
                 RooftopAgilityPlugin.class, JadAutoPrayersPlugin.class, SpeedDartMakerPlugin.class,
                OneTickSwitcherPlugin.class);
        RuneLite.main(args);
    }
}