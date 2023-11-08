package com.piggyplugins.PowerSkiller;


import com.example.EthanApiPlugin.Collections.TileObjects;
import com.google.common.base.Strings;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.awt.*;
import java.util.Optional;

public class PowerSkillerOverlay extends Overlay {
    private final PanelComponent panelComponent = new PanelComponent();
    private final Client client;
    private final PowerSkillerPlugin plugin;

    @Inject
    private PowerSkillerOverlay(Client client, PowerSkillerPlugin plugin) {
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);

    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().clear();
        if (plugin.getObjectWMostPlayers() != null) {
            renderTile(graphics, LocalPoint.fromWorld(client, plugin.getObjectWMostPlayers()), Color.RED, Color.RED, "MOST");
        }

//        LineComponent timeout = buildLine("Timeout: ", String.valueOf(plugin.timeout));
//        panelComponent.getChildren().add(timeout);

        return null;
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

    private void renderTile(final Graphics2D graphics, final LocalPoint dest, final Color color, final Color fillColor, @Nullable String label) {
        if (dest == null) {
            return;
        }
        final Polygon poly = Perspective.getCanvasTilePoly(client, dest);
        if (poly == null) {
            return;
        }
        OverlayUtil.renderPolygon(graphics, poly, color, fillColor, new BasicStroke((float) 1.5));
        if (!Strings.isNullOrEmpty(label)) {
            Point canvasTextLocation = Perspective.getCanvasTextLocation(client, graphics, dest, label, 0);
            if (canvasTextLocation != null) {
                OverlayUtil.renderTextLocation(graphics, canvasTextLocation, label, color);
            }
        }
    }

}
