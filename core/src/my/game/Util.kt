package my.game

import ktx.collections.GdxArray
import my.game.data.Rank
import my.game.data.Source
import my.game.data.Suit

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
}

