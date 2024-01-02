package com.jrplugins.AutoChop

import lombok.Getter
import lombok.RequiredArgsConstructor
import java.awt.Dimension

@Getter
@RequiredArgsConstructor
enum class TreeAndLocation(
    val treeName: String,
    val treeAction: String,
    val logName: String,
    val treeAreaXY: Dimension,
    val treeAreaWH: Dimension,
    val treeWalkLocation: Dimension,
    val bankWalkLocation: Dimension,
    val bankAreaXY: Dimension,
    val bankAreaWH: Dimension,
) {
    TREE(
        treeName = "Tree",
        treeAction = "Chop down",
        logName = "Logs",
        treeAreaXY = Dimension(3147, 3446),
        treeAreaWH = Dimension(38, 21),
        treeWalkLocation = Dimension(3173, 3451),
        bankWalkLocation = Dimension(3185, 3444),
        bankAreaXY = Dimension(3180, 3442),
        bankAreaWH = Dimension(7, 5),
    ),
    Willow(
        treeName = "Willow tree",
        treeAction = "Chop down",
        logName = "Willow logs",
        treeAreaXY = Dimension(3081, 3223),
        treeAreaWH = Dimension(16, 16),
        treeWalkLocation = Dimension(3087, 3236),
        bankWalkLocation = Dimension(3092, 3245),
        bankAreaXY = Dimension(3089, 3240),
        bankAreaWH = Dimension(6, 6),
    ),
    Maple(
        treeName = "Maple tree",
        treeAction = "Chop down",
        logName = "Maple logs",
        treeAreaXY = Dimension(2702, 3498),
        treeAreaWH = Dimension(31, 16),
        treeWalkLocation = Dimension(2731, 3500),
        bankWalkLocation = Dimension(2727, 3493),
        bankAreaXY = Dimension(2721, 3490),
        bankAreaWH = Dimension(9, 6),
    ),
    YEW(
        treeName = "Yew tree",
        treeAction = "Chop down",
        logName = "Yew logs",
        treeAreaXY = Dimension(2704, 3457),
        treeAreaWH = Dimension(20, 10),
        treeWalkLocation = Dimension(2718, 3462),
        bankWalkLocation = Dimension(2727, 3493),
        bankAreaXY = Dimension(2721, 3490),
        bankAreaWH = Dimension(9, 6),
    );

    fun treeName(): String = treeName
    fun treeAction(): String = treeAction
    fun logName(): String = logName
    fun treeAreaXY(): Dimension = treeAreaXY
    fun treeAreaWH(): Dimension = treeAreaWH
    fun treeWalkLocation(): Dimension = treeWalkLocation
    fun bankWalkLocation(): Dimension = bankWalkLocation
    fun bankAreaXY(): Dimension = bankAreaXY
    fun bankAreaWH(): Dimension = bankAreaWH

    override fun toString(): String = treeName
}