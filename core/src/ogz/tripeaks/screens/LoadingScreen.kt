package ogz.tripeaks.screens

import com.badlogic.gdx.graphics.OrthographicCamera
import ktx.app.KtxScreen
import ktx.graphics.use
import ktx.scene2d.Scene2DSkin
import ktx.style.*
import ogz.tripeaks.*

class LoadingScreen(val game: Game) : KtxScreen {
    private val camera = OrthographicCamera()
    private val viewport = IntegerScalingViewport(320, 200, camera)

    override fun show() {
        for (asset in TextureAtlasAssets.values()) {
            game.assets.load(asset)
        }
        for (asset in TextureAssets.values()) {
            game.assets.load(asset)
        }
        for (asset in FontAssets.values()) {
            game.assets.load(asset)
        }
    }

    override fun render(delta: Float) {
        game.assets.update()
        camera.update()

        game.batch.use(camera) {
            game.font.draw(it, "...", -5f, 0f)
        }

        if (game.assets.isFinished) {
            setDefaultSkin()
            game.addScreen(MainMenuScreen(game))
            game.setScreen<MainMenuScreen>()
            game.removeScreen<LoadingScreen>()
            dispose()
        }
    }

    private fun setDefaultSkin() {
        Scene2DSkin.defaultSkin =
                skin(game.assets[TextureAtlasAssets.Ui]) { skin ->
                    color("light", 219f / 255f, 204f / 255f, 196f / 255f, 1f)
                    color("dark", 76f / 255f, 56f / 255f, 77f / 255f, 1f)
                    label {
                        font = game.assets[FontAssets.GameFont]
                        fontColor = skin["dark"]
                    }
                    button {
                        up = skin["buttonUp"]
                        down = skin["buttonDown"]
                        disabled = skin["buttonDisabled"]
                        pressedOffsetY = -1f
                    }
                    textButton {
                        up = skin["buttonUp"]
                        down = skin["buttonDown"]
                        disabled = skin["buttonDisabled"]
                        font = game.assets[FontAssets.GameFont]
                        fontColor = skin["dark"]
                        pressedOffsetY = -1f
                    }
                    window {
                        titleFont = game.assets[FontAssets.GameFont]
                        titleFontColor = skin["light"]
                        background = skin["window"]
                    }
                }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }
}