package ogz.tripeaks.game

import com.badlogic.gdx.Preferences

class GameStats {
    var currentChain: Int = 0
        private set

    var longestChain: Int = 0
        private set

    var removedFromStack: Int = 0
        private set

    var undoCount: Int = 0
        private set

    fun set(currentChain: Int, longestChain: Int, removedFromStack: Int, undoCount: Int) {
        this.currentChain = currentChain
        this.longestChain = longestChain
        this.removedFromStack = removedFromStack
        this.undoCount = undoCount
    }

    fun backToPeaks() {
        currentChain = 0
        undoCount += 1
    }

    fun backToStack() {
        currentChain = 0
        undoCount += 1
        removedFromStack -= 1
    }

    fun takeFromPeaks() {
        currentChain += 1
        if (currentChain > longestChain) {
            longestChain = currentChain
        }
    }

    fun takeFromStack() {
        currentChain = 0
        removedFromStack += 1
    }

    fun load(preferences: Preferences): Boolean {
        currentChain = preferences.getInteger(CURRENT_CHAIN, 0)
        longestChain = preferences.getInteger(LONGEST_CHAIN, 0)
        removedFromStack = preferences.getInteger(REMOVED_FROM_STACK, 0)
        undoCount = preferences.getInteger(UNDO_COUNT, 0)
        return true
    }

    fun save(preferences: Preferences) {
        preferences.putInteger(CURRENT_CHAIN, currentChain)
        preferences.putInteger(LONGEST_CHAIN, longestChain)
        preferences.putInteger(REMOVED_FROM_STACK, removedFromStack)
        preferences.putInteger(UNDO_COUNT, undoCount)
    }

    fun reset() {
        set(0, 0, 0, 0)
    }

    companion object {
        const val CURRENT_CHAIN = "currentChain"
        const val LONGEST_CHAIN = "longestChain"
        const val REMOVED_FROM_STACK = "removedFromStack"
        const val UNDO_COUNT = "undoCount"
    }
}