package ogz.tripeaks.game

import com.badlogic.gdx.Preferences
import ktx.collections.GdxMap
import ktx.collections.getOrPut
import kotlin.math.max

// TODO: Finish class
class Statistics private constructor(
    games: Int,
    wins: Int,
    layoutStats: GdxMap<String, LayoutStatistics>,
    longestChain: Int,
    currentChain: Int,
    removedFromStack: Int,
    undos: Int,
    layoutTag: String,
) {
    private var dirty = false
    private var won = false

    var currentChainLength = currentChain
        private set(value) {
            field = value
            dirty = dirty || (value > 0)
        }

    var longestChainLength = longestChain
        private set

    var cardsRemovedFromStack = removedFromStack
        private set

    var currentLayout = layoutTag
        private set

    var undoCount = undos
        private set(value) {
            field = value
            dirty = dirty || (value > 0)
        }

    var gamesPlayed = games
        private set

    var gamesWon = wins
        private set

    val perLayoutStatistics = layoutStats

    fun win() {
        won = true
        update(true)
    }

    fun start(layoutTag: String) {
        if (dirty && !won) update(false)
        dirty = false
        won = false
        currentLayout = layoutTag
        longestChainLength = 0
        cardsRemovedFromStack = 0
        currentChainLength = 0
        undoCount = 0
    }

    fun deal() {
        longestChainLength = max(currentChainLength, longestChainLength)
        currentChainLength = 0
        cardsRemovedFromStack++
    }

    fun undo(toStack: Boolean) {
        undoCount++
        longestChainLength = max(currentChainLength, longestChainLength)
        currentChainLength = 0
        if (toStack) cardsRemovedFromStack--
    }

    fun take() {
        currentChainLength++
        longestChainLength = max(currentChainLength, longestChainLength)
    }

    private fun update(won: Boolean) {
        val layout = perLayoutStatistics.getOrPut(currentLayout) { LayoutStatistics(currentLayout, 0, 0, 0) }
        if (won) {
            gamesWon++
            layout.won++
        }
        gamesPlayed++
        layout.played++
        layout.longestChain = max(longestChainLength, layout.longestChain)
    }

    fun load() {
        // TODO
    }

    fun save() {
        // TODO
    }

    companion object {
        const val CURRENT_CHAIN = "currentChain"
        const val LONGEST_CHAIN = "longestChain"
        const val REMOVED_FROM_STACK = "removedFromStack"

        fun new(currentLayout: String) = Statistics(
            games = 0,
            wins = 0,
            layoutStats = GdxMap(),
            longestChain = 0,
            currentChain = 0,
            removedFromStack = 0,
            undos = 0,
            layoutTag = currentLayout
        )

        fun load() {
            // TODO
        }
    }
}

class LayoutStatistics(var tag: String, var played: Int, var won: Int, var longestChain: Int)
