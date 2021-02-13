package my.game.screens

import com.badlogic.gdx.graphics.OrthographicCamera
import ktx.app.KtxScreen
import ktx.graphics.use
import ktx.scene2d.Scene2DSkin
import ktx.style.*
import my.game.*

class LoadingScreen(val game: Game) : KtxScreen {
    private val camera = OrthographicCamera()
    private val viewport = IntegerScalingViewport(320, 200, camera)

    override fun show() {
        for (asset in TextureAtlasAssets.values()) {
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
                    label {
                        font = game.assets[FontAssets.GameFont]
                        fontColor = skin["light"]
                    }
                    button {
                        up = skin["buttonUp"]
                        down = skin["buttonDown"]
                    }
                    textButton {
                        up = skin["buttonUp"]
                        down = skin["buttonDown"]
                        font = game.assets[FontAssets.GameFont]
                        fontColor = skin["light"]
                    }
                }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }
}