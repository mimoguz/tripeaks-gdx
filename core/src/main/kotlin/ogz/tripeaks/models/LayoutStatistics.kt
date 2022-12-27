package ogz.tripeaks.models

class LayoutStatistics(var tag: String, var played: Int, var won: Int, var longestChain: Int) {
    val clone: LayoutStatistics
        get() = LayoutStatistics(tag, played, won, longestChain)
}