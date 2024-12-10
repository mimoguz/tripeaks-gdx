package ogz.tripeaks

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.inject.Context
import ogz.tripeaks.Constants.MAX_WORLD_WIDTH
import ogz.tripeaks.Constants.MIN_WORLD_WIDTH
import ogz.tripeaks.Constants.WORLD_HEIGHT
import ogz.tripeaks.assets.Text
import ogz.tripeaks.assets.TextLoader
import ogz.tripeaks.graphics.CustomViewport
import ogz.tripeaks.screens.LoadingScreen
import ogz.tripeaks.services.PersistenceService
import ogz.tripeaks.services.PlayerStatisticsService
import ogz.tripeaks.services.SettingsService

class Main(systemDarkMode: Boolean = false) : KtxGame<KtxScreen>() {

    private val context = Context()
    private val playerStatistics = PlayerStatisticsService()
    private val settings = SettingsService(systemDarkMode)

    override fun create() {
        val viewport = CustomViewport(
            MIN_WORLD_WIDTH.toInt(),
            MAX_WORLD_WIDTH.toInt(),
            WORLD_HEIGHT.toInt(),
            OrthographicCamera()
        )

        val uiStage = Stage(
            CustomViewport(
                MIN_WORLD_WIDTH.toInt(),
                MAX_WORLD_WIDTH.toInt(),
                WORLD_HEIGHT.toInt(),
                OrthographicCamera()
            )
        )

        val assets = AssetManager()
        assets.setLoader(Text::class.java, TextLoader(InternalFileHandleResolver()))

        context.apply {
            bindSingleton(uiStage)
            bindSingleton(viewport)
            bindSingleton(playerStatistics)
            bindSingleton(settings)
            bindSingleton(assets)
            bindSingleton(PersistenceService())
            bindSingleton(SpriteBatch())
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
