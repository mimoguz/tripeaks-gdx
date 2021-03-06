package ogz.tripeaks.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.badlogic.gdx.utils.ScreenUtils
import ktx.app.KtxScreen
import ktx.graphics.use
import ktx.scene2d.Scene2DSkin
import ogz.tripeaks.Const
import ogz.tripeaks.Game
import ogz.tripeaks.TextureAtlasAssets
import ogz.tripeaks.get
import ogz.tripeaks.views.GameState

class GameScreen(val game: Game) : KtxScreen {
    private val camera = OrthographicCamera()
    private val viewport = IntegerScalingViewport(Const.CONTENT_WIDTH.toInt(), Const.CONTENT_HEIGHT.toInt(), camera)
    private val touchPoint3D = Vector3()
    private val touchPoint2D = Vector2()
    private val stage = Stage(viewport)
    private var paused = false
    private var backgroundColor = Const.LIGHT_BACKGROUND
    private var dark = false
        set(value) {
            if (value xor field) {
                backgroundColor = if (value) Const.DARK_BACKGROUND else Const.LIGHT_BACKGROUND
                state.setTheme(value)
                field = value
                setUi()
            }
        }
    private val state = GameState(game.assets, dark)
    private var dealButton = Button()
    private var undoButton = Button()

    override fun render(delta: Float) {
        camera.update()
        state.update(delta)

        if (state.won && !paused) {
            showEndGameDialog()
        }

        stage.act(delta)

        dealButton.isDisabled = !state.canDeal || state.won
        undoButton.isDisabled = !state.canUndo || state.won
        dealButton.touchable = if (dealButton.isDisabled) Touchable.disabled else Touchable.enabled
        undoButton.touchable = if (undoButton.isDisabled) Touchable.disabled else Touchable.enabled

        ScreenUtils.clear(backgroundColor)

        game.batch.enableBlending()
        game.batch.use(camera) { batch ->
            state.draw(batch)
        }
        game.batch.disableBlending()

        stage.draw()

        if (Gdx.input.justTouched() && !paused) {
            touchPoint3D.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            viewport.unproject(touchPoint3D)
            touchPoint2D.set(touchPoint3D.x, touchPoint3D.y)
            state.touch(touchPoint2D)
        }
    }

    override fun show() {
        if (!load()) {
            state.init()
        }
        if (readDarkThemePreference() != dark) {
            dark = !dark
        } else {
            setUi()
        }
    }

    private fun setUi() {
        stage.clear()

        dealButton = makeImageButton(
                "deal",
                Const.SPRITE_WIDTH, Const.SPRITE_HEIGHT,
                Const.STACK_POSITION.x + Const.CELL_WIDTH * 2f, Const.STACK_POSITION.y
        ) {
            state.deal()
            dark = !dark
        }

        undoButton = makeImageButton(
                "undo",
                Const.SPRITE_WIDTH, Const.SPRITE_HEIGHT,
                Const.DISCARD_POSITION.x - Const.CELL_WIDTH * 2f, Const.DISCARD_POSITION.y
        ) { state.undo() }

        stage.actors.addAll(dealButton, undoButton)

        // The new game button
        stage.actors.add(
                makeImageButton(
                        "new",
                        Const.SPRITE_WIDTH, Const.SPRITE_WIDTH,
                        Const.CONTENT_WIDTH - Const.SPRITE_WIDTH - 2f, Const.CONTENT_HEIGHT - Const.SPRITE_WIDTH - Const.VERTICAL_PADDING - 3f
                ) { showMenu() }
        )

        Gdx.input.inputProcessor = stage
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun dispose() {
        if (!state.won) {
            save()
        }
        super.dispose()
    }

    override fun pause() {
        if (!state.won) {
            save()
        } else {
            Gdx.app.getPreferences(Const.PREFERENCES_SAVE)
                    .putBoolean(Const.PREFERENCES_VALID, false)
        }
        super.pause()
    }

    override fun resume() {
        load()
        if (readDarkThemePreference() != dark) {
            dark = !dark
        } else {
            setUi()
        }
        super.resume()
    }

    private fun showEndGameDialog() {
        val dialog = object : Dialog("", Scene2DSkin.defaultSkin) {
            override fun result(obj: Any?) {
                paused = false
                if (obj == true) {
                    state.init()
                } else {
                    Gdx.app.exit()
                }
            }
        }.apply {
            buttonTable.pad(8f, 0f, 0f, 0f)
            buttonTable.defaults().width(60f)
            pad(16f, 24f, 16f, 24f)
            text("You won!")
            button("New game", true)
            button("Exit", false)
            key(Input.Keys.ENTER, true)
            key(Input.Keys.ESCAPE, false)
        }
        paused = true
        dialog.show(stage)
    }

    private fun showMenu() {
        val dialog = Dialog("", Scene2DSkin.defaultSkin)
        dialog.buttonTable.apply {
            pad(8f, 0f, 0f, 0f)
            defaults().width(120f)
            pad(16f, 24f, 16f, 24f)
            add(TextButton("Start a new game", Scene2DSkin.defaultSkin).apply {
                addListener(object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {
                        paused = false
                        state.init()
                        dialog.hide()
                    }
                })
            })
            row()
            add(TextButton("Toggle theme", Scene2DSkin.defaultSkin).apply {
                addListener(object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {
                        paused = false
                        toggleTheme()
                        dialog.hide()
                    }
                })
            })
            row()
            add(TextButton("Return to game", Scene2DSkin.defaultSkin).apply {
                addListener(object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {
                        dialog.hide()
                        paused = false
                    }
                })
            })
            row()
            row()
            add(TextButton("Exit game", Scene2DSkin.defaultSkin).apply {
                addListener(object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {
                        dialog.hide()
                        paused = false
                        Gdx.app.exit()
                    }
                })
            }).pad(20f, 0f, 0f, 0f)
        }
        paused = true
        dialog.show(stage)
    }

    private fun toggleTheme() {
        dark = !dark
        val preferences = Gdx.app.getPreferences(Const.PREFERENCES_GAME_PREFS)
        preferences.putBoolean(Const.PREFERENCES_DARK_THEME, dark)
        preferences.flush()
    }

    private fun readDarkThemePreference() = Gdx.app.getPreferences(Const.PREFERENCES_GAME_PREFS).getBoolean(Const.PREFERENCES_DARK_THEME, false)

    private fun save() {
        val preferences = Gdx.app.getPreferences(Const.PREFERENCES_SAVE)
        preferences.putBoolean(Const.PREFERENCES_VALID, true)
        state.save(preferences)
        preferences.flush()
    }

    private fun load(): Boolean {
        val preferences = Gdx.app.getPreferences(Const.PREFERENCES_SAVE)
        if (preferences.getBoolean(Const.PREFERENCES_VALID, false)) {
            return try {
                state.load(preferences)
                preferences.putBoolean(Const.PREFERENCES_VALID, false)
                preferences.flush()
                true
            } catch (_: Exception) {
                false
            }
        }
        return false
    }

    private fun makeImageButton(key: String, buttonWidth: Float, buttonHeight: Float, x: Float, y: Float, onChange: () -> Unit): Button {
        val button = if (dark) Button(Scene2DSkin.defaultSkin, "dark") else Button(Scene2DSkin.defaultSkin)
        val icon = if (dark) key + "Dark" else key
        button.apply {
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    onChange()
                }
            })
            setSize(buttonWidth, buttonHeight)
            setPosition(x, y)
            children.add(
                    Image(SpriteDrawable(game.assets[TextureAtlasAssets.Ui].createSprite(icon))).apply {
                        setPosition((buttonWidth - width) / 2f, (buttonHeight - height) / 2f)
                        touchable = Touchable.disabled
                    }
            )
        }
        return button
    }
}
