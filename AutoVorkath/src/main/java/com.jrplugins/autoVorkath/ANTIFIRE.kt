package com.jrplugins.autoVorkath

import lombok.Getter
import lombok.RequiredArgsConstructor

@Getter
@RequiredArgsConstructor
enum class ANTIFIRE(private val antiFireName: String) {
    EXTENDED_SUPER_ANTIFIRE("Extended super antifire"),
    EXTENDED_SUPER_ANTIFIRE_MIX("Extended super antifire mix"),
    EXTENDED_ANTIFIRE("Extended antifire"),
    EXTENDED_ANTIFIRE_MIX("Extended antifire mix"),
    SUPER_ANTIFIRE_POTION("Super antifire potion"),
    SUPER_ANTIFIRE_MIX("Super antifire mix"),
    ANTIFIRE_POTION("Antifire potion"),
    ANTIFIRE_MIX("Antifire mix");

    override fun toString(): String = antiFireName

}
