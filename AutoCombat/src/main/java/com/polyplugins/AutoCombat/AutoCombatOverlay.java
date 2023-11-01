package com.polyplugins.AutoCombat;


import com.example.EthanApiPlugin.Collections.TileObjects;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.inject.Inject;
import javax.sound.sampled.Line;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Optional;

public class AutoCombatOverlay extends Overlay {

    private final PanelComponent panelComponent = new PanelComponent();
    private final Client client;
    private final AutoCombatPlugin plugin;

    @Inject
    private AutoCombatOverlay(Client client, AutoCombatPlugin plugin) {
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setDragTargetable(true);

    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().clear();

        LineComponent started = buildLine("Started: ", String.valueOf(plugin.started));
        LineComponent timeout = buildLine("Timeout: ", String.valueOf(plugin.timeout));
        LineComponent lootTrigger = buildLine("Loot Trigger: ", String.valueOf(plugin.lootTrigger));
        LineComponent lootQ = buildLine("Loot Q: ", String.valueOf(plugin.lootQueue.size()));

        panelComponent.getChildren().add(started);
        panelComponent.getChildren().add(timeout);
        panelComponent.getChildren().add(lootTrigger);
        panelComponent.getChildren().add(lootQ);

        return panelComponent.render(graphics);
    }

    /**
     * Builds a line component with the given left and right text
     *
     * @param left
     * @param right
     * @return Returns a built line component with White left text and Yellow right text
     */
    private LineComponent buildLine(String left, String right) {
        return LineComponent.builder()
                .left(left)
                .right(right)
                .leftColor(Color.WHITE)
                .rightColor(Color.YELLOW)
                .build();
    }
}
