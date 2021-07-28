package ogz.tripeaks.game

import kotlin.math.absoluteValue


/** A "card" is just an integer between 0 and 51 (inclusive). I would like to use IntArrays to store them,
 * so no inline classes. This object provides helper functions.
 */
object Card {
    fun areNeighbors(card1: Int, card2: Int): Boolean {
        assert(card1 in 0..51) { "card1 ($card1) is not in valid range" }
        assert(card2 in 0..51) { "card2 ($card2) is not in valid range" }
        val rank1 = card1 % 13
        val rank2 = card2 % 13
        return ((rank1 - rank2).absoluteValue == 1 ||
                (rank1 == 0 && rank2 == 12) ||
                (rank1 == 12 && rank2 == 0))
    }
}

