package ogz.tripeaks.game

import ktx.collections.GdxArray
import ktx.collections.GdxIntArray
import ogz.tripeaks.util.all

class GameState(cards: IntArray, emptyDiscard: Boolean, val layout: Layout) {
    val sockets = GdxArray<SocketState>(layout.numberOfSockets)
    val stack = GdxIntArray()
    val discard = GdxIntArray(52)
    private val minDiscarded = if (emptyDiscard) 0 else 1

    val canUndo get() = discard.size > minDiscarded
    val canDeal get() = stack.size > 0
    val won get() = sockets.all { it.isEmpty }
    val stalled get() = canDeal || ((layout.numberOfSockets - 1) downTo 0).any { isOpen(it) }

    init {
        require(cards.size == 52 && cards.distinct().size == cards.size)
        for (i in 0 until layout.numberOfSockets) sockets.add(SocketState(cards[i], false))
        for (i in (layout.numberOfSockets - 1 + minDiscarded) downTo layout.numberOfSockets) discard.add(cards[i])
        for (i in 51 downTo (layout.numberOfSockets + minDiscarded)) {
            stack.add(cards[i])
        }
    }

    /** Takes a card from the table and put it to the discard.
     *  @return true if the action is performed, false otherwise.
     */
    fun take(socketIndex: Int): Boolean {
        require(socketIndex in 0 until layout.numberOfSockets)
        if (isOpen(socketIndex) &&
            (discard.isEmpty || Card.areNeighbors(discard.peek(), sockets[socketIndex].card))) {
            sockets[socketIndex].isEmpty = true
            discard.add(sockets[socketIndex].card)
            return true
        }
        return false
    }

    /** Takes a new card from the stack
     * @return true if there was at least one card in the stack, false otherwise.
     */
    fun deal(): Boolean {
        if (!canDeal) return false
        discard.add(stack.pop())
        return true
    }

    /** Undoes an action.
     *  @return null if can't undo, socket number if the card came from a socket, -1 if the card came from the stack.
     */
    fun undo(): Int? {
        if (discard.size <= minDiscarded) return null

        val card = discard.pop()
        val socketIndex = sockets.indexOfFirst { it.card == card }

        if (socketIndex < 0) {
            stack.add(card)
            return -1
        }

        sockets[socketIndex].isEmpty = false
        return socketIndex
    }

    /** Checks if a socket is not blocked. */
    fun isOpen(socketIndex: Int) =
        (!sockets[socketIndex].isEmpty) && layout[socketIndex].blockedBy.all { sockets[it].isEmpty }
}


