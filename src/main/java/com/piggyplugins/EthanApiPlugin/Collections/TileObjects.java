package com.piggyplugins.EthanApiPlugin.Collections;

import com.piggyplugins.EthanApiPlugin.Collections.query.TileObjectQuery;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.client.RuneLite;
import net.runelite.client.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class TileObjects {
    static Client client = RuneLite.getInjector().getInstance(Client.class);
    static List<TileObject> tileObjects = new ArrayList<>();

    public static TileObjectQuery search() {
        return new TileObjectQuery(tileObjects);
    }

    public static boolean isObjectInRange(String name) {
        return search().nameContainsNoCase(name).nearestToPlayer().isPresent();
    }

    public static TileObject getNearestObjectToPlayer(String name) {
        return search().nameContainsNoCase(name).nearestToPlayer().get();
    }

    @Subscribe(priority = 10000)
    public void onGameTick(GameTick e) {
        tileObjects.clear();
        TileItems.tileItems.clear();
        for (Tile[] tiles : client.getScene().getTiles()[client.getPlane()]) {
            if (tiles == null) {
                continue;
            }
            for (Tile tile : tiles) {
                if (tile == null) {
                    continue;
                }
                if (tile.getGroundItems() != null) {
                    for (TileItem groundItem : tile.getGroundItems()) {
                        if (groundItem == null) {
                            continue;
                        }
                        TileItems.tileItems.add(new ETileItem(tile.getWorldLocation(), groundItem));
                    }
                }
                for (GameObject gameObject : tile.getGameObjects()) {
                    if (gameObject == null) {
                        continue;
                    }
                    if (gameObject.getId() == -1) {
                        continue;
                    }
                    tileObjects.add(gameObject);
                }
                if (tile.getGroundObject() != null) {
                    if (tile.getGroundObject().getId() == -1) {
                        continue;
                    }
                    tileObjects.add(tile.getGroundObject());
                }
                if (tile.getWallObject() != null) {
                    if (tile.getWallObject().getId() == -1) {
                        continue;
                    }
                    tileObjects.add(tile.getWallObject());
                }
                if (tile.getDecorativeObject() != null) {
                    if (tile.getDecorativeObject().getId() == -1) {
                        continue;
                    }
                    tileObjects.add(tile.getDecorativeObject());
                }
            }
        }
    }
}
