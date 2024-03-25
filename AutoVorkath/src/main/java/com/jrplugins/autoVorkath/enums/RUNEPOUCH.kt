/*
 * Copyright (c) 2024. By Jrod7938
 *
 */
package com.jrplugins.autoVorkath.enums

import lombok.Getter
import lombok.RequiredArgsConstructor

@Getter
@RequiredArgsConstructor
enum class RUNEPOUCH(private val runePouchName: String) {
    RUNE_POUCH("Rune pouch"),
    DIVINE_RUNE_POUCH("Divine rune pouch");

    override fun toString(): String {
        return runePouchName
    }
}