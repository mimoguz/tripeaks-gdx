package ogz.tripeaks.data

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import ogz.tripeaks.Const

class StatKeeper {
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
        if (preferences.getBoolean(Const.SAVE_VALID, false)) {
            this.currentChain = preferences.getInteger(Const.SAVE_CURRENT_CHAIN, 0)
            this.longestChain = preferences.getInteger(Const.SAVE_LONGEST_CHAIN, 0)
            this.removedFromStack = preferences.getInteger(Const.SAVE_REMOVED_FROM_STACK, 0)
            this.undoCount = preferences.getInteger(Const.SAVE_UNDO_COUNT, 0)
            return true
        }
        return false
    }

    fun save(preferences: Preferences) {
        preferences.putInteger(Const.SAVE_CURRENT_CHAIN, currentChain)
        preferences.putInteger(Const.SAVE_LONGEST_CHAIN, longestChain)
        preferences.putInteger(Const.SAVE_REMOVED_FROM_STACK, removedFromStack)
        preferences.putInteger(Const.SAVE_UNDO_COUNT, undoCount)
    }
}