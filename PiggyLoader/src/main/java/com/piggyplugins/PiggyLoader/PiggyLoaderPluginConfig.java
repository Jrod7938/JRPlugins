/*

    Thanks to Kotori (https://github.com/OreoCupcakes)
    Modified version of his loader plugin. (https://github.com/OreoCupcakes/kotori-plugins/tree/master/kotoripluginloader/src/main/java/com/theplug/kotori/kotoripluginloader)

 */
package com.piggyplugins.PiggyLoader;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("PiggyLoader")
public interface PiggyLoaderPluginConfig extends Config {

    @ConfigItem(
            keyName = "needsEthan",
            name = "Download EthanApiPlugin",
            description = "Download EthanApiPlugin and dependencies"
    )
    default boolean needsEthan() {
        return true;
    }
}
