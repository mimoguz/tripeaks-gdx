package ogz.tripeaks

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.inject.Context
import ogz.tripeaks.graphics.CustomViewport
import ogz.tripeaks.models.PlayerStatistics
import ogz.tripeaks.screens.Constants
import ogz.tripeaks.screens.Constants.MAX_WORLD_WIDTH
import ogz.tripeaks.screens.Constants.MIN_WORLD_WIDTH
import ogz.tripeaks.screens.Constants.WORLD_HEIGHT
import ogz.tripeaks.services.MessageBox
import ogz.tripeaks.services.PersistenceService

class Main : KtxGame<KtxScreen>() {
    private val context = Context()
    private var playerStatistics: PlayerStatistics? = null
    private val persistence = PersistenceService()
    private val messageBox = MessageBox()
    private val assets = AssetManager()
    private lateinit var batch: SpriteBatch
    private lateinit var viewport: CustomViewport
    private lateinit var uiStage: Stage

    override fun create() {
        // Gdx.graphics.isContinuousRendering = false

        batch = SpriteBatch()
        viewport = CustomViewport(MIN_WORLD_WIDTH, MAX_WORLD_WIDTH, WORLD_HEIGHT, OrthographicCamera())
        uiStage = Stage(CustomViewport(MIN_WORLD_WIDTH, MAX_WORLD_WIDTH, WORLD_HEIGHT, OrthographicCamera()))
        // TODO: Preferences as a singleton and a receiver, and new game supplier that depends on it
        context.apply {
            bindSingleton(assets)
            bindSingleton(batch)
            bindSingleton(messageBox)
            bindSingleton(persistence)
            bindSingleton(uiStage)
            bindSingleton(viewport)
        }

        playerStatistics = persistence.loadPlayerStatistics() ?: PlayerStatistics()
        playerStatistics?.register(messageBox)

        addScreen(LoadingScreen(this, context))
        setScreen<LoadingScreen>()
    }

    override fun pause() {
        playerStatistics?.let { persistence.savePlayerStatistics(it) }
        playerStatistics?.unregister()
        super.pause()
    }

    override fun resume() {
        playerStatistics = playerStatistics ?: persistence.loadPlayerStatistics() ?: PlayerStatistics()
        playerStatistics?.register(messageBox)
        super.resume()
    }

    override fun dispose() {
        context.dispose()
        super.dispose()
    }
}

