package com.piggyplugins;

import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.PacketUtils.PacketUtilsPlugin;
import com.piggyplugins.AutoAerial.AutoAerialPlugin;
import com.piggyplugins.PowerSkiller.PowerSkillerPlugin;
import com.polyplugins.AutoCombat.AutoCombatPlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class PluginTester {
    public static void main(String[] args) throws Exception {
        ExternalPluginManager.loadBuiltin(EthanApiPlugin.class, PacketUtilsPlugin.class,
                AutoCombatPlugin.class, AutoAerialPlugin.class, PowerSkillerPlugin.class);
        RuneLite.main(args);
    }
}