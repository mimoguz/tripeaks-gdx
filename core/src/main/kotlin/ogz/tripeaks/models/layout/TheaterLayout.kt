package ogz.tripeaks.models.layout

class TheaterLayout : LayoutBase(
    listOf(
        Socket(0, listOf(), listOf(6, 7), 0, 3, 0),
        Socket(1, listOf(), listOf(7), 0, 5, 0),
        Socket(2, listOf(), listOf(8, 9), 0, 8, 0),
        Socket(3, listOf(), listOf(9, 10), 0, 10, 0),
        Socket(4, listOf(), listOf(11), 0, 13, 0),
        Socket(5, listOf(), listOf(11, 12), 0, 15, 0),
        Socket(6, listOf(0), listOf(13, 14), 1, 2, 1),
        Socket(7, listOf(0, 1), listOf(14), 1, 4, 1),
        Socket(8, listOf(2), listOf(15, 16), 1, 7, 1),
        Socket(9, listOf(2, 3), listOf(16, 17), 1, 9, 1),
        Socket(10, listOf(3), listOf(17, 18), 1, 11, 1),
        Socket(11, listOf(4, 5), listOf(19), 1, 14, 1),
        Socket(12, listOf(5), listOf(19, 20), 1, 16, 1),
        Socket(13, listOf(6), listOf(21, 22), 2, 1, 2),
        Socket(14, listOf(6, 7), listOf(22), 2, 3, 2),
        Socket(15, listOf(8), listOf(23, 24), 2, 6, 2),
        Socket(16, listOf(8, 9), listOf(24, 25), 2, 8, 2),
        Socket(17, listOf(9, 10), listOf(25, 26), 2, 10, 2),
        Socket(18, listOf(10), listOf(26, 27), 2, 12, 2),
        Socket(19, listOf(11, 12), listOf(28), 2, 15, 2),
        Socket(20, listOf(12), listOf(28, 29), 2, 17, 2),
        Socket(21, listOf(13), listOf(), 3, 0, 3),
        Socket(22, listOf(13, 14), listOf(), 3, 2, 3),
        Socket(23, listOf(15), listOf(), 3, 5, 3),
        Socket(24, listOf(15, 16), listOf(), 3, 7, 3),
        Socket(25, listOf(16, 17), listOf(), 3, 9, 3),
        Socket(26, listOf(17, 18), listOf(), 3, 11, 3),
        Socket(27, listOf(18), listOf(), 3, 13, 3),
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
        const val TAG = "theaterLayout"
    }

}