package ogz.tripeaks.services

import ktx.inject.Context
import ogz.tripeaks.models.GameStatistics
import ogz.tripeaks.models.LayoutStatistics
import ogz.tripeaks.models.PlayerStatistics

class PlayerStatisticsService {
    private var statistics: PlayerStatistics? = null
    private lateinit var persistence: PersistenceService

    fun initialize(context: Context) {
        persistence = context.inject()
        statistics = persistence.loadPlayerStatistics() ?: PlayerStatistics()
    }

    fun paused() {
        statistics?.let { persistence.savePlayerStatistics(it) }
    }

    fun resumed() {
        statistics = statistics ?: persistence.loadPlayerStatistics() ?: PlayerStatistics()
    }

    fun get(): PlayerStatistics = statistics!!.clone()

    fun addWin(gameStatistics: GameStatistics) {
        statistics?.apply {
            won += 1
            var stats = layoutStatistics.find { it.tag == gameStatistics.layoutTag }
            if (stats == null) {
                stats = LayoutStatistics(gameStatistics.layoutTag, 0, 0, 0)
                layoutStatistics.add(stats)
            }
            stats.played += 1
            stats.won += 1
            stats.longestChain = gameStatistics.longestChain
            layoutStatistics.sort { a, b -> b.won.compareTo(a.won) }
        }
    }

    fun updatePlayed() {
        statistics?.apply {  played += 1 }
    }
}