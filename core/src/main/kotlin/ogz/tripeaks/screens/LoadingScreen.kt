package ogz.tripeaks.screens

import com.badlogic.gdx.assets.AssetManager
import ktx.app.KtxScreen
import ktx.inject.Context
import ogz.tripeaks.Main
import ogz.tripeaks.assets.BundleAssets
import ogz.tripeaks.assets.FontAssets
import ogz.tripeaks.assets.ShaderSourceAssets
import ogz.tripeaks.assets.TextureAssets
import ogz.tripeaks.assets.TextureAtlasAssets
import ogz.tripeaks.assets.load
import ogz.tripeaks.graphics.BlurredRenderer
import ogz.tripeaks.graphics.SimpleRenderer
import ogz.tripeaks.services.PlayerStatisticsService
import ogz.tripeaks.services.SettingsService

class LoadingScreen(private val app: Main, private val context: Context) : KtxScreen {

    private val assets = context.inject<AssetManager>()

    override fun show() {
        TextureAssets.entries.forEach(assets::load)
        TextureAtlasAssets.entries.forEach(assets::load)
        FontAssets.entries.forEach(assets::load)
        BundleAssets.entries.forEach(assets::load)
        ShaderSourceAssets.entries.forEach(assets::load)
    }

    override fun render(delta: Float) {
        if (assets.isFinished) {
            switch()
        } else {
            assets.update()
        }
    }

    private fun switch() {
        context.inject<PlayerStatisticsService>().initialize(context)
        context.inject<SettingsService>().initialize(context)
        context.bindSingleton(SimpleRenderer())
        context.bindSingleton(BlurredRenderer(assets))
        val startScreen = StartScreen(app, context)
        app.addScreen(startScreen)
        app.setScreen<StartScreen>()
        app.removeScreen<LoadingScreen>()
        dispose()
    }

}