package my.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import ktx.app.KtxScreen
import ktx.graphics.color
import ktx.graphics.use
import my.game.FontAssets
import my.game.Game
import my.game.IntegerScalingViewport
import my.game.get

class MainMenuScreen(val game: Game) : KtxScreen {
    private val camera = OrthographicCamera()
    private val viewport = IntegerScalingViewport(320, 200, camera)
    private val font = game.assets[FontAssets.GameFont].apply {
        color = color(219f / 255f, 204f / 255f, 196f / 255f)
    }

    override fun render(delta: Float) {
        camera.update()

        game.batch.use(camera) {
           font.draw(game.batch, "TriPeaks", -100f, 20f)
           font.draw(game.batch, "Tap anywhere to begin.", -100f, -20f)
        }

        if (Gdx.input.isTouched) {
            game.addScreen(GameScreen(game))
            game.setScreen<GameScreen>()
            game.removeScreen<MainMenuScreen>()
            dispose()
        }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }
}
