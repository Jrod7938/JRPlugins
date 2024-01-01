package com.piggyplugins.CannonReloader;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("CannonReloader")
public interface CannonReloaderConfig extends Config {
//    @ConfigItem(
//            keyName = "cannonInstructions",
//            name = "",
//            description = "Cannon Instructions.",
//            position = 40,
//            section = "cannonConfig"
//    )
//    default String cannonConfigInstructions() {
//        return "Right click the cannon and the safe spot to set your desired locations.";
//    }

    @ConfigItem(
            keyName = "UseSafespot",
            name = "Use Safespot?",
            description = "Should safespot be used?",
            position = 43,
            section = "enemyNPCConfig"
    )
    default boolean useSafespot() {
        return false;
    }

//    @ConfigItem(
//            keyName = "SafespotCoords",
//            name = "Safespot X,Y Coords",
//            description = "Set your safespot coords X,Y. Example: 3024,1028",
//            position = 46,
//            section = "cannonConfig"
//    )
//    default String SafespotCoords() {
//        return "1234,1234";
//    }
//
//    @ConfigItem(
//            keyName = "CannonCoords",
//            name = "Cannon X,Y Coords",
//            description = "Set your cannon coords X,Y. Example: 3024,1028",
//            position = 100,
//            section = "cannonConfig"
//    )
//    default String CannonCoords() {
//        return "1234,1234";
//    }

    @Range(
            min = 1,
            max = 30
    )
    @ConfigItem(
            keyName = "CannonLowAmount",
            name = "Reload cannon at:",
            position = 104,
            section = "cannonConfig",
            description = "Will reload cannon at set amount."
    )
    default int cannonLowAmount()
    {
        return 5;
    }

    @ConfigSection(
            name = "Tile Configuration",
            description = "ging stuff for tiles section.",
            closedByDefault = true,
            position = 166
    )
    String tileConfig = "tileConfig";

    @ConfigItem(
            keyName = "safespotTile",
            name = "safespot tile colour",
            position = 169,
            description = "",
            section = "tileConfig"
    )
    default Color safespotTile()
    {
        return Color.GREEN;
    }

    @Alpha
    @ConfigItem(
            keyName = "safespotTileFill",
            name = "safespotTile fill colour",
            position = 170,
            description = "",
            section = "tileConfig"
    )
    default Color safespotTileFill()
    {
        return new Color(0, 0, 0, 50);
    }

    @ConfigItem(
            keyName = "cannonspotFillColour",
            name = "cannonSpotTile tile colour",
            position = 173,
            description = "",
            section = "tileConfig"
    )
    default Color cannonSpotTile()
    {
        return Color.YELLOW;
    }
    @Alpha
    @ConfigItem(
            keyName = "cannonSpotTileFill",
            name = "cannonSpotTileFill fill colour",
            position = 174,
            description = "",
            section = "tileConfig"
    )
    default Color cannonSpotTileFill()
    {
        return new Color(0, 0, 0, 50);
    }
}
