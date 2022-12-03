package ogz.tripeaks

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import ktx.app.KtxScreen
import ktx.scene2d.Scene2DSkin
import ogz.tripeaks.assets.FontAssets
import ogz.tripeaks.assets.TextureAssets
import ogz.tripeaks.assets.TextureAtlasAssets
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.assets.get
import ogz.tripeaks.assets.load

class LoadingScreen(private val game: Main) : KtxScreen {

    private val assets = AssetManager()

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
                Color(76f / 244f, 56f / 255f, 77f / 255f, 1f),
                Color(224 / 244f, 122f / 255f, 95f / 255f, 1f),
                "light"
            )
            game.addScreen(ProblemScreen(assets))
            game.setScreen<ProblemScreen>()
            game.removeScreen<LoadingScreen>()
            Gdx.graphics.requestRendering()
            dispose()
        } else {
            assets.update()
            Gdx.graphics.requestRendering()
        }
    }
}