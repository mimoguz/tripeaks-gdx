package ogz.tripeaks.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import ktx.collections.GdxArray
import ktx.collections.GdxIntArray
import ktx.collections.GdxMap
import ktx.log.error
import ktx.log.logger
import ogz.tripeaks.game.layout.Layout
import ogz.tripeaks.util.all

class GameState private constructor(
    val layout: Layout,
    val sockets: GdxArray<SocketState>,
    val stack: GdxIntArray,
    val discard: GdxIntArray,
    val minDiscarded: Int,
    longestChain: Int = 0,
    currentChain: Int = 0,
    removedFromStack: Int = 0,
    undos: Int = 0
) {

    val canUndo get() = discard.size > minDiscarded
    val canDeal get() = stack.size > 0
    val won get() = sockets.all { it.isEmpty }
    val stalled: Boolean
        get() = !(canDeal ||
                discard.isEmpty ||
                ((layout.numberOfSockets - 1) downTo 0).any {
                    isOpen(it) && Card.areNeighbors(discard.peek(), sockets[it].card)
                })

    var currentChainLength = currentChain
        private set

    var longestChainLength = longestChain
        private set

    var cardsRemovedFromStack = removedFromStack
        private set

    var undoCount = undos
        private set


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
            currentChainLength++
            longestChainLength = maxOf(currentChainLength, longestChainLength)
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
        currentChainLength = 0
        cardsRemovedFromStack++
        return true
    }

    /** Undoes an action.
     *  @return null if can't undo, socket number if the card came from a socket, -1 if the card came from the stack.
     */
    fun undo(): Int? {
        if (discard.size <= minDiscarded) return null

        currentChainLength = 0
        undoCount++

        val card = discard.pop()
        val socketIndex = sockets.indexOfFirst { it.card == card }

        if (socketIndex < 0) {
            stack.add(card)
        } else {
            sockets[socketIndex].isEmpty = false
        }

        return socketIndex
    }

    /** Checks if a socket is not blocked. */
    fun isOpen(socketIndex: Int) =
        (!sockets[socketIndex].isEmpty) && layout[socketIndex].blockedBy.all { sockets[it].isEmpty }


    fun save() {
        val preferences = Gdx.app.getPreferences(SAVE_NAME)
        preferences.clear()

        val serializedSockets = sockets.map { it.serialize() }.joinToString(SEPARATOR)
        preferences.putString(SOCKETS, serializedSockets)

        val discardCopy = discard.toArray()
        val serializedDiscard = discardCopy.joinToString(SEPARATOR)
        preferences.putString(DISCARD, serializedDiscard)

        val stackCopy = stack.toArray()
        val serializedStack = stackCopy.joinToString(SEPARATOR)
        preferences.putString(STACK, serializedStack)

        preferences.putString(LAYOUT, layout.tag)
        preferences.putInteger(CURRENT_CHAIN, currentChainLength)
        preferences.putInteger(LONGEST_CHAIN, longestChainLength)
        preferences.putInteger(REMOVED_FROM_STACK, cardsRemovedFromStack)
        preferences.putInteger(UNDO_COUNT, undoCount)
        preferences.putInteger(MIN_DISCARDED, minDiscarded)

        preferences.flush()
    }

    companion object {
        const val LAYOUT = "layout"
        const val DISCARD = "discard"
        const val SOCKETS = "sockets"
        const val STACK = "stack"
        const val MIN_DISCARDED = "minDiscarded"
        const val SEPARATOR = ";"
        const val CURRENT_CHAIN = "currentChain"
        const val LONGEST_CHAIN = "longestChain"
        const val REMOVED_FROM_STACK = "removedFromStack"
        const val UNDO_COUNT = "undoCount"
        const val SAVE_NAME = "save"

        private val log = logger<GameState>()

        fun new(cards: IntArray, emptyDiscard: Boolean, layout: Layout): GameState {
            require(cards.size == 52 && cards.distinct().size == cards.size)

            val sockets = GdxArray<SocketState>(layout.numberOfSockets)
            val stack = GdxIntArray.with()
            val discard = GdxIntArray.with()
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

        fun load(layouts: GdxMap<String, Layout>): GameState? {
            val preferences = Gdx.app.getPreferences(SAVE_NAME)

            try {
                if (!preferences.contains(LAYOUT)) return null

                val layout = layouts[preferences.getString(LAYOUT)]

                val serializedSockets = preferences.getString(SOCKETS)
                val socketStates = serializedSockets.split(SEPARATOR).map(SocketState::deserialize)
                val sockets = GdxArray<SocketState>(layout.numberOfSockets)
                socketStates.forEach(sockets::add)

                val serializedDiscard = preferences.getString(DISCARD)
                val discarded = if (serializedDiscard.isNotBlank()) {
                    serializedDiscard.split(SEPARATOR).map(Integer::parseInt)
                } else {
                    listOf()
                }
                val discard = GdxIntArray((discarded.size))
                discarded.forEach(discard::add)

                val serializedStack = preferences.getString(STACK)
                val stacked = if (serializedStack.isNotBlank()) {
                    serializedStack.split(SEPARATOR).map(Integer::parseInt)
                } else {
                    listOf()
                }
                val stack = GdxIntArray(stacked.size)
                stacked.forEach(stack::add)

                val minDiscarded = preferences.getInteger(MIN_DISCARDED)

                val currentChain = preferences.getInteger(CURRENT_CHAIN)
                val longestChain = preferences.getInteger(LONGEST_CHAIN)
                val removedFromStack = preferences.getInteger(REMOVED_FROM_STACK)
                val undos = preferences.getInteger(UNDO_COUNT)

                require((socketStates.count { !it.isEmpty } + stack.size + discard.size) == 52)

                return GameState(
                    layout = layout,
                    sockets = sockets,
                    stack = stack,
                    discard = discard,
                    minDiscarded = minDiscarded,
                    longestChain = longestChain,
                    currentChain = currentChain,
                    removedFromStack = removedFromStack,
                    undos = undos
                )
            } catch (e: Exception) {
                log.error { "Error loading game state: ${e.message}\n\n${e.stackTrace.joinToString("\n")}" }
            }

            return null
        }

        fun clearSave() {
            val preferences = Gdx.app.getPreferences(SAVE_NAME)
            preferences.clear()
            preferences.flush()
        }
    }
}


