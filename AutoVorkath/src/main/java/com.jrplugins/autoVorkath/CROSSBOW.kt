package com.jrplugins.autoVorkath

import lombok.Getter
import lombok.RequiredArgsConstructor

@Getter
@RequiredArgsConstructor
enum class CROSSBOW(private val crossbowName: String) {
    ARMADYL_CROSSBOW("Armadyl crossbow"),
    DRAGON_HUNTER_CROSSBOW("Dragon hunter crossbow"),
    RUNE_CROSSBOW("Rune crossbow"),
    DRAGON_CROSSBOW("Dragon crossbow"),
    TOXIC_BLOWPIPE("Toxic blowpipe");

    override fun toString(): String {
        return crossbowName
    }
}
