package com.piggyplugins;

import com.example.AutoTitheFarm.AutoTitheFarmPlugin;
import com.example.E3t4g.et34g;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.PacketUtils.PacketUtilsPlugin;
import com.example.PathingTesting.PathingTesting;
import com.example.PrayerFlicker.EthanPrayerFlickerPlugin;
import com.example.RunEnabler.RunEnabler;
import com.example.UpkeepPlugin.UpkeepPlugin;
import com.example.harpoon2ticker.SwordFish2Tick;
import com.jrplugins.AutoChop.AutoChop;
import com.jrplugins.autoVorkath.AutoVorkathPlugin;
import com.piggyplugins.AutoAerial.AutoAerialPlugin;
import com.piggyplugins.AutoJugHumidifier.AutoJugHumidifierPlugin;
import com.piggyplugins.AutoRifts.AutoRiftsPlugin;
import com.piggyplugins.AutoSmith.AutoSmith;
import com.piggyplugins.Firemaking.FiremakingPlugin;
import com.piggyplugins.HerbCleaner.HerbCleanerPlugin;
import com.piggyplugins.ItemCombiner.ItemCombinerPlugin;
import com.piggyplugins.LeftClickBlackJack.LeftClickBlackJackPlugin;
import com.piggyplugins.PiggyUtils.PiggyUtilsPlugin;
import com.piggyplugins.PowerSkiller.PowerSkillerPlugin;
import com.piggyplugins.RooftopAgility.RooftopAgilityPlugin;
import com.piggyplugins.SixHourLog.SixHourLogPlugin;
import com.piggyplugins.SpeedDartMaker.SpeedDartMakerPlugin;
import com.piggyplugins.VardorvisHelper.VardorvisHelperPlugin;
import com.piggyplugins.autoLeviathanPrayers.AutoLeviathanPrayer;
import com.piggyplugins.autoWhispererPrayers.AutoWhispererPrayer;
import com.polyplugins.AutoBoner.AutoBonerPlugin;
import com.polyplugins.Butterfly.ButterflyPlugin;
import com.polyplugins.Chompy.AutoChompyPlugin;
import com.polyplugins.Dialogue.DialogueContinuerPlugin;
import com.polyplugins.KittenFeeder.KittenFeederPlugin;
import com.polyplugins.Trapper.AutoTrapperPlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;
import net.runelite.client.plugins.cannon.CannonPlugin;
import rsnhider.RsnHiderPlugin;

public class PluginTester {
    public static void main(String[] args) throws Exception {
        ExternalPluginManager.loadBuiltin(
                EthanApiPlugin.class,
                PacketUtilsPlugin.class,
                AutoAerialPlugin.class,
                AutoBonerPlugin.class,
                ButterflyPlugin.class,
                FiremakingPlugin.class,
                DialogueContinuerPlugin.class,
                KittenFeederPlugin.class,
                AutoChompyPlugin.class,
                PowerSkillerPlugin.class,
                AutoTrapperPlugin.class,
                SpeedDartMakerPlugin.class,
                RooftopAgilityPlugin.class,
                UpkeepPlugin.class,
                AutoChop.class,
                PathingTesting.class,
                PiggyUtilsPlugin.class,
                SixHourLogPlugin.class,
                AutoSmith.class,
                AutoRiftsPlugin.class,
                AutoJugHumidifierPlugin.class,
                et34g.class,
                SwordFish2Tick.class,
                HerbCleanerPlugin.class,
                ItemCombinerPlugin.class,
                EthanPrayerFlickerPlugin.class,
                RunEnabler.class,
                AutoVorkathPlugin.class,
                RsnHiderPlugin.class,
                AutoTitheFarmPlugin.class,
                VardorvisHelperPlugin.class,
                AutoLeviathanPrayer.class,
                AutoWhispererPrayer.class,
                CannonPlugin.class,
                LeftClickBlackJackPlugin.class
        );
        RuneLite.main(args);
    }
}