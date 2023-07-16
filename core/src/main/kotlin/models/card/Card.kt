package ogz.tripeaks.models

import kotlin.math.absoluteValue

typealias Card = Int

/** Are this card and the other card circular-consecutive? */
fun Card.areNeighbors(other: Card): Boolean {
    assert(this in 0..51) { "The card 1 ($this) is not in valid range" }
    assert(other in 0..51) { "The card 2 ($other) is not in valid range" }
    val rank1 = this % 13
    val rank2 = other % 13
    return (rank1 - rank2).absoluteValue == 1 || (rank1 == 0 && rank2 == 12) || (rank1 == 12 && rank2 == 0)
}
