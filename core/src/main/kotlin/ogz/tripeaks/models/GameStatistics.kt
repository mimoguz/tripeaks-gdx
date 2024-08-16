package ogz.tripeaks.models

import ktx.collections.GdxIntArray
import ktx.collections.gdxIntArrayOf

class GameStatistics(
    private var tag: String,
    private var chains: GdxIntArray = gdxIntArrayOf(),
    private var undoCounter: Int = 0,
    private var score: Int = 0
) {

    constructor() : this("")

    val longestChain
        get() = chains.max( 0)

    val undoCount
        get() = undoCounter

    val layoutTag: String
        get() = tag

    fun onDeal() {
        chains.add(0)
    }

    fun onTake() {
        if (chains.isEmpty) {
            chains.add(0)
        }
        chains[chains.size - 1] += 1
    }

    fun onUndo(socket: Int) {
        if (targetIsTableau(socket)) {
            // You can't undo a take if there wasn't any, so this must be safe:
            var currentChain = chains[chains.size - 1]
            currentChain -= 1
            if (currentChain == 0) {
                chains.pop()
            } else {
                chains[chains.size - 1] = currentChain
            }
            undoCounter += 1
        } else if (targetIsDeck(socket)) {
            undoCounter += 1
        }
    }

    companion object {

        private fun GdxIntArray.max(initialValue: Int = Int.MIN_VALUE): Int {
            var max = initialValue
            for (i in 0 until this.size) {
                max = max.coerceAtLeast(this[i])
            }
            return max
        }

        private fun targetIsTableau(socket: Int): Boolean = socket >= 0
        private fun targetIsDeck(socket: Int): Boolean = socket == -1

    }

}