package com.piggyplugins

import com.example.AutoTitheFarm.AutoTitheFarmPlugin
import com.example.E3t4g.et34g
import com.example.EthanApiPlugin.EthanApiPlugin
import com.example.PacketUtils.PacketUtilsPlugin
import com.example.PathingTesting.PathingTesting
import com.example.PrayerFlicker.EthanPrayerFlickerPlugin
import com.example.RunEnabler.RunEnabler
import com.example.UpkeepPlugin.UpkeepPlugin
import com.example.harpoon2ticker.SwordFish2Tick
import com.jrplugins.AutoChop.AutoChop
import com.jrplugins.autoVorkath.AutoVorkathPlugin
import com.piggyplugins.AutoAerial.AutoAerialPlugin
import com.piggyplugins.AutoJugHumidifier.AutoJugHumidifierPlugin
import com.piggyplugins.AutoRifts.AutoRiftsPlugin
import com.piggyplugins.AutoSmith.AutoSmith
import com.piggyplugins.Firemaking.FiremakingPlugin
import com.piggyplugins.HerbCleaner.HerbCleanerPlugin
import com.piggyplugins.ItemCombiner.ItemCombinerPlugin
import com.piggyplugins.LeftClickBlackJack.LeftClickBlackJackPlugin
import com.piggyplugins.PiggyUtils.PiggyUtilsPlugin
import com.piggyplugins.PowerSkiller.PowerSkillerPlugin
import com.piggyplugins.RooftopAgility.RooftopAgilityPlugin
import com.piggyplugins.SixHourLog.SixHourLogPlugin
import com.piggyplugins.SpeedDartMaker.SpeedDartMakerPlugin
import com.piggyplugins.VardorvisHelper.VardorvisHelperPlugin
import com.piggyplugins.autoLeviathanPrayers.AutoLeviathanPrayer
import com.piggyplugins.autoWhispererPrayers.AutoWhispererPrayer
import com.polyplugins.AutoBoner.AutoBonerPlugin
import com.polyplugins.Butterfly.ButterflyPlugin
import com.polyplugins.Chompy.AutoChompyPlugin
import com.polyplugins.Dialogue.DialogueContinuerPlugin
import com.polyplugins.KittenFeeder.KittenFeederPlugin
import com.polyplugins.Trapper.AutoTrapperPlugin
import net.runelite.client.RuneLite
import net.runelite.client.externalplugins.ExternalPluginManager
import net.runelite.client.plugins.cannon.CannonPlugin
import rsnhider.RsnHiderPlugin

object PluginTester {
    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        ExternalPluginManager.loadBuiltin(
            EthanApiPlugin::class.java,
            PacketUtilsPlugin::class.java,
            AutoAerialPlugin::class.java,
            AutoBonerPlugin::class.java,
            ButterflyPlugin::class.java,
            FiremakingPlugin::class.java,
            DialogueContinuerPlugin::class.java,
            KittenFeederPlugin::class.java,
            AutoChompyPlugin::class.java,
            PowerSkillerPlugin::class.java,
            AutoTrapperPlugin::class.java,
            SpeedDartMakerPlugin::class.java,
            RooftopAgilityPlugin::class.java,
            UpkeepPlugin::class.java,
            AutoChop::class.java,
            PathingTesting::class.java,
            PiggyUtilsPlugin::class.java,
            SixHourLogPlugin::class.java,
            AutoSmith::class.java,
            AutoRiftsPlugin::class.java,
            AutoJugHumidifierPlugin::class.java,
            et34g::class.java,
            SwordFish2Tick::class.java,
            HerbCleanerPlugin::class.java,
            ItemCombinerPlugin::class.java,
            EthanPrayerFlickerPlugin::class.java,
            RunEnabler::class.java,
            AutoVorkathPlugin::class.java,
            RsnHiderPlugin::class.java,
            AutoTitheFarmPlugin::class.java,
            VardorvisHelperPlugin::class.java,
            AutoLeviathanPrayer::class.java,
            AutoWhispererPrayer::class.java,
            CannonPlugin::class.java,
            LeftClickBlackJackPlugin::class.java
        )
        RuneLite.main(args)
    }
}
