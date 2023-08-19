package com.example.EthanApiPlugin.Collections;

import com.example.EthanApiPlugin.Collections.query.ItemQuery;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.client.RuneLite;
import net.runelite.client.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TradeInventory {

    private static final int TRADE_INVENTORY_PACKED_ID = 22020096;

    static Client client = RuneLite.getInjector().getInstance(Client.class);
    static List<Widget> tradeInventoryItems = new ArrayList<>();

    public static ItemQuery search()
    {
        return new ItemQuery(tradeInventoryItems.stream().filter(Objects::nonNull).collect(Collectors.toList()));
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded e)
    {
        if (e.getGroupId() == 336)
        {
            try
            {
                tradeInventoryItems =
                        Arrays.stream(client.getWidget(TRADE_INVENTORY_PACKED_ID).getDynamicChildren()).filter(Objects::nonNull).filter(x -> x.getItemId() != 6512 && x.getItemId() != -1).collect(Collectors.toList());
            }
            catch (NullPointerException err)
            {
                tradeInventoryItems.clear();
            }
        }
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged e)
    {
        if (client.getWidget(TRADE_INVENTORY_PACKED_ID) == null)
        {
            tradeInventoryItems.clear();
            return;
        }
        try
        {
            tradeInventoryItems =
                    Arrays.stream(client.getWidget(TRADE_INVENTORY_PACKED_ID).getDynamicChildren()).filter(Objects::nonNull).filter(x -> x.getItemId() != 6512 && x.getItemId() != -1).collect(Collectors.toList());
        }
        catch (NullPointerException err)
        {
            tradeInventoryItems.clear();
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged)
    {
        if (gameStateChanged.getGameState() == GameState.HOPPING || gameStateChanged.getGameState() == GameState.LOGIN_SCREEN || gameStateChanged.getGameState() == GameState.CONNECTION_LOST)
        {
            tradeInventoryItems.clear();
        }
    }
}
