package ogz.tripeaks.game

import com.badlogic.gdx.Preferences
import ktx.collections.GdxMap
import ktx.collections.getOrPut
import ktx.collections.set
import kotlin.math.max

@Suppress("GDXKotlinUnsafeIterator")
class Statistics private constructor(
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
            layout.won++
        }
        layout.played++
        layout.longestChain = max(longestChainLength, layout.longestChain)
    }

    fun save(preferences: Preferences) {
        preferences.apply {
            putInteger(CURRENT_CHAIN, currentChainLength)
            putInteger(LONGEST_CHAIN, longestChainLength)
            putInteger(REMOVED_FROM_STACK, cardsRemovedFromStack)
            putInteger(UNDO_COUNT, undoCount)

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
        const val LAYOUT_PLAYED = "Played"
        const val LAYOUT_WON = "Won"
        const val LAYOUT_LONGEST_CHAIN = "LongestChain"

        private var instance: Statistics? = null

        fun getInstance(defaultLayout: String): Statistics {
            instance = instance ?: Statistics(
                layoutStats = GdxMap(),
                longestChain = 0,
                currentChain = 0,
                removedFromStack = 0,
                undos = 0,
                layoutTag = defaultLayout
            )
            return instance!!
        }

        fun load(
            preferences: Preferences,
            savedLayout: String,
            layouts: List<String>
        ): Statistics {
            val currentChain = preferences.getInteger(CURRENT_CHAIN)
            val longestChain = preferences.getInteger(LONGEST_CHAIN)
            val removedFromStack = preferences.getInteger(REMOVED_FROM_STACK)
            val undos = preferences.getInteger(UNDO_COUNT)

            val layoutStats = GdxMap<String, LayoutStatistics>()
            for (tag in layouts) {
                val layoutPlayed = preferences.getInteger("${tag}_$LAYOUT_PLAYED", 0)
                val layoutWon = preferences.getInteger("${tag}_$LAYOUT_WON", 0)
                val layoutLongestChain = preferences.getInteger("${tag}_$LAYOUT_LONGEST_CHAIN", 0)
                layoutStats[tag] = LayoutStatistics(tag, layoutPlayed, layoutWon, layoutLongestChain)
            }

            instance = Statistics(
                layoutStats = layoutStats,
                longestChain = longestChain,
                currentChain = currentChain,
                removedFromStack = removedFromStack,
                undos = undos,
                layoutTag = savedLayout
            )
            return instance!!
        }
    }
}

class LayoutStatistics(var tag: String, var played: Int, var won: Int, var longestChain: Int)
