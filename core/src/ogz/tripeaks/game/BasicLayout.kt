package ogz.tripeaks.game

import ktx.collections.*

/** 28 sockets arranged in three peaks, indexed from 0 (top-left) to 27 (bottom-right).  */
class BasicLayout : Layout {

    private val sockets = GdxArray.with(
        Socket(0, GdxIntArray.with(), GdxIntArray.with(3, 4), 0, 3, 0),
        Socket(1, GdxIntArray.with(), GdxIntArray.with(5, 6), 0, 9, 0),
        Socket(2, GdxIntArray.with(), GdxIntArray.with(7, 8), 0, 15, 0),
        Socket(3, GdxIntArray.with(0), GdxIntArray.with(9, 10), 1, 2, 1),
        Socket(4, GdxIntArray.with(0), GdxIntArray.with(10, 11), 1, 4, 1),
        Socket(5, GdxIntArray.with(1), GdxIntArray.with(12, 13), 1, 8, 1),
        Socket(6, GdxIntArray.with(1), GdxIntArray.with(13, 14), 1, 10, 1),
        Socket(7, GdxIntArray.with(2), GdxIntArray.with(15, 16), 1, 14, 1),
        Socket(8, GdxIntArray.with(2), GdxIntArray.with(16, 17), 1, 16, 1),
        Socket(9, GdxIntArray.with(3), GdxIntArray.with(18, 19), 2, 1, 2),
        Socket(10, GdxIntArray.with(3, 4), GdxIntArray.with(19, 20), 2, 3, 2),
        Socket(11, GdxIntArray.with(4), GdxIntArray.with(20, 21), 2, 5, 2),
        Socket(12, GdxIntArray.with(5), GdxIntArray.with(21, 22), 2, 7, 2),
        Socket(13, GdxIntArray.with(5, 6), GdxIntArray.with(22, 23), 2, 9, 2),
        Socket(14, GdxIntArray.with(6), GdxIntArray.with(23, 24), 2, 11, 2),
        Socket(15, GdxIntArray.with(7), GdxIntArray.with(24, 25), 2, 13, 2),
        Socket(16, GdxIntArray.with(7, 8), GdxIntArray.with(25, 26), 2, 15, 2),
        Socket(17, GdxIntArray.with(8), GdxIntArray.with(26, 27), 2, 17, 2),
        Socket(18, GdxIntArray.with(9), GdxIntArray.with(), 3, 0, 3),
        Socket(19, GdxIntArray.with(9, 10), GdxIntArray.with(), 3, 2, 3),
        Socket(20, GdxIntArray.with(10, 11), GdxIntArray.with(), 3, 4, 3),
        Socket(21, GdxIntArray.with(11, 12), GdxIntArray.with(), 3, 6, 3),
        Socket(22, GdxIntArray.with(12, 13), GdxIntArray.with(), 3, 8, 3),
        Socket(23, GdxIntArray.with(13, 14), GdxIntArray.with(), 3, 10, 3),
        Socket(24, GdxIntArray.with(14, 15), GdxIntArray.with(), 3, 12, 3),
        Socket(25, GdxIntArray.with(15, 16), GdxIntArray.with(), 3, 14, 3),
        Socket(26, GdxIntArray.with(16, 17), GdxIntArray.with(), 3, 16, 3),
        Socket(27, GdxIntArray.with(17), GdxIntArray.with(), 3, 18, 3),
    )

    private val cellMap = GdxMap<Int, Socket>()

    private fun getCellIndex(column: Int, row: Int) = row * COLUMNS + column

    init {
        sockets.items.forEach { socket ->
            cellMap[getCellIndex(socket.column, socket.row)] = socket
        }
    }

    override val numberOfSockets: Int get() = sockets.size

    override val numberOfColumns: Int get() = COLUMNS

    override val numberOfRows: Int get() = ROWS

    override operator fun get(index: Int): Socket = sockets[index]

    override fun lookup(column: Int, row: Int): Socket? {
        val index = getCellIndex(column, row)
        return if (cellMap.containsKey(index)) cellMap[index] else null
    }

    companion object {
        private const val COLUMNS = 20
        private const val ROWS = 4
        const val TAG = "basicLayout"
    }
}