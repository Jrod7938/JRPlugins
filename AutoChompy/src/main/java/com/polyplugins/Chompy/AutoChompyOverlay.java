package com.polyplugins.Chompy;

import net.runelite.api.*;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;


public class AutoChompyOverlay extends OverlayPanel {
    private final AutoChompyPlugin plugin;

    @Inject
    private AutoChompyOverlay(AutoChompyPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
        setPosition(OverlayPosition.BOTTOM_LEFT);
        setDragTargetable(true);
        panelComponent.setPreferredSize(new Dimension(160, 160));
        panelComponent.setBorder(new Rectangle(10, 10, 10, 10));

    }

    @Override
    public Dimension render(Graphics2D graphics) {

        // Improved state mapping and display
        String stateText = "STOPPED"; // Default if not started or state is null

        if (plugin.started) {
            switch (plugin.state) {
                case FILL_BELLOWS:
                    stateText = "Filling Bellows";
                    break;
                case DROP_TOAD:
                    stateText = "Dropping Toad";
                    break;
                case KILL_BIRD:
                    stateText = "Killing Chompy";
                    break;
                case STOPPED:
                    stateText = "Stopped";
                    break;
                case INFLATE_TOAD:
                    stateText = "Inflating Toad";
                    break;
                case WAITING:
                    stateText = "Waiting...";
                    break;
            }
        }

        panelComponent.setPreferredSize(new Dimension(200, 320));
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("[PP] Auto Chompy")
                .color(new Color(255, 157, 249))
                .build());
        panelComponent.getChildren().add(TitleComponent.builder()
                .text(plugin.started ? "Running" : "Paused")
                .color(plugin.started ? Color.GREEN : Color.RED)
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Timeout: ")
                .leftColor(new Color(255, 157, 249))
                .right(String.valueOf(plugin.timeout))
                .rightColor(Color.WHITE)
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("State: ")
                .leftColor(new Color(255, 157, 249))
                .right(stateText)
                .rightColor(Color.WHITE)
                .build());

        return super.render(graphics);
    }


}
