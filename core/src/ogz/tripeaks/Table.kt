package ogz.tripeaks

object Table {
    const val OPEN_CARD_SYMBOL = 'o'
    const val CLOSED_CARD_SYMBOL = 'c'

    val layout =
        """
            ---c-----c-----c----
            --c-c---c-c---c-c---
            -c-c-c-c-c-c-c-c-c--
            o-o-o-o-o-o-o-o-o-o-
        """.trimIndent().lines().map { it.toCharArray() }
}
