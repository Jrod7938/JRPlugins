package com.byronplugins.EthanApiPlugin.Collections;

import com.byronplugins.EthanApiPlugin.Collections.query.ItemQuery;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.RuneLite;
import net.runelite.client.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Inventory {
    static Client client = RuneLite.getInjector().getInstance(Client.class);
    static List<Widget> inventoryItems = new ArrayList<>();

    public static ItemQuery search() {
        return new ItemQuery(inventoryItems);
    }

    public static int getEmptySlots() {
        return 28 - search().result().size();
    }
    public static boolean full(){
        return getEmptySlots()==0;
    }

    public static Widget getInventoryItemByName(String name) {
        return search().nameContainsNoCase(name).first().get();
    }

    public static int getItemAmount(int itemId) {
        return getItemAmount(itemId, false);
    }

    public static int getItemAmount(int itemId, boolean stacked) {
        return stacked ?
                search().withId(itemId).first().map(Widget::getItemQuantity).orElse(0) :
                search().withId(itemId).result().size();
    }

    public static int getItemAmount(String itemName) {
        return getItemAmount(itemName, false);
    }

    public static int getItemAmount(String itemName, boolean stacked) {
        return stacked ?
                search().nameContainsNoCase(itemName).first().map(Widget::getItemQuantity).orElse(0) :
                search().nameContainsNoCase(itemName).result().size();
    }

    public static boolean contains(int id) {
        return contains(id, 1, false);
    }

    public static boolean contains(int id, int amount) {
        return getItemAmount(id, false) >= amount;
    }

    public static boolean contains(int id, int amount, boolean stacked) {
        return getItemAmount(id, stacked) >= amount;
    }

    public static boolean contains(String name) {
        return contains(name, 1, false);
    }

    public static boolean contains(String name, int amount) {
        return getItemAmount(name) >= amount;
    }

    public static boolean contains(String name, int amount, boolean stacked) {
        return getItemAmount(name, stacked) >= amount;
    }

    public static boolean containsAny(int ...ids) {
        for (int id : ids) {
            if (getItemAmount(id) > 0) {
                return true;
            }
        }
        return false;
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged e) {
        client.runScript(6009, 9764864, 28, 1, -1);
        if (e.getContainerId() == 93) {
            Inventory.inventoryItems =
                    Arrays.stream(client.getWidget(WidgetInfo.INVENTORY).getDynamicChildren()).filter(Objects::nonNull).filter(x -> x.getItemId() != 6512 && x.getItemId() != -1).collect(Collectors.toList());
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() == GameState.HOPPING || gameStateChanged.getGameState() == GameState.LOGIN_SCREEN || gameStateChanged.getGameState() == GameState.CONNECTION_LOST) {
            Inventory.inventoryItems.clear();
        }
    }
}
