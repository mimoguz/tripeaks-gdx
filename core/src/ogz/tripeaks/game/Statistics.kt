package ogz.tripeaks.game

import ktx.collections.GdxMap

// TODO: Finish class
class Statistics(
        longestChain: Int = 0,
        currentChain: Int = 0,
        removedFromStack: Int = 0,
        undos: Int = 0,
        layoutTag: String,
) {
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

    var gamesPlayed = 0
        private set

    var gamesWon = 0
        private set

    val layoutStatistics = GdxMap<String, LayoutStatistics>()

    class LayoutStatistics {}
}