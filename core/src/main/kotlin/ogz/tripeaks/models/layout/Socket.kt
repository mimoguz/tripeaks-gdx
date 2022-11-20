package ogz.tripeaks.models.layout

import ktx.collections.GdxIntArray

data class Socket(
    val index: Int,
    val blocks: GdxIntArray,
    val blockedBy: GdxIntArray,
    val row: Int,
    val column: Int,
    val z: Int
)
