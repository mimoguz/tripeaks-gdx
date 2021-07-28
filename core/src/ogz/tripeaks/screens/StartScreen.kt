package ogz.tripeaks.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.app.KtxScreen
import ktx.scene2d.Scene2DSkin
import ogz.tripeaks.*
import ogz.tripeaks.util.GamePreferences
import ogz.tripeaks.util.SkinData

class StartScreen(
    private val game: Game,
    private val assets: AssetManager,
    private val viewport: Viewport,
    private val batch: Batch,
    private val skinData: SkinData,
    private val preferences: GamePreferences
) :
    KtxScreen {

    private val stage = Stage(viewport)

    override fun show() {
        val skin = Scene2DSkin.defaultSkin
        val image =
            if (preferences.useDarkTheme) assets[TextureAssets.DarkTitle] else assets[TextureAssets.LightTitle]
        stage.actors.add(
            Image(image).apply {
                setSize(300f, 168f)
                setPosition(0f, 0f)
            },

            TextButton(assets[BundleAssets.Bundle].get("start"), skin, preferences.themeKey).apply {
                pad(skinData.buttonPadTop, 8f, skinData.buttonPadBottom, 8f)
                addListener(object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {
                        game.addScreen(GameScreen(game, assets, viewport, batch, preferences, skinData))
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

    override fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }

    override fun dispose() {
        stage.dispose()
    }
}
