package ogz.tripeaks.models.layout

/** 28 sockets arranged in three peaks, indexed from 0 (top-left) to 27 (bottom-right).  */
class BasicLayout : LayoutBase(
    listOf(
        // Row 0
        Socket(0, listOf(), listOf(3, 4), 0, 3, 0),
        Socket(1, listOf(), listOf(5, 6), 0, 9, 0),
        Socket(2, listOf(), listOf(7, 8), 0, 15, 0),

        // Row 1
        Socket(3, listOf(0), listOf(9, 10), 1, 2, 1),
        Socket(4, listOf(0), listOf(10, 11), 1, 4, 1),
        Socket(5, listOf(1), listOf(12, 13), 1, 8, 1),
        Socket(6, listOf(1), listOf(13, 14), 1, 10, 1),
        Socket(7, listOf(2), listOf(15, 16), 1, 14, 1),
        Socket(8, listOf(2), listOf(16, 17), 1, 16, 1),

        // Row 2
        Socket(9, listOf(3), listOf(18, 19), 2, 1, 2),
        Socket(10, listOf(3, 4), listOf(19, 20), 2, 3, 2),
        Socket(11, listOf(4), listOf(20, 21), 2, 5, 2),
        Socket(12, listOf(5), listOf(21, 22), 2, 7, 2),
        Socket(13, listOf(5, 6), listOf(22, 23), 2, 9, 2),
        Socket(14, listOf(6), listOf(23, 24), 2, 11, 2),
        Socket(15, listOf(7), listOf(24, 25), 2, 13, 2),
        Socket(16, listOf(7, 8), listOf(25, 26), 2, 15, 2),
        Socket(17, listOf(8), listOf(26, 27), 2, 17, 2),

        // Row 3
        Socket(18, listOf(9), listOf(), 3, 0, 3),
        Socket(19, listOf(9, 10), listOf(), 3, 2, 3),
        Socket(20, listOf(10, 11), listOf(), 3, 4, 3),
        Socket(21, listOf(11, 12), listOf(), 3, 6, 3),
        Socket(22, listOf(12, 13), listOf(), 3, 8, 3),
        Socket(23, listOf(13, 14), listOf(), 3, 10, 3),
        Socket(24, listOf(14, 15), listOf(), 3, 12, 3),
        Socket(25, listOf(15, 16), listOf(), 3, 14, 3),
        Socket(26, listOf(16, 17), listOf(), 3, 16, 3),
        Socket(27, listOf(17), listOf(), 3, 18, 3),
    )
) {

    override val numberOfColumns: Int get() = COLUMNS

    override val numberOfRows: Int get() = ROWS

    override val tag: String get() = TAG

    companion object {
        private const val COLUMNS = 20
        private const val ROWS = 5
        const val TAG = "basicLayout"
    }

}