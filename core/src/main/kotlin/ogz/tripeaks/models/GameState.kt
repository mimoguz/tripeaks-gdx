package ogz.tripeaks.models

import ktx.collections.GdxArray
import ktx.collections.GdxIntArray
import ktx.collections.gdxArrayOf
import ktx.collections.gdxIntArrayOf
import ktx.collections.toGdxArray
import ogz.tripeaks.models.layout.BasicLayout
import ogz.tripeaks.models.layout.Layout
import ogz.tripeaks.models.layout.Socket

/**
 * GameScreen should use this directly.
 */
@Suppress("unused")
class GameState private constructor(
    private var stats: GameStatistics,
    private var layout: Layout,
    private var sockets: GdxArray<SocketState>,
    private var played: Boolean,
    var stack: GdxIntArray,
    var discard: GdxIntArray,
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
        stats = GameStatistics(BasicLayout.TAG),
        layout = BasicLayout(),
        sockets = gdxArrayOf(),
        played = false,
        stack = gdxIntArrayOf(),
        discard = gdxIntArrayOf(),
        canEmptyDiscard = false,
        stalledBefore = false,
    )

    val isValid: Boolean
        get() = layout.numberOfSockets == sockets.size
                && (sockets.count { !it.isEmpty } + discard.size + stack.size) == 52

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

    val wasPlayed: Boolean
        get() = played

    /** Is the game won? */
    val won: Boolean
        get() = sockets.all { it.isEmpty }

    /** Is the game stalled? */
    val stalled: Boolean
        get() = !(discard.isEmpty || canTakeAny || canDeal)

    val gameLayout: Layout
        get() = layout

    val statistics: GameStatistics
        get() = stats

    fun socketState(index: Int): SocketState = sockets[index]

    fun socket(index: Int): Socket = layout[index]

    /** Removes the card at the socket it can. */
    fun take(socketIndex: Int): Boolean {
        require(socketIndex in 0 until layout.numberOfSockets)
        if (canTake(socketIndex)) {
            // When we take a card from the tableau, we don't actually clear its socket
            // but just mark it empty. This lets us easily restore the tableau on undo.
            sockets[socketIndex].isEmpty = true
            discard.add(sockets[socketIndex].card)
            stalledBefore = stalledBefore || stalled
            stats.onTake()
            played = true
            return true
        }
        return false
    }

    /** Moves a card from stack to discard if there is any left. */
    fun deal(): Boolean {
        if (canDeal) {
            discard.add(stack.pop())
            stalledBefore = stalledBefore || stalled
            stats.onDeal()
            played = true
            return true
        }
        return false
    }

    // TODO: Do I really need a return value here?
    /**
     * Undo the last move, and return the cards socket number (-1 if the card came form the stack).
     * Return Int.MIN_VALUE if the is nothing to undo.
     * It's return value is used to update tableau visuals.
     */
    fun undo(): Int {
        // Discard is empty, there are no moves to undo.
        if (!canUndo) {
            return Int.MIN_VALUE
        }

        val card = discard.pop()
        val socketIndex = sockets.indexOfFirst { it.card == card }
        if (socketIndex == -1) {
            // Card wasn't from the tableau.
            stack.add(card)
        } else {
            // Restore the socket.
            sockets[socketIndex].isEmpty = false
        }
        stats.onUndo(socketIndex)
        return socketIndex
    }

    /** Is the card at the socket index not blocked by any other cards? */
    fun isOpen(socketIndex: Int): Boolean =
        !sockets[socketIndex].isEmpty && layout[socketIndex].blockedBy.all { sockets[it].isEmpty }

    fun isEmpty(socketIndex: Int): Boolean = sockets[socketIndex].isEmpty

    fun t0Copy(): GameState {
        // Basically, create a copy and undo everything
        val copyStack = stack.toArray().toGdxArray()
        val copyDiscard = discard.toArray().toGdxArray()
        val copySockets = sockets.toGdxArray()
        val minDiscardLength = if (canEmptyDiscard) 0 else 1
        while (copyDiscard.size > minDiscardLength) {
            val card = copyDiscard.pop()
            val socketIndex = copySockets.indexOfFirst { it.card == card }
            if (socketIndex == -1) {
                copyStack.add(card)
            } else {
                copySockets[socketIndex].isEmpty = false
            }
        }
        return GameState(
            stats = GameStatistics(gameLayout.tag),
            layout = layout,
            sockets = copySockets,
            played = false,
            stack = copyStack,
            discard = copyDiscard,
            canEmptyDiscard = canEmptyDiscard,
            stalledBefore = false
        )
    }

    /** Can we remove the card at the socket index from the tableau? */
    private fun canTake(socketIndex: Int): Boolean =
        isOpen(socketIndex) && (discard.isEmpty || sockets[socketIndex].card.areNeighbors(discard.peek()))

    companion object {

        fun startNew(layout: Layout, emptyDiscard: Boolean): GameState {
            val statistics = GameStatistics(layout.tag)
            val cards = (0 until 52).shuffled()

            val sockets = GdxArray<SocketState>(layout.numberOfSockets)
            for (i in 0 until layout.numberOfSockets) {
                sockets.add(SocketState(cards[i], false))
            }

            val discard = gdxIntArrayOf()
            if (!emptyDiscard) {
                discard.add(cards[layout.numberOfSockets])
            }

            val stack = gdxIntArrayOf()
            // Iterate from the end, so that the first card remained after the tableau and the discard pile
            // have been set up will end up at the top of the stack.
            for (i in 51 downTo (layout.numberOfSockets + if (emptyDiscard) 0 else 1)) {
                stack.add(cards[i])
            }

            return GameState(
                stats = statistics,
                layout = layout,
                sockets = sockets,
                played = false,
                stack = stack,
                discard = discard,
                canEmptyDiscard = emptyDiscard,
                stalledBefore = false
            )
        }

    }

}