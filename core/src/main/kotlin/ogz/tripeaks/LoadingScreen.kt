package ogz.tripeaks

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import ktx.app.KtxScreen
import ktx.scene2d.Scene2DSkin
import ogz.tripeaks.assets.FontAssets
import ogz.tripeaks.assets.TextureAtlasAssets
import ogz.tripeaks.assets.UiSkinBase
import ogz.tripeaks.assets.get
import ogz.tripeaks.assets.load

class LoadingScreen(private val game: Main) : KtxScreen {

    private val assets = AssetManager()

    override fun show() {
        TextureAtlasAssets.values().forEach { assets.load(it) }
        FontAssets.values().forEach { assets.load(it) }
    }

    override fun render(delta: Float) {
        if (assets.isFinished) {
            Scene2DSkin.defaultSkin = UiSkinBase(
                assets[TextureAtlasAssets.Ui],
                assets[FontAssets.GamePixels],
                Color(76f / 244f, 56f / 255f, 77f / 255f, 1f),
                Color(224 / 244f, 122f / 255f, 95f / 255f, 1f),
                "light"
            )
            game.addScreen(DemoScreen(assets))
            game.setScreen<DemoScreen>()
            game.removeScreen<LoadingScreen>()
            dispose()
        } else {
            assets.update()
            Gdx.graphics.requestRendering()
        }
    }
}