package my.game.data

import java.util.*
import kotlin.math.abs

class Card {
    private var suit: Suit = Suit.Club
    private var rank: Rank = Rank.Ace
    var isOpen: Boolean = false
    var source: Source = Source.Stack

    fun set(suit: Suit, rank: Rank, source: Source, open: Boolean = false): Card {
        this.suit = suit
        this.rank = rank
        this.isOpen = open
        this.source = source
        return this
    }

    fun getSpriteName() =
            suit.toString().toLowerCase(Locale.US) +
                    (rank.ordinal + 1).toString().padStart(2, '0')

    fun areConsecutive(other: Card): Boolean {
        val distance = abs(rank.ordinal - other.rank.ordinal)
        return distance == 1 || distance == 12
    }

    override fun toString(): String = "$rank of ${suit}s"
}

