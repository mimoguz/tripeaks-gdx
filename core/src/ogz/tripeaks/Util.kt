package ogz.tripeaks

import ktx.collections.GdxArray
import ogz.tripeaks.data.Rank
import ogz.tripeaks.data.Source
import ogz.tripeaks.data.Suit

object Util {
    data class CardInfo(val suit: Suit, val rank: Rank)

    fun makeDeck(): GdxArray<CardInfo> {
        val deck = GdxArray<CardInfo>(52)
        for (suit in Suit.values()) {
            for (rank in Rank.values()) {
                deck.add(CardInfo(suit, rank))
            }
        }
        deck.shuffle()
        return deck
    }

    inline fun getIndex(column: Int, row: Int): Int = row * Constants.COLUMN_COUNT + column

    inline fun getIndex(cell: Source.Cell): Int = getIndex(cell.column, cell.row)

    inline fun getColumn(index: Int): Int = index % Constants.COLUMN_COUNT

    inline fun getRow(index: Int): Int = index / Constants.COLUMN_COUNT

    inline fun getCellX(column: Int): Float = column * Constants.CELL_WIDTH + 1f

    inline fun getCellY(row: Int): Float =
            Constants.CONTENT_HEIGHT - (row + 2) * Constants.CELL_HEIGHT - Constants.VERTICAL_PADDING
}

