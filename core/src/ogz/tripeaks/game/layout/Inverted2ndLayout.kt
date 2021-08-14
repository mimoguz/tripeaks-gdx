package ogz.tripeaks.game.layout

import ktx.collections.*
import ogz.tripeaks.game.Socket

/** 30 sockets arranged in peak-valley-peak format.  */
class Inverted2ndLayout : LayoutImpl() {

    override val sockets: GdxArray<Socket> = GdxArray.with(
        // Row 0
        Socket(0, GdxIntArray.with(), GdxIntArray.with(6, 7), 0, 3, 0),
        Socket(1, GdxIntArray.with(8), GdxIntArray.with(), 0, 6, 3),
        Socket(2, GdxIntArray.with(8, 9), GdxIntArray.with(), 0, 8, 3),
        Socket(3, GdxIntArray.with(9, 10), GdxIntArray.with(), 0, 10, 3),
        Socket(4, GdxIntArray.with(10), GdxIntArray.with(), 0, 12, 3),
        Socket(5, GdxIntArray.with(), GdxIntArray.with(11, 12), 0, 15, 0),

        // Row 1
        Socket(6, GdxIntArray.with(0), GdxIntArray.with(13, 14), 1, 2, 1),
        Socket(7, GdxIntArray.with(0), GdxIntArray.with(14, 15), 1, 4, 1),
        Socket(8, GdxIntArray.with(16), GdxIntArray.with(1, 2), 1, 7, 2),
        Socket(9, GdxIntArray.with(16, 17), GdxIntArray.with(2, 3), 1, 9, 2),
        Socket(10, GdxIntArray.with(17), GdxIntArray.with(3, 4), 1, 11, 2),
        Socket(11, GdxIntArray.with(5), GdxIntArray.with(18, 19), 1, 14, 1),
        Socket(12, GdxIntArray.with(5), GdxIntArray.with(19, 20), 1, 16, 1),

        // Row 2
        Socket(13, GdxIntArray.with(6), GdxIntArray.with(21, 22), 2, 1, 2),
        Socket(14, GdxIntArray.with(6, 7), GdxIntArray.with(22, 23), 2, 3, 2),
        Socket(15, GdxIntArray.with(7), GdxIntArray.with(23, 24), 2, 5, 2),
        Socket(16, GdxIntArray.with(25), GdxIntArray.with(8, 9), 2, 8, 1),
        Socket(17, GdxIntArray.with(25), GdxIntArray.with(9, 10), 2, 10, 1),
        Socket(18, GdxIntArray.with(11), GdxIntArray.with(26, 27), 2, 13, 2),
        Socket(19, GdxIntArray.with(11, 12), GdxIntArray.with(27, 28), 2, 15, 2),
        Socket(20, GdxIntArray.with(12), GdxIntArray.with(28, 29), 2, 17, 2),

        // Row 3
        Socket(21, GdxIntArray.with(13), GdxIntArray.with(), 3, 0, 3),
        Socket(22, GdxIntArray.with(13, 14), GdxIntArray.with(), 3, 2, 3),
        Socket(23, GdxIntArray.with(14, 15), GdxIntArray.with(), 3, 4, 3),
        Socket(24, GdxIntArray.with(15), GdxIntArray.with(), 3, 6, 3),
        Socket(25, GdxIntArray.with(), GdxIntArray.with(16, 17), 3, 9, 0),
        Socket(26, GdxIntArray.with(18), GdxIntArray.with(), 3, 12, 3),
        Socket(27, GdxIntArray.with(18, 19), GdxIntArray.with(), 3, 14, 3),
        Socket(28, GdxIntArray.with(19, 20), GdxIntArray.with(), 3, 16, 3),
        Socket(29, GdxIntArray.with(20), GdxIntArray.with(), 3, 18, 3),
    )

    init {
        super.init()
    }

    override val numberOfColumns: Int get() = COLUMNS

    override val numberOfRows: Int get() = ROWS

    override val tag: String get() = TAG

    companion object {
        private const val COLUMNS = 20
        private const val ROWS = 5
        const val TAG = "inverted2ndLayout"
    }
}