package com.example.EthanApiPlugin.Collections;

import com.example.EthanApiPlugin.Collections.query.ItemQuery;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.RuneLite;
import net.runelite.client.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GrandExchangeInventory 
{

    static Client client = RuneLite.getInjector().getInstance(Client.class);
    static List<Widget> geInventoryItems = new ArrayList<>();

    public static ItemQuery search()
    {
        return new ItemQuery(geInventoryItems.stream().filter(Objects::nonNull).collect(Collectors.toList()));
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded e)
    {
        if (e.getGroupId() == WidgetID.GRAND_EXCHANGE_INVENTORY_GROUP_ID)
        {
            try
            {
                geInventoryItems =
                        Arrays.stream(client.getWidget(WidgetInfo.GRAND_EXCHANGE_INVENTORY_ITEMS_CONTAINER).getDynamicChildren()).filter(Objects::nonNull).filter(x -> x.getItemId() != 6512 && x.getItemId() != -1).collect(Collectors.toList());

                for (Widget w : geInventoryItems) {
                    System.out.println(w.getName());
                }
            }
            catch (NullPointerException err)
            {
                geInventoryItems.clear();
            }
        }
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged e)
    {
        if (e.getContainerId() == 518)
        {
            if (client.getWidget(WidgetInfo.GRAND_EXCHANGE_INVENTORY_ITEMS_CONTAINER) == null)
            {
                geInventoryItems.clear();
                return;
            }
            try
            {
                geInventoryItems =
                        Arrays.stream(client.getWidget(WidgetInfo.GRAND_EXCHANGE_INVENTORY_ITEMS_CONTAINER).getDynamicChildren()).filter(Objects::nonNull).filter(x -> x.getItemId() != 6512 && x.getItemId() != -1).collect(Collectors.toList());
                return;
            }
            catch (NullPointerException err)
            {
                geInventoryItems.clear();
                return;
            }
        }

    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged)
    {
        if (gameStateChanged.getGameState() == GameState.HOPPING || gameStateChanged.getGameState() == GameState.LOGIN_SCREEN || gameStateChanged.getGameState() == GameState.CONNECTION_LOST)
        {
            geInventoryItems.clear();
        }
    }
}
