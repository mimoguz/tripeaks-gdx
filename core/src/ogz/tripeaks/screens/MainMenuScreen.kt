package ogz.tripeaks.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.ScreenUtils
import ktx.app.KtxScreen
import ktx.scene2d.Scene2DSkin
import ogz.tripeaks.*

class MainMenuScreen(val game: Game) : KtxScreen {

    private val stage = Stage(
            IntegerScalingViewport(
                    Const.CONTENT_WIDTH.toInt(),
                    Const.CONTENT_HEIGHT.toInt(),
                    OrthographicCamera()
            )
    )

    private var clearColor = Const.LIGHT_BACKGROUND

    override fun render(delta: Float) {
        stage.act(delta)
        ScreenUtils.clear(clearColor)
        stage.draw()
    }

    override fun show() {
        val skin = Scene2DSkin.defaultSkin
        val useDarkTheme = Util.readDarkThemePreference()
        if (useDarkTheme) {
            clearColor = Const.DARK_BACKGROUND
        }
        stage.actors.add(
                Image(game.assets[TextureAssets.Title]).apply {
                    setSize(118f, 50f)
                    setPosition((Const.CONTENT_WIDTH - width) / 2, Const.CONTENT_HEIGHT / 2f + 8f)
                },

                TextButton("Start", skin, if (useDarkTheme) "dark" else "light").apply {
                    addListener(object : ChangeListener() {
                        override fun changed(event: ChangeEvent?, actor: Actor?) {
                            game.addScreen(GameScreen(game))
                            game.setScreen<GameScreen>()
                            game.removeScreen<MainMenuScreen>()
                            dispose()
                        }

                    })
                    width = 100f
                    setPosition(Const.CONTENT_WIDTH / 2f - 50f, Const.CONTENT_HEIGHT / 2f - height - 8f)
                }
        )
        Gdx.input.inputProcessor = stage
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
    }
}
