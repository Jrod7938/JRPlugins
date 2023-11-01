package com.ozplugins.AutoMLM;

import net.runelite.client.config.*;

@ConfigGroup("AutoMLM")
public interface AutoMLMConfiguration extends Config {
    String version = "v0.1";

    @ConfigItem(
            keyName = "instructions",
            name = "",
            description = "Instructions.",
            position = 1,
            section = "instructionsConfig"
    )
    default String instructions() {
        return "Start at Motherlode mine. Must have hammer in inventory. Set hotkey up and activate plugin with the hotkey.";
    }

    @ConfigSection(
            //keyName = "delayTickConfig",
            name = "Instructions",
            description = "Plugin instructions.",
            position = 2
    )
    String instructionsConfig = "instructionsConfig";

    @ConfigSection(
            name = "Setup",
            description = "Plugin setup.",
            position = 5
    )
    String setupConfig = "setupConfig";

    @ConfigItem(
            keyName = "start/stop hotkey",
            name = "Start/Stop Hotkey",
            description = "Toggle for turning plugin on and off.",
            position = 6,
            section = "setupConfig"
    )
    default Keybind toggle() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "MineArea",
            name = "What area to mine:",
            position = 7,
            section = "setupConfig",
            description = "Input the area where you want to mine at."
    )
    default MineArea MineArea() {
        return MineArea.LOWER_1;
    }

    @ConfigSection(
            name = "Game Tick Configuration",
            description = "Configure how the bot handles game tick delays, 1 game tick equates to roughly 600ms",
            position = 57
    )
    String delayTickConfig = "delayTickConfig";

    @Range(
            min = 0,
            max = 10
    )
    @ConfigItem(
            keyName = "tickDelayMin",
            name = "Game Tick Min",
            description = "",
            position = 58,
            section = "delayTickConfig"
    )
    default int tickDelayMin() {
        return 1;
    }

    @Range(
            min = 0,
            max = 10
    )
    @ConfigItem(
            keyName = "tickDelayMax",
            name = "Game Tick Max",
            description = "",
            position = 59,
            section = "delayTickConfig"
    )
    default int tickDelayMax() {
        return 3;
    }

    @Range(
            min = 0,
            max = 10
    )
    @ConfigItem(
            keyName = "tickDelayTarget",
            name = "Game Tick Target",
            description = "",
            position = 60,
            section = "delayTickConfig"
    )
    default int tickDelayTarget() {
        return 2;
    }

    @Range(
            min = 0,
            max = 10
    )
    @ConfigItem(
            keyName = "tickDelayDeviation",
            name = "Game Tick Deviation",
            description = "",
            position = 61,
            section = "delayTickConfig"
    )
    default int tickDelayDeviation() {
        return 1;
    }

    @ConfigItem(
            keyName = "tickDelayWeightedDistribution",
            name = "Game Tick Weighted Distribution",
            description = "Shifts the random distribution towards the lower end at the target, otherwise it will be an even distribution",
            position = 62,
            section = "delayTickConfig"
    )
    default boolean tickDelayWeightedDistribution() {
        return false;
    }

    @Range(
            min = 0,
            max = 100
    )
    @ConfigItem(
            keyName = "tickDelaySpecMin",
            name = "Spec Tick Delay Min",
            description = "",
            position = 63,
            section = "delayTickConfig"
    )
    default int tickDelaySpecMin() {
        return 20;
    }

    @Range(
            min = 0,
            max = 100
    )
    @ConfigItem(
            keyName = "tickDelaySpecMax",
            name = "Spec Tick Delay Max",
            description = "",
            position = 64,
            section = "delayTickConfig"
    )
    default int tickDelaySpecMax() {
        return 60;
    }

    @ConfigItem(
            keyName = "tickDelaySpecTarget",
            name = "Spec Tick Target",
            description = "",
            position = 65,
            section = "delayTickConfig"
    )
    default int tickDelaySpecTarget() {
        return 40;
    }

    @Range(
            min = 0,
            max = 100
    )
    @ConfigItem(
            keyName = "tickDelaySpecDeviation",
            name = "Spec Tick Deviation",
            description = "",
            position = 66,
            section = "delayTickConfig"
    )
    default int tickDelaySpecDeviation() {
        return 15;
    }

    @ConfigItem(
            keyName = "useSpec",
            name = "Spec?",
            description = "dpick or infernal",
            position = 67,
            section = "delayTickConfig"
    )
    default boolean useSpec() {
        return true;
    }

    @ConfigItem(
            keyName = "fixWheels",
            name = "Fix Wheels?",
            description = "Fix wheels? Hammer in inventory and bank fillers required if so",
            position = 68,
            section = "delayTickConfig"
    )
    default boolean fixWheels() {
        return false;
    }

    @ConfigSection(
            name = "UI Settings",
            description = "UI settings.",
            position = 80
    )
    String UIConfig = "UIConfig";

    @ConfigItem(
            keyName = "UISetting",
            name = "UI Layout: ",
            description = "Choose what UI layout you'd like.",
            position = 81,
            section = "UIConfig",
            hidden = false
    )
    default UISettings UISettings() {
        return UISettings.FULL;
    }

    @ConfigItem(
            keyName = "enableUI",
            name = "Enable UI",
            description = "Enable to turn on in game UI",
            section = "UIConfig",
            position = 140
    )
    default boolean enableUI() {
        return true;
    }

}
