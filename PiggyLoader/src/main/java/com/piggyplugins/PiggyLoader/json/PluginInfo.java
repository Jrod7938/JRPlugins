/*

    Thanks to Kotori (https://github.com/OreoCupcakes)
    Modified version of his loader plugin. (https://github.com/OreoCupcakes/kotori-plugins/tree/master/kotoripluginloader/src/main/java/com/theplug/kotori/kotoripluginloader)

 */
package com.piggyplugins.PiggyLoader.json;

import lombok.Getter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PluginInfo {
    @Getter
    private String name;
    @Getter
    private String packageId;
    @Getter
    private String mainClassName;
    @Getter
    private String url;
}