package com.jrplugins.autoVorkath

import lombok.Getter
import lombok.RequiredArgsConstructor

@Getter
@RequiredArgsConstructor
enum class PRAYER_POTION(private val potionName: String) {
    PRAYER("Prayer potion"),
    RESTORE("Restore potion"),
    SUPER_RESTORE("Super restore");

    override fun toString(): String {
        return potionName
    }
}