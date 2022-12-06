package ogz.tripeaks.models

import ktx.collections.GdxArray
import ktx.collections.GdxIntArray
import ogz.tripeaks.models.layout.BasicLayout
import ogz.tripeaks.models.layout.Layout

@Suppress("unused")
class GameState private constructor(
    private var state: Int,
    private var layout: Layout,
    private var sockets: GdxArray<SocketState>,
    private var stack: GdxIntArray,
    private var discard: GdxIntArray,
    /** This property tells that if the game was started with an empty discard pile or not. */
    private var canEmptyDiscard: Boolean,
    /**
     * This property is used to track if the game was stalled before.
     * We take it as a constructor parameter so it can be serialized automatically.
     */
    private var stalledBefore: Boolean,
) {

    /**
     * Empty constructor that's required for de-serialization. Don't use this directly,
     * use GameState::startNew method instead.
     */
    constructor() : this(
        state = 0,
        layout = BasicLayout(),
        sockets = GdxArray(),
        stack = GdxIntArray(),
        discard = GdxIntArray(),
        canEmptyDiscard = true,
        stalledBefore = true,
    )

    val currentState: Int
        get() = state

    /** Are there any moves we can take back? */
    val canUndo: Boolean
        get() = if (canEmptyDiscard) discard.size > 0 else discard.size > 1

    /** Are there any cards left on the stack? */
    val canDeal: Boolean
        get() = stack.size > 0

    /** Are there any playable cards on the tableau? */
    val canTakeAny: Boolean
        get() = ((layout.numberOfSockets - 1) downTo 0).any { idx ->
            isOpen(idx) && discard.peek().areNeighbors(sockets[idx].card)
        }

    /**
     * This property can be used to check if the game was stalled before, so we won't show the
     * stalled game dialog more than once for the same game. Unlike the ```stalled``` property,
     * it won't be cleared after undo.
     */
    val wasStalledBefore: Boolean
        get() = stalledBefore

    /** Is the game won? */
    val won: Boolean
        get() = sockets.all { it.isEmpty }

    /** Is the game stalled? */
    val stalled: Boolean
        get() = !(discard.isEmpty || canTakeAny || canDeal)

    fun step() {
        state += 1
    }

    /** Removes the card at the socket it can. */
    fun take(socketIndex: Int): Boolean {
        require(socketIndex in 0 until layout.numberOfSockets)
        if (canTake(socketIndex)) {
            // When we take a card from the tableau, we don't actually clear its socket
            // but just mark it empty. This lets us easily restore the tableau on undo.
            sockets[socketIndex].isEmpty = true
            discard.add(sockets[socketIndex].card)
            stalledBefore = stalledBefore || stalled
            return true
        }
        return false
    }

    /** Moves a card from stack to discard if there is any left. */
    fun deal(): Boolean {
        if (canDeal) {
            discard.add(stack.pop())
            stalledBefore = stalledBefore || stalled
            return true
        }
        return false
    }

    fun unstall() {
        stalledBefore = false
    }

    // TODO: Do I really need a return value here?
    /**
     * Undo the last move, and return the cards socket number (-1 if the card came form the stack).
     * It's return value is used to update tableau visuals.
     */
    fun undo(): Int? {
        // Discard is empty, there are no moves to undo.
        if (!canUndo) {
            return null
        }

        val card = discard.pop()
        val socketIndex = sockets.indexOfFirst { it.card == card }
        if (socketIndex < 0) {
            // Card wasn't from the tableau.
            stack.add(card)
        } else {
            // Restore the socket.
            sockets[socketIndex].isEmpty = false
        }

        return socketIndex
    }

    /** Is the card at the socket index not blocked by any other cards? */
    private fun isOpen(socketIndex: Int): Boolean =
        (!sockets[socketIndex].isEmpty) && layout[socketIndex].blockedBy.items.all { sockets[it].isEmpty }

    /** Can we remove the card at the socket index from the tableau? */
    private fun canTake(socketIndex: Int): Boolean =
        isOpen(socketIndex) && (discard.isEmpty || sockets[socketIndex].card.areNeighbors(discard.peek()))

    companion object {
        fun startNew(cards: IntArray, preferences: Any?): GameState {
            // A deck of 52 distinct cards.
            require(cards.size == 52 && cards.distinct().size == cards.size && cards.all { it in 0..51 })

            val layout = BasicLayout()
            val emptyDiscard = false
            val sockets = GdxArray<SocketState>(layout.numberOfSockets)
            val stack = GdxIntArray.with()
            val discard = GdxIntArray.with()

            for (i in 0 until layout.numberOfSockets) {
                sockets.add(SocketState(cards[i], false))
            }

            if (!emptyDiscard) {
                discard.add(cards[layout.numberOfSockets])
            }

            // Iterate from the end, so that the first card remained after the tableau and the discard pile
            // have been set up will end up at the top of the stack.
            for (i in 51 downTo (layout.numberOfSockets + if (emptyDiscard) 0 else 1)) {
                stack.add(cards[i])
            }

            return GameState(
                state = 0,
                layout = layout,
                sockets = sockets,
                stack = stack,
                discard = discard,
                canEmptyDiscard = emptyDiscard,
                stalledBefore = false
            )
        }
    }
}