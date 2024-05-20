package com.example.AutoTitheFarm;

import lombok.AccessLevel;
import lombok.Getter;

import static com.example.AutoTitheFarm.AutoTitheFarmPlugin.*;

public enum Plants {
    GOLOVANOVA(34, 53, 27384, 27387, 27390, 27393),

    BOLOGANO(54, 73, 27395, 27398, 27401, 27404),

    LOGAVANO(74, 99, 27406, 27409, 27412, 27415);

    private final int minLevelRequirement;

    private final int maxLevelRequirement;

    // unwatered IDs
    @Getter(AccessLevel.PACKAGE)
    private final int firstStageId;

    @Getter(AccessLevel.PACKAGE)
    private final int secondStageId;

    @Getter(AccessLevel.PACKAGE)
    private final int thirdStageId;

    @Getter(AccessLevel.PACKAGE)
    private final int fourthStageId;

    Plants(int minLevelRequirement, int maxLevelRequirement, int firstStageId, int secondStageId, int thirdStageId, int fourthStageId) {
        this.minLevelRequirement = minLevelRequirement;
        this.maxLevelRequirement = maxLevelRequirement;
        this.firstStageId = firstStageId;
        this.secondStageId = secondStageId;
        this.thirdStageId = thirdStageId;
        this.fourthStageId = fourthStageId;
    }

    private boolean farmingLevelIsInRange() {
        return getFarmingLevel() >= minLevelRequirement && getFarmingLevel() <= maxLevelRequirement;
    }

    private Plants getPlant() {
        return farmingLevelIsInRange() ? this : null;
    }

    public static Plants getNeededPlant() {
        Plants neededPlant = null;
        for (Plants plant : Plants.values()) {
            if (plant.getPlant() == null) {
                continue;
            }
            neededPlant = plant.getPlant();
        }
        return neededPlant;
    }
}