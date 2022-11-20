package ogz.tripeaks.services

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.Logger
import com.badlogic.gdx.utils.SerializationException
import ogz.tripeaks.models.GameState
import java.time.Instant

class PersistenceService {

    private val logger = Logger(PersistenceService::class.simpleName)

    init {
        logger.level = Logger.ERROR
    }

    fun saveGame(current: GameState) {
        try {
            val prefs = Gdx.app.getPreferences(SAVE_FILE)
            val json = Json()
            val serialized = json.toJson(current)
            // Yes, I'm putting JSON in preferences.
            // Is there another easy and cross-platform way to write save data to correct place?
            prefs.putString(SAVE_KEY, serialized)
            prefs.flush()
        } catch (e: Exception) {
            logger.error("${Instant.now()} - Save error (unhandled): ${e.message}")
            throw e
        }
    }

    fun loadGame(): GameState? {
        val prefs = Gdx.app.getPreferences(SAVE_FILE)
        try {
            val save = prefs.getString(SAVE_KEY, null) ?: return null
            val json = Json()
            return json.fromJson(GameState::class.java, save)
        } catch (e: Exception) {
            when (e) {
                is NullPointerException,
                is SerializationException -> {
                    logger.error("${Instant.now()} - Malformed save: ${e.message}")
                    return null
                }
                else -> {
                    logger.error("${Instant.now()} - Error during load (unhandled): ${e.message}")
                    throw e
                }
            }
        }
    }

    companion object {
        const val SAVE_FILE = "ogz.tripeaks.save.preferences"
        const val SAVE_KEY = "save"
    }
}

