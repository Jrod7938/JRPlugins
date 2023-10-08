package org.example.twotickthreetickteaks;

import com.google.inject.Inject;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class TickChopperOverlay extends OverlayPanel {

    @Inject
    TickChopperOverlay(TickChopper tickChopper) {
        super(tickChopper);
        setPosition(OverlayPosition.BOTTOM_LEFT);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        long timeElapsed = System.currentTimeMillis() - TickChopper.timeBegan;

        panelComponent.getChildren().clear();

        Color titleColor = Color.PINK;
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Piggy Teaks")
                .color(titleColor)
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Time: ")
                .right(formatTime(timeElapsed))
                .build());


        panelComponent.setPreferredSize(new Dimension(200, (int) panelComponent.getPreferredSize().getHeight()));

        return super.render(graphics);
    }

    public static String formatTime(long duration) {
        long hours = TimeUnit.MILLISECONDS.toHours(duration)
                - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration));

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
