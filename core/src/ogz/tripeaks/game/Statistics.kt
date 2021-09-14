package ogz.tripeaks.game

import com.badlogic.gdx.Preferences
import ktx.collections.GdxMap
import ktx.collections.getOrPut
import ktx.collections.set
import kotlin.math.max

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
        private set

    var longestChainLength = longestChain
        private set

    var cardsRemovedFromStack = removedFromStack
        private set

    var currentLayout = layoutTag
        private set

    var undoCount = undos
        private set

    var gamesPlayed = games
        private set

    var gamesWon = wins
        private set

    val perLayoutStatistics = layoutStats

    fun win() {
        won = true
        update(true)
    }

    fun startNewGame(layoutTag: String) {
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
        dirty = true
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
        dirty = true
    }

    private fun update(won: Boolean) {
        val layout =
            perLayoutStatistics.getOrPut(currentLayout) { LayoutStatistics(currentLayout, 0, 0, 0) }
        if (won) {
            gamesWon++
            layout.won++
        }
        gamesPlayed++
        layout.played++
        layout.longestChain = max(longestChainLength, layout.longestChain)
    }

    fun save(preferences: Preferences) {
        preferences.apply {
            putInteger(CURRENT_CHAIN, currentChainLength)
            putInteger(LONGEST_CHAIN, longestChainLength)
            putInteger(REMOVED_FROM_STACK, cardsRemovedFromStack)
            putInteger(UNDO_COUNT, undoCount)
            putInteger(GAMES, gamesPlayed)
            putInteger(WINS, gamesWon)

            for (layout in perLayoutStatistics.values()) {
                putInteger("${layout.tag}_$LAYOUT_PLAYED", layout.played)
                putInteger("${layout.tag}_$LAYOUT_WON", layout.won)
                putInteger("${layout.tag}_$LAYOUT_LONGEST_CHAIN", layout.longestChain)
            }
        }
    }

    companion object {
        const val CURRENT_CHAIN = "currentChain"
        const val LONGEST_CHAIN = "longestChain"
        const val REMOVED_FROM_STACK = "removedFromStack"
        const val UNDO_COUNT = "undoCount"
        const val GAMES = "gamesPlayed"
        const val WINS = "gamesWon"
        const val LAYOUT_PLAYED = "Played"
        const val LAYOUT_WON = "Won"
        const val LAYOUT_LONGEST_CHAIN = "LongestChain"

        private var instance: Statistics? = null

        fun getInstance(currentLayout: String): Statistics {
            if (instance != null) {
                return instance!!
            } else {
                val stats = Statistics(
                    games = 0,
                    wins = 0,
                    layoutStats = GdxMap(),
                    longestChain = 0,
                    currentChain = 0,
                    removedFromStack = 0,
                    undos = 0,
                    layoutTag = currentLayout
                )
                instance = stats
                return stats
            }
        }

        fun load(
            preferences: Preferences,
            currentLayout: String,
            layouts: List<String>
        ): Statistics {
            val currentChain = preferences.getInteger(CURRENT_CHAIN)
            val longestChain = preferences.getInteger(LONGEST_CHAIN)
            val removedFromStack = preferences.getInteger(REMOVED_FROM_STACK)
            val undos = preferences.getInteger(UNDO_COUNT)
            val games = preferences.getInteger(GAMES, 0)
            val wins = preferences.getInteger(WINS, 0)

            val layoutStats = GdxMap<String, LayoutStatistics>()
            for (tag in layouts) {
                val played = preferences.getInteger("${tag}_$LAYOUT_PLAYED", 0)
                val won = preferences.getInteger("${tag}_$LAYOUT_WON", 0)
                val longestChain = preferences.getInteger("${tag}_$LAYOUT_LONGEST_CHAIN", 0)
                layoutStats[tag] = LayoutStatistics(tag, played, won, longestChain)
            }

            val stats = Statistics(
                games,
                wins,
                layoutStats,
                longestChain,
                currentChain,
                removedFromStack,
                undos,
                currentLayout
            )
            instance = stats
            return stats;
        }
    }
}

class LayoutStatistics(var tag: String, var played: Int, var won: Int, var longestChain: Int)
