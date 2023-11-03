package com.piggyplugins;

import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.PacketUtils.PacketUtilsPlugin;
import com.piggyplugins.AutoAerial.AutoAerialPlugin;
import com.polyplugins.AutoBoner.AutoBonerPlugin;
import com.polyplugins.AutoCombat.AutoCombatPlugin;
import com.polyplugins.AutoSulphur.AutoSulphurPlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class PluginTester {
    public static void main(String[] args) throws Exception {
        ExternalPluginManager.loadBuiltin(EthanApiPlugin.class, PacketUtilsPlugin.class,
                AutoCombatPlugin.class, AutoAerialPlugin.class);
        RuneLite.main(args);
    }
}