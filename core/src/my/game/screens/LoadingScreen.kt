package my.game.screens

import com.badlogic.gdx.graphics.OrthographicCamera
import ktx.app.KtxScreen
import ktx.graphics.use
import my.game.*

class LoadingScreen(val game: Game): KtxScreen {
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
            game.addScreen(MainMenuScreen(game))
            game.setScreen<MainMenuScreen>()
            game.removeScreen<LoadingScreen>()
            dispose()
        }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }
}