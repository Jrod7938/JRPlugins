package com.polyplugins.Chompy;

import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

import java.util.Arrays;
import java.util.Comparator;

@Getter(AccessLevel.PUBLIC)
public enum Bubbles {
    BUBBLE_1(new WorldPoint(2395, 3045, 0)),
    BUBBLE_2(new WorldPoint(2395, 3046, 0)),
    BUBBLE_3(new WorldPoint(2396, 3047, 0)),
    BUBBLE_4(new WorldPoint(2400, 3044, 0)),
    BUBBLE_5(new WorldPoint(2401, 3043, 0)),
    BUBBLE_6(new WorldPoint(2401, 3042, 0)),
    BUBBLE_7(new WorldPoint(2400, 3041, 0)),
    BUBBLE_8(new WorldPoint(2399, 3041, 0)),
    BUBBLE_9(new WorldPoint(2393, 3051, 0)),
    BUBBLE_10(new WorldPoint(2392, 3053, 0)),
    BUBBLE_11(new WorldPoint(2392, 3054, 0)),
    BUBBLE_12(new WorldPoint(2393, 3055, 0)),
    BUBBLE_13(new WorldPoint(2394, 3055, 0)),
    BUBBLE_14(new WorldPoint(2398, 3052, 0)),
    BUBBLE_15(new WorldPoint(2399, 3051, 0)),
    BUBBLE_16(new WorldPoint(2397, 3049, 0));

    private final WorldPoint location;

    Bubbles(WorldPoint location) {
        this.location = location;
    }

    public static Bubbles getNearestBubble(WorldPoint worldPoint, int radius, Bubbles lastVisitedBubble) {
        return Arrays.stream(values())
                .filter(bubble -> bubble != lastVisitedBubble)
                .filter(bubble -> bubble.getLocation().distanceTo(worldPoint) <= radius)
                .min(Comparator.comparingInt(bubble -> bubble.getLocation().distanceTo(worldPoint)))
                .orElse(null);
    }
}
