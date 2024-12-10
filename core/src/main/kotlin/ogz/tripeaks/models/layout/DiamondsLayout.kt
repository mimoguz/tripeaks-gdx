package ogz.tripeaks.models.layout

/** 27 sockets arranged in three diamonds.  */
class DiamondsLayout : LayoutBase(
    listOf(
        // Row 0
        Socket(0, listOf(), listOf(3, 4), 0, 2, 0),
        Socket(1, listOf(), listOf(5, 6), 0, 8, 0),
        Socket(2, listOf(), listOf(7, 8), 0, 14, 0),

        // Row 1
        Socket(3, listOf(0), listOf(9, 10), 1, 1, 1),
        Socket(4, listOf(0), listOf(10, 11), 1, 3, 1),
        Socket(5, listOf(1), listOf(12, 13), 1, 7, 1),
        Socket(6, listOf(1), listOf(13, 14), 1, 9, 1),
        Socket(7, listOf(2), listOf(15, 16), 1, 13, 1),
        Socket(8, listOf(2), listOf(16, 17), 1, 15, 1),

        // Row 2
        Socket(9, listOf(3, 18), listOf(), 2, 0, 2),
        Socket(10, listOf(3, 4, 18, 19), listOf(), 2, 2, 2),
        Socket(11, listOf(4, 19), listOf(), 2, 4, 2),
        Socket(12, listOf(5, 20), listOf(), 2, 6, 2),
        Socket(13, listOf(5, 6, 20, 21), listOf(), 2, 8, 2),
        Socket(14, listOf(6, 21), listOf(), 2, 10, 2),
        Socket(15, listOf(7, 22), listOf(), 2, 12, 2),
        Socket(16, listOf(7, 8, 22, 23), listOf(), 2, 14, 2),
        Socket(17, listOf(8, 23), listOf(), 2, 16, 2),

        // Row 3
        Socket(18, listOf(24), listOf(9, 10), 3, 1, 1),
        Socket(19, listOf(24), listOf(10, 11), 3, 3, 1),
        Socket(20, listOf(25), listOf(12, 13), 3, 7, 1),
        Socket(21, listOf(25), listOf(13, 14), 3, 9, 1),
        Socket(22, listOf(26), listOf(15, 16), 3, 13, 1),
        Socket(23, listOf(26), listOf(16, 17), 3, 15, 1),

        // Row 4
        Socket(24, listOf(), listOf(18, 19), 4, 2, 0),
        Socket(25, listOf(), listOf(20, 21), 4, 8, 0),
        Socket(26, listOf(), listOf(22, 23), 4, 14, 0),
    )
) {

    override val numberOfColumns: Int get() = COLUMNS

    override val numberOfRows: Int get() = ROWS

    override val tag: String get() = TAG

    companion object {
        private const val COLUMNS = 18
        private const val ROWS = 7
        const val TAG = "diamondsLayout"
    }

}