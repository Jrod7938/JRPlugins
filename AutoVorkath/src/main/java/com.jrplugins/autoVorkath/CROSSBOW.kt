package com.jrplugins.autoVorkath

import lombok.Getter
import lombok.RequiredArgsConstructor

@Getter
@RequiredArgsConstructor
enum class CROSSBOW(private val crossbowName: String) {
    DRAGON_HUNTER_CROSSBOW("Dragon hunter crossbow"),
    RUNE_CROSSBOW("Rune crossbow"),
    DRAGON_CROSSBOW("Dragon crossbow");

    override fun toString(): String {
        return crossbowName
    }
}
