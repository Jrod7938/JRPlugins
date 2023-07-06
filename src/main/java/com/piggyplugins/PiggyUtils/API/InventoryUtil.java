package com.piggyplugins.PiggyUtils.API;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.query.ItemQuery;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import net.runelite.api.widgets.Widget;

import java.util.List;
import java.util.Optional;

public class InventoryUtil {
    public static boolean useItemNoCase(String name, String... actions) {
        return nameContainsNoCase(name).first().flatMap(item ->
        {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(item, actions);
            return Optional.of(true);
        }).orElse(false);
    }

    public static ItemQuery nameContainsNoCase(String name) {
        return Inventory.search().filter(widget -> widget.getName().toLowerCase().contains(name.toLowerCase()));
    }

    public static Optional<Widget> getById(int id) {
        return Inventory.search().withId(id).first();
    }

    public static Optional<Widget> getItemNameContains(String name, boolean caseSensitive) {
        if (caseSensitive) {
            return Inventory.search().filter(widget -> widget.getName().contains(name)).first();
        } else {
            return Inventory.search().filter(widget -> widget.getName().toLowerCase().contains(name.toLowerCase())).first();
        }
    }

    public static Optional<Widget> getItemNameContains(String name) {
        return getItemNameContains(name, true);
    }

    public static Optional<Widget> getItem(String name, boolean caseSensitive) {
        if (caseSensitive) {
            return Inventory.search().filter(widget -> widget.getName().equals(name)).first();
        } else {
            return Inventory.search().filter(widget -> widget.getName().toLowerCase().equals(name.toLowerCase())).first();
        }
    }

    public static Optional<Widget> getItem(String name) {
        return getItem(name, true);
    }

    public static int getItemAmount(String name) {
        Optional<Widget> item = getItem(name);
        return item.map(Widget::getItemQuantity).orElse(0);
    }

    public static int getItemAmount(int id) {
        Optional<Widget> item = getById(id);
        return item.map(Widget::getItemQuantity).orElse(0);
    }

    public static boolean hasItem(String name) {
        return getItemAmount(name) > 0;
    }

    public static boolean hasItem(String name, int amount) {
        return getItemAmount(name) >= amount;
    }

    public static boolean hasItems(String ...names) {
        for (String name : names) {
            if (!hasItem(name)) {
                return false;
            }
        }

        return true;
    }

    public static boolean hasItem(int id) {
        return getItemAmount(id) > 0;
    }

    public static List<Widget> getItems() {
        return Inventory.search().result();
    }
}
