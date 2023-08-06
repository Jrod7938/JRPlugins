package com.piggyplugins;

import com.piggyplugins.AutoJugHumidifier.AutoJugHumidifierPlugin;
import com.piggyplugins.AutoRifts.AutoRiftsPlugin;
import com.piggyplugins.CannonReloader.CannonReloaderPlugin;
import com.piggyplugins.ChinBreakHandler.ChinBreakHandlerPlugin;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.piggyplugins.HerbCleaner.HerbCleanerPlugin;
import com.piggyplugins.ItemCombiner.ItemCombinerPlugin;
import com.piggyplugins.JadAutoPrayers.JadAutoPrayersPlugin;
import com.example.PacketUtils.PacketUtilsPlugin;
import com.piggyplugins.OneTickSwitcher.OneTickSwitcherPlugin;
import com.piggyplugins.PiggyUtils.PiggyUtilsPlugin;
import com.piggyplugins.PowerSkiller.PowerSkillerPlugin;
import com.piggyplugins.PrayAgainstPlayer.PrayAgainstPlayerPlugin;
import com.piggyplugins.RooftopAgility.RooftopAgilityPlugin;
import com.piggyplugins.ShiftClickWalker.ShiftClickWalkerPlugin;
import com.piggyplugins.SixHourLog.SixHourLogPlugin;
import com.piggyplugins.SpeedDartMaker.SpeedDartMakerPlugin;
import com.piggyplugins.VardorvisHelper.VardorvisHelperPlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ExamplePluginTest {
    public static void main(String[] args) throws Exception {
        ExternalPluginManager.loadBuiltin(EthanApiPlugin.class, PacketUtilsPlugin.class,
                //new stuff
                PiggyUtilsPlugin.class, ChinBreakHandlerPlugin.class, HerbCleanerPlugin.class,
                ItemCombinerPlugin.class, RooftopAgilityPlugin.class, JadAutoPrayersPlugin.class,
                SpeedDartMakerPlugin.class, OneTickSwitcherPlugin.class, AutoRiftsPlugin.class,
                PrayAgainstPlayerPlugin.class, PowerSkillerPlugin.class, AutoJugHumidifierPlugin.class,
                CannonReloaderPlugin.class, SixHourLogPlugin.class, VardorvisHelperPlugin.class,
                ShiftClickWalkerPlugin.class);
        RuneLite.main(args);
    }
}