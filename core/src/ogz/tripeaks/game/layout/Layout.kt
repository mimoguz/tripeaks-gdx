package ogz.tripeaks.game.layout

import ogz.tripeaks.game.Socket

interface Layout {
    val numberOfSockets: Int
    val numberOfColumns: Int
    val numberOfRows: Int
    operator fun get(index: Int): Socket
    fun lookup(column: Int, row: Int): Socket?
}
