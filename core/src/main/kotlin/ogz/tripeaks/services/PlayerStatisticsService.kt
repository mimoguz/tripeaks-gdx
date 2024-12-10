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
        // TODO: Saving on update now, not needed.
        // statistics?.let { persistence.savePlayerStatistics(it) }
    }

    fun resumed() {
        statistics = statistics ?: persistence.loadPlayerStatistics() ?: PlayerStatistics()
    }

    fun get(): PlayerStatistics = statistics!!.clone()

    fun addWin(gameStatistics: GameStatistics) {
        statistics?.let { playerStats ->
            val layoutStatistics = addGame(playerStats, gameStatistics)
            layoutStatistics.won += 1
        }
        save()
    }

    fun addLose(gameStatistics: GameStatistics) {
        statistics?.let { playerStats ->
            addGame(playerStats, gameStatistics)
        }
        save()
    }

    private fun addGame(
        playerStats: PlayerStatistics,
        gameStatistics: GameStatistics
    ): LayoutStatistics {
        var stats = playerStats.layoutStatistics.find { it.tag == gameStatistics.layoutTag }
        if (stats == null) {
            stats = LayoutStatistics(gameStatistics.layoutTag, 0, 0, 0, 0)
            playerStats.layoutStatistics.add(stats)
        }
        stats.played += 1
        stats.longestChain = gameStatistics.longestChain.coerceAtLeast(stats.longestChain)
        playerStats.layoutStatistics.sort { a, b -> b.played.compareTo(a.played) }
        return stats
    }

    private fun save() {
        statistics?.let { persistence.savePlayerStatistics(it) }
    }

}