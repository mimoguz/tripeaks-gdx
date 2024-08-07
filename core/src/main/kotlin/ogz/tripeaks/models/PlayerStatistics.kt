package ogz.tripeaks.models

import ktx.collections.GdxArray
import ktx.collections.gdxArrayOf

class PlayerStatistics(
    var played: Int,
    var won: Int,
    var layoutStatistics: GdxArray<LayoutStatistics>,
) {

    constructor() : this(played = 0, won = 0, layoutStatistics = gdxArrayOf())

    fun clone(
        played: Int = this.played,
        won: Int = this.won,
        layoutStatistics: GdxArray<LayoutStatistics> = this.layoutStatistics.clone()
    ): PlayerStatistics = PlayerStatistics(played, won, layoutStatistics)

    companion object {

        private fun (GdxArray<LayoutStatistics>).clone(): GdxArray<LayoutStatistics> {
            val result = GdxArray<LayoutStatistics>(this.size)
            this.forEach { result.add(it.clone) }
            return result
        }

    }

}