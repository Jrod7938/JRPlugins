package com.byronplugins;

import com.byronplugins.ChinBreakHandler.ChinBreakHandlerPlugin;
import com.byronplugins.EthanApiPlugin.EthanApiPlugin;
import com.byronplugins.HerbCleaner.HerbCleanerPlugin;
import com.byronplugins.ItemCombiner.ItemCombinerPlugin;
import com.byronplugins.JadAutoPrayers.JadAutoPrayers;
import com.byronplugins.PacketUtils.PacketUtilsPlugin;
import com.byronplugins.RooftopAgility.RooftopAgility;
import com.byronplugins.SpeedDartMaker.SpeedDartMakerPlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ExamplePluginTest {
    public static void main(String[] args) throws Exception {
        ExternalPluginManager.loadBuiltin(EthanApiPlugin.class, PacketUtilsPlugin.class,

                //new stuff
                ChinBreakHandlerPlugin.class, HerbCleanerPlugin.class, ItemCombinerPlugin.class,
                SpeedDartMakerPlugin.class, RooftopAgility.class, JadAutoPrayers.class);
        RuneLite.main(args);
    }
}