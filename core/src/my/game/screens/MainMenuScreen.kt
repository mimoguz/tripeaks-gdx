package my.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import ktx.app.KtxScreen
import ktx.scene2d.Scene2DSkin
import my.game.Constants
import my.game.Game
import my.game.TextureAssets
import my.game.get

class MainMenuScreen(val game: Game) : KtxScreen {

    private val stage = Stage(
            IntegerScalingViewport(
                    Constants.CONTENT_WIDTH.toInt(),
                    Constants.CONTENT_HEIGHT.toInt(),
                    OrthographicCamera()
            )
    )

    override fun render(delta: Float) {
        stage.act(delta)
        Gdx.gl.glClearColor(0.39f, 0.64f, 0.28f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.draw()
    }

    override fun show() {
        val skin = Scene2DSkin.defaultSkin
        stage.actors.add(
                Image(game.assets[TextureAssets.Title]).apply {
                    setSize(128f, 48f)
                    setPosition(Constants.CONTENT_WIDTH / 2f - 64f, Constants.CONTENT_HEIGHT / 2f + 8f)
                },

                TextButton("Start", skin).apply {
                    addListener(object : ChangeListener() {
                        override fun changed(event: ChangeEvent?, actor: Actor?) {
                            game.addScreen(GameScreen(game))
                            game.setScreen<GameScreen>()
                            game.removeScreen<MainMenuScreen>()
                            dispose()
                        }

                    })
                    width = 100f
                    setPosition(Constants.CONTENT_WIDTH / 2f - 50f, Constants.CONTENT_HEIGHT / 2f - height)
                }
        )
        Gdx.input.inputProcessor = stage
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
    }
}
