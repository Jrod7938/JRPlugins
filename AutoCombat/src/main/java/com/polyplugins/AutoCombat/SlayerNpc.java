package com.polyplugins.AutoCombat;

import lombok.Getter;

public enum SlayerNpc {
    ROCKSLUG("Rockslug", "Bag of salt", 5),
    DESERT_LIZARD("Desert lizard", "Ice cooler", 5),
    GARGOYLE("Gargoyle", "Rock hammer", 8);

    @Getter
    private final String npcName;
    @Getter
    private final String itemName;
    @Getter
    private final int useHp;

    SlayerNpc(String npcName, String itemName, int useHp) {
        this.npcName = npcName;
        this.itemName = itemName;
        this.useHp = useHp;
    }


}
