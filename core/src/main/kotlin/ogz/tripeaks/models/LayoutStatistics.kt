package ogz.tripeaks.models

import ogz.tripeaks.models.layout.BasicLayout

class LayoutStatistics(
    var tag: String,
    var played: Int,
    var won: Int,
    var longestChain: Int,
    // Planned for v1.2
    var highScore: Int,
) {

    constructor() : this(
        tag = BasicLayout.TAG,
        played = 0,
        won = 0,
        longestChain = 0,
        highScore = 0
    )

    val clone: LayoutStatistics
        get() = LayoutStatistics(tag, played, won, longestChain, highScore)

}