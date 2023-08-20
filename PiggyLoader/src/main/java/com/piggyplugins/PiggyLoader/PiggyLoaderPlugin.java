/*

    Thanks to Kotori (https://github.com/OreoCupcakes)
    Modified version of his loader plugin. (https://github.com/OreoCupcakes/kotori-plugins/tree/master/kotoripluginloader/src/main/java/com/theplug/kotori/kotoripluginloader)

 */
package com.piggyplugins.PiggyLoader;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.piggyplugins.PiggyLoader.json.Info;
import com.piggyplugins.PiggyLoader.json.PluginInfo;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.RuneLiteProperties;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.events.ExternalPluginsChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginInstantiationException;
import net.runelite.client.plugins.PluginManager;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@PluginDescriptor(
        name = "<html><font color=\"#FF9DF9\">[PP]</font> Piggy Loader</html>",
        description = "Loads Piggy Plugins remotely",
        enabledByDefault = false
)
public class PiggyLoaderPlugin extends Plugin {
    private static final String ETHAN_PACKAGE = "com.example";
    private static final String PIGGY_PACKAGE = "com.piggyplugins";
    private final String pluginsJsonURL = "https://github.com/0Hutch/PiggyPluginsRelease/blob/main/plugins.json?raw=true";
    private final String infoJsonURL = "https://github.com/0Hutch/PiggyPluginsRelease/blob/main/info.json?raw=true";
    private final String currentLoaderVersion = "1.0.1";

    @Inject
    private Client client;
    @Inject
    private PiggyLoaderPluginConfig config;
    @Inject
    private PluginManager manager;
    @Inject
    private EventBus eventBus;

    private Gson gson;
    private Info infoJsonObject;
    private PluginInfo[] pluginInfoJsonObject;
    private ArrayList<String> pluginInfoList;
    private ArrayList<String> loadedPluginClassPaths;
    private List<Plugin> installedPlugins;
    private URLClassLoader classLoader;

    @Provides
    private PiggyLoaderPluginConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(PiggyLoaderPluginConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        gson = new Gson();
        pluginInfoList = new ArrayList<>();
        loadedPluginClassPaths = new ArrayList<>();
        installedPlugins = new ArrayList<>();
        log.info(RuneLiteProperties.getVersion());
        init();
    }

    @Override
    protected void shutDown() throws Exception {
        uninstallPlugins();
        gson = null;
        infoJsonObject = null;
        pluginInfoJsonObject = null;
        pluginInfoList = null;
        loadedPluginClassPaths = null;
        installedPlugins = null;
        classLoader = null;
    }

    private void stop() {
        try {
            manager.stopPlugin(this);
            manager.setPluginEnabled(this, false);
        } catch (PluginInstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    private void init() {
        new Thread(() ->
        {
            parseJsonFile(infoJsonURL, infoJsonObject);
            parseJsonFile(pluginsJsonURL, pluginInfoJsonObject);
            parsePluginInfo();
            createClassLoader();
            if (!outdatedLoaderVersion()) {
                loadPlugins(buildPluginsToLoadList());
            } else {
                stop();
            }
        }).start();
    }

    private void parseJsonFile(String url, Object objectToSet) {
        if (objectToSet != null) {
            return;
        }

        BufferedReader reader = null;

        for (int i = 1; i < 6; i++) {
            try {
                URL urlJsonFile = new URL(url);
                reader = new BufferedReader(new InputStreamReader(urlJsonFile.openStream()));
                break;
            } catch (Exception e) {
                log.error("Attempt #" + i + ". Unable to download JSON file from URL. Retrying...", e);
            }
        }

        if (reader == null) {
            stop();
            return;
        }

        try {
            switch (url) {
                case pluginsJsonURL:
                    pluginInfoJsonObject = gson.fromJson(reader, PluginInfo[].class);
                    break;
                case infoJsonURL:
                    infoJsonObject = gson.fromJson(reader, Info.class);
                    break;
            }
        } catch (Exception e) {
            log.error("Unable to convert JSON file into a Java object.", e);
            stop();
        }

        try {
            reader.close();
        } catch (Exception e) {
            log.error("Unable to close BufferedReader stream.", e);
            stop();
        }
    }

    private void parsePluginInfo() {
        if (pluginInfoJsonObject == null || !pluginInfoList.isEmpty()) {
            stop();
            return;
        }

        for (PluginInfo plugin : pluginInfoJsonObject) {
            //0 = name, 1 = package, 2 = class, 3 = url
            pluginInfoList.add(plugin.getName());
            pluginInfoList.add(plugin.getPackageId());
            pluginInfoList.add(plugin.getMainClassName());
            pluginInfoList.add(plugin.getUrl());
        }
    }

    private void createClassLoader() {
        if (classLoader != null || pluginInfoList == null) {
            stop();
            return;
        }

        try {
            ArrayList<URL> pluginUrlsList = new ArrayList<>();
            for (int i = 3; i < pluginInfoList.size(); i += 4) {
                URL pluginUrl = new URL(pluginInfoList.get(i));
                if (!pluginUrlsList.contains(pluginUrl)) {
                    pluginUrlsList.add(pluginUrl);
                }
            }
            classLoader = new URLClassLoader(pluginUrlsList.toArray(URL[]::new), client.getClass().getClassLoader());
        } catch (Exception e) {
            log.error("Unable to create plugin ClassLoader.", e);
            stop();
        }
    }

    private String getPluginClassPath(String pluginName, String packageName) {
        int pluginNameIndex = pluginInfoList.indexOf(pluginName);
        if (pluginNameIndex == -1) {
            return null;
        }
        int pluginPackageNameIndex = pluginNameIndex + 1;
        int pluginMainClassNameIndex = pluginNameIndex + 2;

        String pluginPackageName = pluginInfoList.get(pluginPackageNameIndex);
        String pluginMainClassName = pluginInfoList.get(pluginMainClassNameIndex);
        return packageName + "." + pluginPackageName + "." + pluginMainClassName;
    }

    private void loadPlugins(ArrayList<String> pluginClassPaths) {
        if (pluginClassPaths == null || pluginClassPaths.isEmpty()) {
            stop();
            return;
        }

        try {
            ArrayList<Class<?>> classesToLoad = new ArrayList<>();
            for (String classPath : pluginClassPaths) {
                if (!loadedPluginClassPaths.contains(classPath)) {
                    classesToLoad.add(classLoader.loadClass(classPath));
                    loadedPluginClassPaths.add(classPath);
                }
            }

            List<Plugin> scannedPlugins = manager.loadPlugins(classesToLoad, null);

            //Mark down installed plugins in reverse order (so you can uninstall plugins later on)
            for (int i = scannedPlugins.size() - 1; i >= 0; i--) {
                if (!installedPlugins.contains(scannedPlugins.get(i))) {
                    installedPlugins.add(scannedPlugins.get(i));
                }
            }

            SwingUtilities.invokeLater(() ->
            {
                for (Plugin p : scannedPlugins) {
                    if (p == null) {
                        continue;
                    }

                    try {
                        manager.startPlugin(p);
                        if (p.getName().contains("EthanApiPlugin") || p.getName().contains("PacketUtils") || p.getName().contains("PiggyUtils")) {
                            manager.setPluginEnabled(p, true);
                        }
                    } catch (Exception e) {
                        log.error("Unable to start plugin: " + p.getName(), e);
                    }
                }
            });
            eventBus.post(new ExternalPluginsChanged());
        } catch (Exception e) {
            stop();
            log.error("Unable to load the plugins.", e);
        }
    }

    private void uninstallPlugins() {
        if (installedPlugins.isEmpty()) {
            return;
        }

        for (Plugin p : installedPlugins) {
            try {
                manager.stopPlugin(p);
                manager.remove(p);
            } catch (Exception e) {
                log.error("Unable to stop and uninstall plugin: " + p.getName());
            }
        }

        eventBus.post(new ExternalPluginsChanged());
    }

    //Load Plugins
    private ArrayList<String> buildPluginsToLoadList() {
        if (infoJsonObject == null) {
            stop();
            return null;
        }

        if (outdatedGameRevision()) {
            stop();
            return null;
        }

        if (outdatedRuneliteVersion()) {
            stop();
            return null;
        }

        ArrayList<String> pluginClassPathsToLoad = new ArrayList<>();

        if (config.needsEthan()) {
            pluginClassPathsToLoad.add("com.example.PacketUtils.PacketUtilsPlugin");
            pluginClassPathsToLoad.add("com.example.EthanApiPlugin.EthanApiPlugin");
        }

        pluginClassPathsToLoad.add(getPluginClassPath("PiggyUtils", PIGGY_PACKAGE));
        pluginClassPathsToLoad.add(getPluginClassPath("AoeWarn", PIGGY_PACKAGE));
        pluginClassPathsToLoad.add(getPluginClassPath("AutoJugHumidifier", PIGGY_PACKAGE));
        pluginClassPathsToLoad.add(getPluginClassPath("AutoRifts", PIGGY_PACKAGE));
        pluginClassPathsToLoad.add(getPluginClassPath("ChinBreakHandler", PIGGY_PACKAGE));
        pluginClassPathsToLoad.add(getPluginClassPath("HerbCleaner", PIGGY_PACKAGE));
        pluginClassPathsToLoad.add(getPluginClassPath("ItemCombiner", PIGGY_PACKAGE));
        pluginClassPathsToLoad.add(getPluginClassPath("ItemDropper", PIGGY_PACKAGE));
        pluginClassPathsToLoad.add(getPluginClassPath("JadAutoPrayers", PIGGY_PACKAGE));
        pluginClassPathsToLoad.add(getPluginClassPath("OneTickSwitcher", PIGGY_PACKAGE));
        pluginClassPathsToLoad.add(getPluginClassPath("PowerSkiller", PIGGY_PACKAGE));
        pluginClassPathsToLoad.add(getPluginClassPath("PrayAgainstPlayer", PIGGY_PACKAGE));
        pluginClassPathsToLoad.add(getPluginClassPath("RooftopAgility", PIGGY_PACKAGE));
        pluginClassPathsToLoad.add(getPluginClassPath("ShiftClickWalker", PIGGY_PACKAGE));
        pluginClassPathsToLoad.add(getPluginClassPath("SpeedDartMaker", PIGGY_PACKAGE));
        pluginClassPathsToLoad.add(getPluginClassPath("VardorvisHelper", PIGGY_PACKAGE));
        pluginClassPathsToLoad.add(getPluginClassPath("AutoTele", ETHAN_PACKAGE));
        pluginClassPathsToLoad.add(getPluginClassPath("CalvarionHelper", ETHAN_PACKAGE));
        pluginClassPathsToLoad.add(getPluginClassPath("gauntletFlicker", ETHAN_PACKAGE));
        pluginClassPathsToLoad.add(getPluginClassPath("ThreeTickFourGranite", ETHAN_PACKAGE));
        pluginClassPathsToLoad.add(getPluginClassPath("harpoon2ticker", ETHAN_PACKAGE));
        pluginClassPathsToLoad.add(getPluginClassPath("NightmareHelper", ETHAN_PACKAGE));
        pluginClassPathsToLoad.add(getPluginClassPath("EthanPrayerFlicker", ETHAN_PACKAGE));
        pluginClassPathsToLoad.add(getPluginClassPath("LavaRunecrafter", ETHAN_PACKAGE));
        pluginClassPathsToLoad.add(getPluginClassPath("RunEnabler", ETHAN_PACKAGE));
        pluginClassPathsToLoad.add(getPluginClassPath("SuperGlassMaker", ETHAN_PACKAGE));
        pluginClassPathsToLoad.add(getPluginClassPath("UpkeepPlugin", ETHAN_PACKAGE));

        return pluginClassPathsToLoad;
    }

    //Version Check Functions
    private boolean outdatedGameRevision() {
        if (infoJsonObject == null) {
            return true;
        }
        return client.getRevision() != infoJsonObject.getGameRevision();
    }

    private boolean outdatedRuneliteVersion() {
        if (infoJsonObject == null) {
            return true;
        }

        return false;
    }

    private boolean outdatedLoaderVersion() {
        if (infoJsonObject == null) {
            return true;
        }
        String[] internalVersionNumber = currentLoaderVersion.split("\\.");
        String[] infoJsonVersionNumber = infoJsonObject.getLoaderVersion().split("\\.");

        //Check local major version number, logic is if local major is less, then it's an older version.
        if (Integer.parseInt(internalVersionNumber[0]) > Integer.parseInt(infoJsonVersionNumber[0])) {
            return false;
        } else if (Integer.parseInt(internalVersionNumber[0]) < Integer.parseInt(infoJsonVersionNumber[0])) {
            return true;
        }

        //Check local minor version number, logic is if local minor is less, then it's an older version.
        if (Integer.parseInt(internalVersionNumber[1]) > Integer.parseInt(infoJsonVersionNumber[1])) {
            return false;
        } else if (Integer.parseInt(internalVersionNumber[1]) < Integer.parseInt(infoJsonVersionNumber[1])) {
            return true;
        }

        //Check local patch version number, logic is if local patch is less, then it is an older version. If equal or greater, then not older version.
        return Integer.parseInt(internalVersionNumber[2]) < Integer.parseInt(infoJsonVersionNumber[2]);
    }
}
