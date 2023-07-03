package com.byronplugins.EthanApiPlugin.Collections;

import com.byronplugins.Packets.MousePackets;
import com.byronplugins.Packets.TileItemPackets;
import net.runelite.api.TileItem;
import net.runelite.api.coords.WorldPoint;

public class ETileItem {
    public WorldPoint location;
    public TileItem tileItem;

    public ETileItem(WorldPoint worldLocation, TileItem tileItem) {
        this.location = worldLocation;
        this.tileItem = tileItem;
    }

    public WorldPoint getLocation() {
        return location;
    }

    public TileItem getTileItem() {
        return tileItem;
    }

    public void interact(boolean ctrlDown) {
        MousePackets.queueClickPacket();
        TileItemPackets.queueTileItemAction(this, ctrlDown);
    }
}
