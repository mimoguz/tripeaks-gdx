package ogz.tripeaks.models.layout

/** 27 sockets arranged in three diamonds.  */
class Inverted2ndLayout : LayoutBase(
    listOf(
        // Row 0
        Socket(0, listOf(), listOf(6, 7), 0, 3, 0),
        Socket(1, listOf(8), listOf(), 0, 6, 3),
        Socket(2, listOf(8, 9), listOf(), 0, 8, 3),
        Socket(3, listOf(9, 10), listOf(), 0, 10, 3),
        Socket(4, listOf(10), listOf(), 0, 12, 3),
        Socket(5, listOf(), listOf(11, 12), 0, 15, 0),

        // Row 1
        Socket(6, listOf(0), listOf(13, 14), 1, 2, 1),
        Socket(7, listOf(0), listOf(14, 15), 1, 4, 1),
        Socket(8, listOf(16), listOf(1, 2), 1, 7, 2),
        Socket(9, listOf(16, 17), listOf(2, 3), 1, 9, 2),
        Socket(10, listOf(17), listOf(3, 4), 1, 11, 2),
        Socket(11, listOf(5), listOf(18, 19), 1, 14, 1),
        Socket(12, listOf(5), listOf(19, 20), 1, 16, 1),

        // Row 2
        Socket(13, listOf(6), listOf(21, 22), 2, 1, 2),
        Socket(14, listOf(6, 7), listOf(22, 23), 2, 3, 2),
        Socket(15, listOf(7), listOf(23, 24), 2, 5, 2),
        Socket(16, listOf(25), listOf(8, 9), 2, 8, 1),
        Socket(17, listOf(25), listOf(9, 10), 2, 10, 1),
        Socket(18, listOf(11), listOf(26, 27), 2, 13, 2),
        Socket(19, listOf(11, 12), listOf(27, 28), 2, 15, 2),
        Socket(20, listOf(12), listOf(28, 29), 2, 17, 2),

        // Row 3
        Socket(21, listOf(13), listOf(), 3, 0, 3),
        Socket(22, listOf(13, 14), listOf(), 3, 2, 3),
        Socket(23, listOf(14, 15), listOf(), 3, 4, 3),
        Socket(24, listOf(15), listOf(), 3, 6, 3),
        Socket(25, listOf(), listOf(16, 17), 3, 9, 0),
        Socket(26, listOf(18), listOf(), 3, 12, 3),
        Socket(27, listOf(18, 19), listOf(), 3, 14, 3),
        Socket(28, listOf(19, 20), listOf(), 3, 16, 3),
        Socket(29, listOf(20), listOf(), 3, 18, 3),
    )
) {

    override val numberOfColumns: Int get() = COLUMNS

    override val numberOfRows: Int get() = ROWS

    override val tag: String get() = TAG

    companion object {
        private const val COLUMNS = 20
        private const val ROWS = 5
        const val TAG = "inverted2ndLayout"
    }

}