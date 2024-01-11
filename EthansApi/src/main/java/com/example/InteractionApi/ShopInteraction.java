package com.example.InteractionApi;

import com.example.EthanApiPlugin.Collections.Shop;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import net.runelite.api.widgets.Widget;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public class ShopInteraction {

    // mega braindead shop buying, gg - 0Hutch

    public static boolean buyOne(String name) {
        return Shop.search().withName(name).first().flatMap(item ->
        {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(item, "Buy 1");
            return Optional.of(true);
        }).orElse(false);
    }

    public static boolean buyOne(int id) {
        return Shop.search().withId(id).first().flatMap(item ->
        {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(item, "Buy 1");
            return Optional.of(true);
        }).orElse(false);
    }

    public static boolean buyOne(Set<Integer> id) {
        return Shop.search().withSet(id).first().flatMap(item ->
        {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(item, "Buy 1");
            return Optional.of(true);
        }).orElse(false);
    }

    public static boolean buyOne(Predicate<? super Widget> predicate) {
        return Shop.search().filter(predicate).first().flatMap(item ->
        {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(item, "Buy 1");
            return Optional.of(true);
        }).orElse(false);
    }

    public static boolean buyOneIndex(int index) {
        return Shop.search().indexIs(index).first().flatMap(item ->
        {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(item, "Buy 1");
            return Optional.of(true);
        }).orElse(false);
    }

    public static boolean buyOne(Widget item) {
        if (item == null) {
            return false;
        }
        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetAction(item, "Buy 1");
        return true;
    }

    public static boolean buyFive(String name) {
        return Shop.search().withName(name).first().flatMap(item ->
        {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(item, "Buy 5");
            return Optional.of(true);
        }).orElse(false);
    }

    public static boolean buyFive(int id) {
        return Shop.search().withId(id).first().flatMap(item ->
        {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(item, "Buy 5");
            return Optional.of(true);
        }).orElse(false);
    }

    public static boolean buyFive(Set<Integer> id) {
        return Shop.search().withSet(id).first().flatMap(item ->
        {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(item, "Buy 5");
            return Optional.of(true);
        }).orElse(false);
    }

    public static boolean buyFive(Predicate<? super Widget> predicate) {
        return Shop.search().filter(predicate).first().flatMap(item ->
        {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(item, "Buy 5");
            return Optional.of(true);
        }).orElse(false);
    }

    public static boolean buyFiveIndex(int index) {
        return Shop.search().indexIs(index).first().flatMap(item ->
        {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(item, "Buy 5");
            return Optional.of(true);
        }).orElse(false);
    }

    public static boolean buyFive(Widget item) {
        if (item == null) {
            return false;
        }
        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetAction(item, "Buy 5");
        return true;
    }

    public static boolean buyTen(String name) {
        return Shop.search().withName(name).first().flatMap(item ->
        {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(item, "Buy 10");
            return Optional.of(true);
        }).orElse(false);
    }

    public static boolean buyTen(int id) {
        return Shop.search().withId(id).first().flatMap(item ->
        {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(item, "Buy 10");
            return Optional.of(true);
        }).orElse(false);
    }

    public static boolean buyTen(Set<Integer> id) {
        return Shop.search().withSet(id).first().flatMap(item ->
        {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(item, "Buy 10");
            return Optional.of(true);
        }).orElse(false);
    }

    public static boolean buyTen(Predicate<? super Widget> predicate) {
        return Shop.search().filter(predicate).first().flatMap(item ->
        {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(item, "Buy 10");
            return Optional.of(true);
        }).orElse(false);
    }

    public static boolean buyTenIndex(int index) {
        return Shop.search().indexIs(index).first().flatMap(item ->
        {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(item, "Buy 10");
            return Optional.of(true);
        }).orElse(false);
    }

    public static boolean buyTen(Widget item) {
        if (item == null) {
            return false;
        }
        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetAction(item, "Buy 10");
        return true;
    }

    public static boolean buyFifty(String name) {
        return Shop.search().withName(name).first().flatMap(item ->
        {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(item, "Buy 50");
            return Optional.of(true);
        }).orElse(false);
    }

    public static boolean buyFifty(int id) {
        return Shop.search().withId(id).first().flatMap(item ->
        {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(item, "Buy 50");
            return Optional.of(true);
        }).orElse(false);
    }

    public static boolean buyFifty(Set<Integer> id) {
        return Shop.search().withSet(id).first().flatMap(item ->
        {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(item, "Buy 50");
            return Optional.of(true);
        }).orElse(false);
    }

    public static boolean buyFifty(Predicate<? super Widget> predicate) {
        return Shop.search().filter(predicate).first().flatMap(item ->
        {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(item, "Buy 50");
            return Optional.of(true);
        }).orElse(false);
    }

    public static boolean buyFiftyIndex(int index) {
        return Shop.search().indexIs(index).first().flatMap(item ->
        {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(item, "Buy 50");
            return Optional.of(true);
        }).orElse(false);
    }

    public static boolean buyFifty(Widget item) {
        if (item == null) {
            return false;
        }
        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetAction(item, "Buy 50");
        return true;
    }
}
