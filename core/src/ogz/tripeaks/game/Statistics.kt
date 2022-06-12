package ogz.tripeaks.game

import com.badlogic.gdx.Preferences
import ktx.collections.GdxMap
import ktx.collections.getOrPut
import ktx.collections.set
import ktx.log.logger
import kotlin.math.max

@Suppress("GDXKotlinUnsafeIterator")
class Statistics private constructor(
    layoutStats: GdxMap<String, LayoutStatistics>,
    currentLongestChain: Int,
    currentChain: Int,
    removedFromStack: Int,
    undos: Int,
    layoutTag: String,
) {

    private var currentLayout = layoutStats.getOrPut(layoutTag) {
        LayoutStatistics(layoutTag, 0, 0, 0)
    }

    private var dirty: Boolean = false
        private set(value) {
            if (!field && value) {
                currentLayout.played++
            }
            field = value
        }

    private var won: Boolean = false
        private set(value) {
            if (!field && value) {
                currentLayout.won++
            }
            field = value
        }

    val perLayoutStatistics = layoutStats

    var currentChainLength = currentChain
        private set

    var cardsRemovedFromStack = removedFromStack
        private set

    var undoCount = undos
        private set

    var currentLongestChainLength: Int = currentLongestChain
        private set(value) {
            field = value
            currentLayout.longestChain = max(value, currentLayout.longestChain)
        }

    fun win() {
        won = true
    }

    fun startNewGame(layoutTag: String) {
        currentLayout =
            perLayoutStatistics.getOrPut(layoutTag) { LayoutStatistics(layoutTag, 0, 0, 0) }
        dirty = false
        won = false
        currentLongestChainLength = 0
        cardsRemovedFromStack = 0
        currentChainLength = 0
        undoCount = 0
    }

    fun deal() {
        currentChainLength = 0
        cardsRemovedFromStack++
        dirty = true
    }

    fun undo(toStack: Boolean) {
        currentChainLength = 0
        undoCount++
        if (toStack) cardsRemovedFromStack--
    }

    fun take() {
        currentChainLength++
        currentLongestChainLength = max(currentChainLength, currentLongestChainLength)
        dirty = true
    }

    fun save(preferences: Preferences) {
        preferences.apply {
            putInteger(CURRENT_CHAIN, currentChainLength)
            putInteger(LONGEST_CHAIN, currentLongestChainLength)
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
        private val log = logger<Statistics>()

        fun getInstance(defaultLayout: String): Statistics {
            instance = instance ?: Statistics(
                layoutStats = GdxMap(),
                currentLongestChain = 0,
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
                try {
                    val layoutPlayed = preferences.getInteger("${tag}_$LAYOUT_PLAYED")
                    val layoutWon = preferences.getInteger("${tag}_$LAYOUT_WON")
                    val layoutLongestChain = preferences.getInteger("${tag}_$LAYOUT_LONGEST_CHAIN")
                    layoutStats[tag] =
                        LayoutStatistics(tag, layoutPlayed, layoutWon, layoutLongestChain)
                } catch (e: Exception) {
                    log.error {
                        "Statistics for $tag doesn't exists of malformed: ${e.message}\n\n${
                            e.stackTrace.joinToString(
                                "\n"
                            )
                        }"
                    }
                }
            }

            instance = Statistics(
                layoutStats = layoutStats,
                currentLongestChain = longestChain,
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
