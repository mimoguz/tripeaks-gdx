package ogz.tripeaks.game.layout

import ktx.collections.GdxArray
import ktx.collections.GdxIntArray
import ogz.tripeaks.game.Socket

/** 27 sockets arranged in three diamonds.  */
class DiamondsLayout : LayoutImpl() {

    override val sockets: GdxArray<Socket> = GdxArray.with(
        // Row 0
        Socket(0, GdxIntArray.with(), GdxIntArray.with(3, 4), 0, 2, 0),
        Socket(1, GdxIntArray.with(), GdxIntArray.with(5, 6), 0, 8, 0),
        Socket(2, GdxIntArray.with(), GdxIntArray.with(8, 8), 0, 14, 0),

        // Row 1
        Socket(3, GdxIntArray.with(0), GdxIntArray.with(9, 10), 1, 1, 1),
        Socket(4, GdxIntArray.with(0), GdxIntArray.with(10, 11), 1, 3, 1),
        Socket(5, GdxIntArray.with(1), GdxIntArray.with(12, 13), 1, 7, 1),
        Socket(6, GdxIntArray.with(1), GdxIntArray.with(13, 14), 1, 9, 1),
        Socket(7, GdxIntArray.with(2), GdxIntArray.with(15, 16), 1, 13, 1),
        Socket(8, GdxIntArray.with(2), GdxIntArray.with(16, 17), 1, 15, 1),

        // Row 2
        Socket(9, GdxIntArray.with(3, 18), GdxIntArray.with(), 2, 0, 2),
        Socket(10, GdxIntArray.with(3, 4, 18, 19), GdxIntArray.with(), 2, 2, 2),
        Socket(11, GdxIntArray.with(4, 19), GdxIntArray.with(), 2, 4, 2),
        Socket(12, GdxIntArray.with(5, 20), GdxIntArray.with(), 2, 6, 2),
        Socket(13, GdxIntArray.with(5, 6, 20, 21), GdxIntArray.with(), 2, 8, 2),
        Socket(14, GdxIntArray.with(6, 21), GdxIntArray.with(), 2, 10, 2),
        Socket(15, GdxIntArray.with(7, 22), GdxIntArray.with(), 2, 12, 2),
        Socket(16, GdxIntArray.with(7, 8, 22, 23), GdxIntArray.with(), 2, 14, 2),
        Socket(17, GdxIntArray.with(8, 23), GdxIntArray.with(), 2, 16, 2),

        // Row 3
        Socket(18, GdxIntArray.with(24), GdxIntArray.with(9, 10), 3, 1, 1),
        Socket(19, GdxIntArray.with(24), GdxIntArray.with(10, 11), 3, 3, 1),
        Socket(20, GdxIntArray.with(25), GdxIntArray.with(12, 13), 3, 7, 1),
        Socket(21, GdxIntArray.with(25), GdxIntArray.with(13, 14), 3, 9, 1),
        Socket(22, GdxIntArray.with(26), GdxIntArray.with(15, 16), 3, 13, 1),
        Socket(23, GdxIntArray.with(26), GdxIntArray.with(16, 17), 3, 15, 1),

        // Row 4
        Socket(24, GdxIntArray.with(), GdxIntArray.with(18, 19), 4, 2, 0),
        Socket(25, GdxIntArray.with(), GdxIntArray.with(20, 21), 4, 8, 0),
        Socket(26, GdxIntArray.with(), GdxIntArray.with(22, 23), 4, 14, 0),
    )

    init {
        super.init()
    }

    override val numberOfColumns: Int get() = COLUMNS

    override val numberOfRows: Int get() = ROWS

    companion object {
        private const val COLUMNS = 18
        private const val ROWS = 7
        const val TAG = "diamondsLayout"
    }
}