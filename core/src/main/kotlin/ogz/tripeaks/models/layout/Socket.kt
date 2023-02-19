package ogz.tripeaks.models.layout

import ktx.collections.GdxIntArray

data class Socket(
    val index: Int,
    val blocks: List<Int>,
    val blockedBy: List<Int>,
    val row: Int,
    val column: Int,
    val z: Int
)
