package ogz.tripeaks.models

import ktx.collections.GdxArray
import ktx.collections.gdxArrayOf

class PlayerStatistics(
    var layoutStatistics: GdxArray<LayoutStatistics>,
) {

    constructor() : this(layoutStatistics = gdxArrayOf())

    fun clone(
        layoutStatistics: GdxArray<LayoutStatistics> = this.layoutStatistics.clone()
    ): PlayerStatistics = PlayerStatistics(layoutStatistics)

    companion object {

        private fun (GdxArray<LayoutStatistics>).clone(): GdxArray<LayoutStatistics> {
            val result = GdxArray<LayoutStatistics>(this.size)
            this.forEach { result.add(it.clone) }
            return result
        }

    }

}