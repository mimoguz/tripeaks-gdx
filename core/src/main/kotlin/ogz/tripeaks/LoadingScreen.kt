package ogz.tripeaks

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.app.KtxScreen
import ktx.inject.Context
import ktx.scene2d.Scene2DSkin
import ogz.tripeaks.assets.FontAssets
import ogz.tripeaks.assets.TextureAssets
import ogz.tripeaks.assets.TextureAtlasAssets
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.assets.get
import ogz.tripeaks.assets.load
import ogz.tripeaks.graphics.CustomViewport
import ogz.tripeaks.models.PlayerStatistics
import ogz.tripeaks.screens.Constants
import ogz.tripeaks.screens.GameScreen
import ogz.tripeaks.services.MessageBox
import ogz.tripeaks.services.PersistenceService

class LoadingScreen(private val game: Main, private val context: Context) : KtxScreen {

    private val assets = context.inject<AssetManager>()
    private val messageBox = context.inject<MessageBox>()

    override fun show() {
        TextureAssets.values().forEach(assets::load)
        TextureAtlasAssets.values().forEach(assets::load)
        FontAssets.values().forEach(assets::load)
    }

    override fun render(delta: Float) {
        if (assets.isFinished) {
            Scene2DSkin.defaultSkin = UiSkin(
                assets[TextureAtlasAssets.Ui],
                assets[FontAssets.GamePixels],
                Constants.LIGHT_UI_TEXT,
                Constants.LIGHT_UI_EMPHASIS,
                "light"
            )
            switch()
        } else {
            assets.update()
            Gdx.graphics.requestRendering()
        }
    }

    private fun switch() {
        val gameScreen = GameScreen(context)
        game.addScreen(gameScreen)
        game.setScreen<GameScreen>()
        game.removeScreen<LoadingScreen>()
        Gdx.graphics.requestRendering()
        dispose()
    }
}