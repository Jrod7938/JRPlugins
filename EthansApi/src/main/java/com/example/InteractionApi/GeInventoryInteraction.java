package com.example.InteractionApi;

import com.example.EthanApiPlugin.Collections.GrandExchangeInventory;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import net.runelite.api.widgets.Widget;

import java.util.Optional;
import java.util.function.Predicate;

public class GeInventoryInteraction {

    public static boolean offerItem(String name) {
        return GrandExchangeInventory.search().withName(name).first().flatMap(item ->
        {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(item, "Offer");
            return Optional.of(true);
        }).orElse(false);
    }

    public static boolean offerItem(int id) {
        return GrandExchangeInventory.search().withId(id).first().flatMap(item ->
        {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(item, "Offer");
            return Optional.of(true);
        }).orElse(false);
    }

    public static boolean offerItem(Predicate<? super Widget> predicate) {
        return GrandExchangeInventory.search().filter(predicate).first().flatMap(item ->
        {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(item, "Offer");
            return Optional.of(true);
        }).orElse(false);
    }

    public static boolean offerItemIndex(int index) {
        return GrandExchangeInventory.search().indexIs(index).first().flatMap(item ->
        {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(item, "Offer");
            return Optional.of(true);
        }).orElse(false);
    }

    public static boolean offerItem(Widget item) {
        if (item == null) {
            return false;
        }
        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetAction(item, "Offer");
        return true;
    }
}
