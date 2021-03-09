package ogz.tripeaks.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.ScreenUtils
import ktx.app.KtxScreen
import ktx.graphics.use
import ktx.scene2d.Scene2DSkin
import ogz.tripeaks.*
import ogz.tripeaks.views.GameState
import kotlin.concurrent.fixedRateTimer

class GameScreen(val game: Game) : KtxScreen {
    private val camera = OrthographicCamera()
    private val viewport = IntegerScalingViewport(Const.CONTENT_WIDTH.toInt(), Const.CONTENT_HEIGHT.toInt(), camera)
    private val touchPoint3D = Vector3()
    private val touchPoint2D = Vector2()
    private val stage = Stage(viewport)
    private var paused = false
    private var backgroundColor = Const.LIGHT_BACKGROUND
    private var useDarkTheme = false
        set(value) {
            if (value xor field) {
                backgroundColor = if (value) Const.DARK_BACKGROUND else Const.LIGHT_BACKGROUND
                state.setTheme(value)
                field = value
                setUi()
            }
        }
    private val state = GameState(game.assets, useDarkTheme)
    private var dealButton = Button()
    private var undoButton = Button()

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
            Gdx.app.getPreferences(Const.SAVE_NAME)
                    .putBoolean(Const.SAVE_VALID, false)
        }
        super.pause()
    }

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

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun resume() {
        load()
        if (!setTheme(Util.readDarkThemePreference())) {
            setUi()
        }
        super.resume()
    }

    override fun show() {
        if (!load()) {
            state.init()
        }
        if (!setTheme(Util.readDarkThemePreference())) {
            setUi()
        }
    }

    private fun load(): Boolean {
        val preferences = Gdx.app.getPreferences(Const.SAVE_NAME)
        if (preferences.getBoolean(Const.SAVE_VALID, false)) {
            return try {
                state.load(preferences)
                preferences.putBoolean(Const.SAVE_VALID, false)
                preferences.flush()
                true
            } catch (_: Exception) {
                false
            }
        }
        return false
    }

    private fun makeImageButton(iconKey: String, buttonWidth: Float, buttonHeight: Float, x: Float, y: Float, onChange: () -> Unit): Button {
        val button = Button(Scene2DSkin.defaultSkin, if (useDarkTheme) "dark" else "light")
        val icon = if (useDarkTheme) iconKey + "Dark" else iconKey
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

    private fun makeDialogButton(text: String, onChange: () -> Unit): TextButton =
            TextButton(text, Scene2DSkin.defaultSkin, if (useDarkTheme) "dark" else "light").apply {
                pad(4f, 8f, 5f, 8f)
                addListener(object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {
                        onChange()
                    }
                })
            }

    private fun makeDialogToggle(text: String, value: Boolean, onChange: (checked: Boolean) -> Unit): TextButton =
            CheckBox(text, Scene2DSkin.defaultSkin, if (useDarkTheme) "dark" else "light").apply {
                isChecked = value
                addListener(object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {
                        onChange(this@apply.isChecked)
                    }
                })
            }

    private fun save() {
        val preferences = Gdx.app.getPreferences(Const.SAVE_NAME)
        preferences.putBoolean(Const.SAVE_VALID, true)
        state.save(preferences)
        preferences.flush()
    }

    private fun showEndGameDialog() {
        val dialog = Dialog("", Scene2DSkin.defaultSkin, if (useDarkTheme) "dark" else "light")
        dialog.apply {
            val message =
                    "You removed ${state.statKeeper.removedFromStack} card${if (state.statKeeper.removedFromStack == 1) "" else "s"} from the stack.\n\n" +
                            "You used undo ${state.statKeeper.undoCount} time${if (state.statKeeper.undoCount == 1) "" else "s"}.\n\n" +
                            "Your longest chain was ${state.statKeeper.longestChain} card${if (state.statKeeper.longestChain == 1) "" else "s"} long."

            buttonTable.pad(0f, 4f, 0f, 4f)
            buttonTable.defaults().width(110f)
            pad(16f, 24f, 16f, 24f)
            contentTable.apply {
                add(Label("You won!", Scene2DSkin.defaultSkin, if (useDarkTheme) "dark" else "light"))
                row()
                add(Label(message, Scene2DSkin.defaultSkin, if (useDarkTheme) "dark" else "light"))
            }
            buttonTable.add(makeDialogButton("Start new game") {
                dialog.hide()
                paused = false
                state.init()
            })
            buttonTable.add(makeDialogButton("Exit game") { Gdx.app.exit() })
        }
        paused = true
        dialog.show(stage)
    }

    private fun showMenu() {
        val dialog = Dialog("", Scene2DSkin.defaultSkin, if (useDarkTheme) "dark" else "light")
        dialog.pad(3f, 12f, 10f, 12f)
        dialog.buttonTable.apply {
            defaults().width(180f).pad(1f)
            //defaults().width(100f).height(34f)
            add(makeDialogButton("Return to game") {
                dialog.hide()
                paused = false
            })
            row()
            add(makeDialogButton("Start new game") {
                paused = false
                state.init()
                dialog.hide()
            })
            row()
            add(makeDialogToggle(" Use dark theme", useDarkTheme) { value ->
                paused = false
                setTheme(value)
                dialog.hide()
            }.apply { align(Align.left) })
            row()
            add(makeDialogToggle(" Put unreachable cards open", useDarkTheme) { value ->
                paused = false
                setTheme(value)
                dialog.hide()
            }.apply { align(Align.left) })
            row()
            add(makeDialogButton("Exit game") { Gdx.app.exit() }.apply { width = 140f })
                    .align(Align.center)
                    .padTop(18f)
        }
        paused = true
        dialog.show(stage)
    }

    private fun setTheme(useDarkTheme: Boolean): Boolean {
        if (useDarkTheme xor this.useDarkTheme) {
            this.useDarkTheme = useDarkTheme
            val preferences = Gdx.app.getPreferences(Const.PREFERENCES_NAME)
            preferences.putBoolean(Const.PREFERENCES_DARK_THEME, this.useDarkTheme)
            preferences.flush()
            return true
        }
        return false
    }

    private fun setUi() {
        stage.clear()

        dealButton = makeImageButton(
                "deal",
                Const.SPRITE_WIDTH,
                Const.SPRITE_HEIGHT,
                Const.STACK_POSITION.x + Const.CELL_WIDTH * 2f,
                Const.STACK_POSITION.y
        ) { state.deal() }

        undoButton = makeImageButton(
                "undo",
                Const.SPRITE_WIDTH,
                Const.SPRITE_HEIGHT,
                Const.DISCARD_POSITION.x - Const.CELL_WIDTH * 2f,
                Const.DISCARD_POSITION.y
        ) { state.undo() }

        stage.actors.addAll(dealButton, undoButton)

        // The new game button
        stage.actors.add(
                makeImageButton(
                        "new",
                        Const.SPRITE_WIDTH,
                        Const.SPRITE_WIDTH,
                        Const.CONTENT_WIDTH - Const.SPRITE_WIDTH - 2f,
                        Const.CONTENT_HEIGHT - Const.SPRITE_WIDTH - Const.VERTICAL_PADDING - 3f
                ) { showMenu() }
        )

        Gdx.input.inputProcessor = stage
    }
}
