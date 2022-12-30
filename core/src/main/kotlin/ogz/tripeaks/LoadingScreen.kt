package ogz.tripeaks

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import ktx.app.KtxScreen
import ktx.inject.Context
import ogz.tripeaks.assets.FontAssets
import ogz.tripeaks.assets.TextureAssets
import ogz.tripeaks.assets.TextureAtlasAssets
import ogz.tripeaks.assets.load
import ogz.tripeaks.screens.GameScreen
import ogz.tripeaks.services.PlayerStatisticsService
import ogz.tripeaks.services.SettingsService

class LoadingScreen(private val game: Main, private val context: Context) : KtxScreen {

    private val assets = context.inject<AssetManager>()

    override fun show() {
        TextureAssets.values().forEach(assets::load)
        TextureAtlasAssets.values().forEach(assets::load)
        FontAssets.values().forEach(assets::load)
    }

    override fun render(delta: Float) {
        if (assets.isFinished) {
            switch()
        } else {
            assets.update()
            Gdx.graphics.requestRendering()
        }
    }

    private fun switch() {
        context.inject<PlayerStatisticsService>().initialize(context)
        context.inject<SettingsService>().initialize(context)
        val gameScreen = GameScreen(context)
        game.addScreen(gameScreen)
        game.setScreen<GameScreen>()
        game.removeScreen<LoadingScreen>()
        Gdx.graphics.requestRendering()
        dispose()
    }
}