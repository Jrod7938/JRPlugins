package com.jrplugins.autoVorkath

import lombok.Getter
import lombok.RequiredArgsConstructor

@Getter
@RequiredArgsConstructor
enum class TELEPORT(private val teleportName: String) {
    CONSTRUCT_CAPE_T("Construct. cape(t)"),
    CONSTRUCT_CAPE("Construct. cape"),
    HOUSE_TAB("Teleport to house");

    override fun toString(): String {
        return teleportName
    }
}
