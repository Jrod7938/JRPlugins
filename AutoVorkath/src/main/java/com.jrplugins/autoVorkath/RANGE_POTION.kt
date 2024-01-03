package com.jrplugins.autoVorkath

import lombok.Getter
import lombok.RequiredArgsConstructor

@Getter
@RequiredArgsConstructor
enum class RANGE_POTION(private val potionName: String) {
    DIVINE_RANGING_POTION("anging potion"),
    DIVINE_BASTION_POTION("astion potion");

    override fun toString(): String {
        return potionName
    }
}
