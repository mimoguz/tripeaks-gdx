package ogz.tripeaks

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.inject.Context
import ogz.tripeaks.graphics.CustomViewport
import ogz.tripeaks.models.GameState
import ogz.tripeaks.models.PlayerStatistics
import ogz.tripeaks.models.Settings
import ogz.tripeaks.screens.Constants.MAX_WORLD_WIDTH
import ogz.tripeaks.screens.Constants.MIN_WORLD_WIDTH
import ogz.tripeaks.screens.Constants.WORLD_HEIGHT
import ogz.tripeaks.services.MessageBox
import ogz.tripeaks.services.Message.Companion as Msg
import ogz.tripeaks.services.PersistenceService
import ogz.tripeaks.services.PlayerStatisticsService
import ogz.tripeaks.services.SettingsService

class Main : KtxGame<KtxScreen>() {
    private val context = Context()
    private var playerStatistics = PlayerStatisticsService()
    private val settings = SettingsService()
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
        context.apply {
            bindSingleton(assets)
            bindSingleton(batch)
            bindSingleton(messageBox)
            bindSingleton(persistence)
            bindSingleton(uiStage)
            bindSingleton(viewport)
            bindSingleton(playerStatistics)
            bindSingleton(settings)
        }

        addScreen(LoadingScreen(this, context))
        setScreen<LoadingScreen>()
    }

    override fun pause() {
        settings.paused()
        playerStatistics.paused()
        super.pause()
    }

    override fun resume() {
        settings.resumed()
        playerStatistics.resumed()
        super.resume()
    }

    override fun dispose() {
        context.dispose()
        super.dispose()
    }
}

