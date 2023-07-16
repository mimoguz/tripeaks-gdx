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
        statistics?.let { playerStats ->
            playerStats.won += 1
            val layoutStatistics = addGame(playerStats, gameStatistics)
            layoutStatistics.won += 1
        }
    }

    fun addLose(gameStatistics: GameStatistics) {
        statistics?.let { playerStats ->
            addGame(playerStats, gameStatistics)
        }
    }

    fun updatePlayed() {
        statistics?.apply { played += 1 }
    }

    private fun addGame(
        playerStats: PlayerStatistics,
        gameStatistics: GameStatistics
    ): LayoutStatistics {
        playerStats.played += 1
        var stats = playerStats.layoutStatistics.find { it.tag == gameStatistics.layoutTag }
        if (stats == null) {
            stats = LayoutStatistics(gameStatistics.layoutTag, 0, 0, 0)
            playerStats.layoutStatistics.add(stats)
        }
        stats.played += 1
        stats.longestChain = gameStatistics.longestChain
        playerStats.layoutStatistics.sort { a, b -> b.played.compareTo(a.played) }
        return stats
    }
}