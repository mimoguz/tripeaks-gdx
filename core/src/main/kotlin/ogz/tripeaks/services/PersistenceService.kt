package ogz.tripeaks.services

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.Logger
import com.badlogic.gdx.utils.SerializationException
import ktx.collections.GdxArray
import ogz.tripeaks.models.GameState
import ogz.tripeaks.models.LayoutStatistics
import ogz.tripeaks.models.PlayerStatistics
import ogz.tripeaks.models.layout.BasicLayout
import java.time.Instant

class PersistenceService {

    private val logger = Logger(PersistenceService::class.simpleName)

    init {
        logger.level = Logger.ERROR
    }

    fun saveGameState(current: GameState) {
        save(current, SAVE_FILE, SAVE_KEY)
    }

    fun loadGameState(): GameState? {
        return load(GameState::class.java, SAVE_FILE, SAVE_KEY)
    }

    fun savePlayerStatistics(current: PlayerStatistics) {
        save(current, STATISTICS_FILE, STATISTICS_KEY)
    }

    fun loadPlayerStatistics(): PlayerStatistics? {
        return load(PlayerStatistics::class.java, STATISTICS_FILE, STATISTICS_KEY)
            ?: loadLegacyPlayerStatistics()
    }

    fun saveSettings(current: SettingsData) {
        save(current, SETTINGS_FILE, SETTINGS_KEY)
    }

    fun loadSettings(): SettingsData? {
        return load(SettingsData::class.java, SETTINGS_FILE, SETTINGS_KEY) ?: loadLegacySettings()
    }

    private fun <T> save(current: T, file: String, key: String) {
        try {
            val prefs = Gdx.app.getPreferences(file)
            val json = Json().toJson(current)
            // Yes, I'm putting JSON in preferences.
            // Is there another easy and cross-platform way to write save data to correct place?
            prefs.putString(key, json)
            prefs.flush()
        } catch (e: Exception) {
            logger.error("${Instant.now()} - Error during saving $key (unhandled): ${e.message}")
            throw e
        }
    }

    private fun <T> load(cls: Class<T>, file: String, key: String): T? {
        val prefs = Gdx.app.getPreferences(file)
        try {
            val save = prefs.getString(key, null) ?: return null
            val json = Json()
            return json.fromJson(cls, save)
        } catch (e: Exception) {
            when (e) {
                is NullPointerException,
                is SerializationException -> {
                    logger.error("${Instant.now()} - Malformed save $key: ${e.message}")
                    return null
                }

                else -> {
                    logger.error("${Instant.now()} - Error during loading $key (unhandled): ${e.message}")
                    throw e
                }
            }
        }
    }

    private fun loadLegacyPlayerStatistics(): PlayerStatistics? {
        val prefs = Gdx.app.getPreferences("save")
        val layoutStats = GdxArray.of(LayoutStatistics::class.java)
        for (layout in Layouts.entries) {
            try {
                if (!prefs.contains("${layout.tag}_Played")) continue
                val played = prefs.getInteger("${layout.tag}_Played")
                val won = prefs.getInteger("${layout.tag}_Won")
                val longestChain = prefs.getInteger("${layout.tag}_LongestChain")
                layoutStats.add(LayoutStatistics(layout.tag, played, won, longestChain))
            } catch (e: Exception) {
                // Pass
            }
        }
        return if (layoutStats.isEmpty) null else PlayerStatistics(layoutStats)
    }

    private fun loadLegacySettings(): SettingsData? {
        val preferences = Gdx.app.getPreferences("gamePreferences")
        if (!preferences.contains("layout")) return null;

        val useDarkTheme = preferences.getBoolean("darkTheme", false)
        val showAllCards = preferences.getBoolean("showAllCards", false)
        val drawing =
            if (showAllCards) DrawingStrategies.BackVisible
            else DrawingStrategies.BackHidden
        val startWithEmptyDiscard = preferences.getBoolean("startWithEmptyDiscard", false)
        val layout = preferences.getString("layout", BasicLayout.TAG).toLayoutVariant()
        return SettingsData().copy(
            darkTheme = useDarkTheme,
            drawingStrategy = drawing,
            emptyDiscard = startWithEmptyDiscard,
            layout = layout,
        )
    }

    companion object {

        const val SETTINGS_FILE = "ogz.tripeaks.settings.preferences"
        const val SETTINGS_KEY = "settings"

        const val SAVE_FILE = "ogz.tripeaks.save.preferences"
        const val SAVE_KEY = "save"

        const val STATISTICS_FILE = "ogz.tripeaks.statistics.preferences"
        const val STATISTICS_KEY = "statistics"

    }

}

