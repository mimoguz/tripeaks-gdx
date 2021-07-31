package ogz.tripeaks.game

import com.badlogic.gdx.Preferences
import ktx.collections.GdxArray
import ktx.collections.GdxIntArray
import ktx.collections.GdxMap
import ktx.log.Logger
import ktx.log.error
import ktx.log.logger
import ogz.tripeaks.game.layout.Layout
import ogz.tripeaks.util.all
import java.lang.Exception

class GameState(
    val layout: Layout,
    val sockets: GdxArray<SocketState>,
    val stack: GdxIntArray,
    val discard: GdxIntArray,
    val minDiscarded: Int
) {

    val canUndo get() = discard.size > minDiscarded
    val canDeal get() = stack.size > 0
    val won get() = sockets.all { it.isEmpty }
    val stalled get() = canDeal || ((layout.numberOfSockets - 1) downTo 0).any { isOpen(it) }

    /** Takes a card from the table and put it to the discard.
     *  @return true if the action is performed, false otherwise.
     */
    fun take(socketIndex: Int): Boolean {
        require(socketIndex in 0 until layout.numberOfSockets)
        if (isOpen(socketIndex) &&
            (discard.isEmpty || Card.areNeighbors(discard.peek(), sockets[socketIndex].card))
        ) {
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


    fun save(preferences: Preferences) {
        preferences.putString(LAYOUT, layout.tag)

        val serializedSockets = sockets.map { it.serialize() }.joinToString(SEPARATOR)
        preferences.putString(SOCKETS, serializedSockets)

        val serializedDiscard = discard.items.joinToString(SEPARATOR)
        preferences.putString(DISCARD, serializedDiscard)

        val serializedStack = stack.items.joinToString(SEPARATOR)
        preferences.putString(STACK, serializedStack)

        preferences.putInteger(MIN_DISCARDED, minDiscarded)
    }

    companion object {
        const val LAYOUT = "layout"
        const val DISCARD = "discard"
        const val SOCKETS = "SOCKETS"
        const val STACK = "stack"
        const val MIN_DISCARDED = "minDiscarded"
        const val SEPARATOR = ";"

        private val log = logger<GameState>()

        fun create(cards: IntArray, emptyDiscard: Boolean, layout: Layout): GameState {
            require(cards.size == 52 && cards.distinct().size == cards.size)

            val sockets = GdxArray<SocketState>(layout.numberOfSockets)
            val stack = GdxIntArray()
            val discard = GdxIntArray(52)
            val minDiscarded = if (emptyDiscard) 0 else 1

            for (i in 0 until layout.numberOfSockets) sockets.add(SocketState(cards[i], false))
            for (i in (layout.numberOfSockets - 1 + minDiscarded) downTo layout.numberOfSockets) discard.add(
                cards[i]
            )
            for (i in 51 downTo (layout.numberOfSockets + minDiscarded)) {
                stack.add(cards[i])
            }

            return GameState(layout, sockets, stack, discard, minDiscarded)
        }

        fun load(preferences: Preferences, layouts: GdxMap<String, Layout>): GameState? {
            try {
                val layout = layouts[preferences.getString(LAYOUT)]

                val serializedSockets = preferences.getString(SOCKETS)
                val socketStates = serializedSockets.split(SEPARATOR).map(SocketState::deserialize)
                val sockets = GdxArray<SocketState>(layout.numberOfSockets)
                socketStates.forEach(sockets::add)

                val serializedDiscard = preferences.getString(DISCARD)
                val discarded = serializedDiscard.split(SEPARATOR).map(Integer::parseInt)
                val discard = GdxIntArray(discarded.size)
                discarded.forEach(discard::add)

                val serializedStack = preferences.getString(STACK)
                val stacked = serializedStack.split(SEPARATOR).map(Integer::parseInt)
                val stack = GdxIntArray(stacked.size)
                stacked.forEach(stack::add)

                val minDiscarded = preferences.getInteger(MIN_DISCARDED)

                require((socketStates.count { !it.isEmpty } +  stack.size + discard.size) == 52)
                require(minDiscarded in 0..1)

                return GameState(layout, sockets, stack, discard, minDiscarded)
            } catch (e: Exception) {
                log.error { "Error loading game state: ${e.message}" }
            }

            return null
        }
    }
}


