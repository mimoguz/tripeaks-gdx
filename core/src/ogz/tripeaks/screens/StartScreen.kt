package ogz.tripeaks.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import ktx.app.KtxScreen
import ktx.scene2d.Scene2DSkin
import ogz.tripeaks.*
import ogz.tripeaks.data.GamePreferences

class StartScreen(val game: Game) : KtxScreen {

    private val stage = Stage(
        IntegerScalingViewport(
            Const.CONTENT_WIDTH.toInt(),
            Const.CONTENT_HEIGHT.toInt(),
            OrthographicCamera()
        )
    )

    override fun dispose() {
        stage.dispose()
    }

    override fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }

    override fun show() {
        val preferences = GamePreferences().load()
        val skin = Scene2DSkin.defaultSkin
        val image =
            if (preferences.useDarkTheme) game.assets[TextureAssets.DarkTitle] else game.assets[TextureAssets.LightTitle]
        stage.actors.add(
            Image(image).apply {
                setSize(300f, 168f)
                setPosition(0f, 0f)
            },

            TextButton(
                game.assets[BundleAssets.Bundle].get("start"),
                skin,
                preferences.themeKey
            ).apply {
                pad(6f, 8f, 5f, 8f)
                addListener(object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {
                        game.addScreen(GameScreen(game, preferences))
                        game.setScreen<GameScreen>()
                        game.removeScreen<StartScreen>()
                        dispose()
                    }

                })
                width = 100f
                setPosition(
                    (Const.CONTENT_WIDTH - width) / 2f,
                    (Const.CONTENT_HEIGHT - height) / 2f
                )
            }
        )
        Gdx.input.inputProcessor = stage
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
    }
}
