package ogz.tripeaks.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.ScreenUtils
import ktx.app.KtxScreen
import ktx.graphics.use
import ktx.scene2d.Scene2DSkin
import ogz.tripeaks.*
import ogz.tripeaks.data.GamePreferences
import ogz.tripeaks.data.SkinData
import ogz.tripeaks.views.GameState
import java.util.*

class GameScreen(
    val game: Game,
    private var preferences: GamePreferences,
    private val skins: ObjectMap<String, SkinData>
) : KtxScreen {
    private val bundle = game.assets[BundleAssets.Bundle]
    private val camera = OrthographicCamera()
    private val viewport = IntegerScalingViewport(
        Const.CONTENT_WIDTH.toInt(),
        Const.CONTENT_HEIGHT.toInt(),
        camera
    )
    private val touchPoint = Vector3()
    private val stage = Stage(viewport)
    private var paused = false
    private var backgroundColor = preferences.backgroundColor
    private val state = GameState(game.assets, preferences.useDarkTheme, preferences.showAllCards)
    private var dealButton = Button()
    private var undoButton = Button()

    override fun dispose() {
        if (!state.won) {
            save()
        }
        stage.dispose()
        super.dispose()
    }

    override fun pause() {
        if (!state.won) {
            save()
        } else {
            Gdx.app.getPreferences(Const.SAVE_NAME)
                .putBoolean(Const.SAVE_VALID, false)
                .flush()
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
            touchPoint.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            viewport.unproject(touchPoint)
            state.touch(touchPoint.x, touchPoint.y)
        }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun resume() {
        show()
        super.resume()
    }

    override fun show() {
        if (!loadSavedGame()) {
            state.init()
        }
        preferences.load()
        setUi()
        state.setShowAllCards(preferences.showAllCards)
        state.setTheme(preferences.useDarkTheme)
    }

    private fun loadSavedGame(): Boolean {
        val save = Gdx.app.getPreferences(Const.SAVE_NAME)
        if (save.getBoolean(Const.SAVE_VALID, false)) {
            return try {
                state.load(save)
                save.putBoolean(Const.SAVE_VALID, false)
                save.flush()
                true
            } catch (_: Exception) {
                false
            }
        }
        return false
    }

    private fun makeImageButton(
        iconKey: String,
        buttonWidth: Float,
        buttonHeight: Float,
        x: Float,
        y: Float,
        onChange: () -> Unit
    ): Button {
        val button = Button(Scene2DSkin.defaultSkin, preferences.themeKey)
        val icon = if (preferences.useDarkTheme) iconKey + "Dark" else iconKey
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

    private fun makeDialogButton(
        text: String,
        onChange: () -> Unit
    ): TextButton {
        val skinData = skins[bundle.get("skinKey")]

        return TextButton(text, skinData.skin, preferences.themeKey).apply {
            pad(skinData.buttonPadTop, 8f, skinData.buttonPadBottom, 8f)
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    onChange()
                }
            })
        }
    }

    private fun makeDialogToggle(
        text: String,
        value: Boolean,
        onChange: (checked: Boolean) -> Unit
    ): CheckBox {
        val skinData = skins[bundle.get("skinKey")]

        return CheckBox(text, skinData.skin, preferences.themeKey).apply {
            isChecked = value
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    onChange(this@apply.isChecked)
                }
            })
            imageCell.padTop(skinData.checkBoxTopMargin)
        }
    }

    private fun save() {
        val preferences = Gdx.app.getPreferences(Const.SAVE_NAME)
        preferences.putBoolean(Const.SAVE_VALID, true)
        state.save(preferences)
        preferences.flush()
    }

    private fun showEndGameDialog() {
        val skinData = skins[bundle.get("skinKey")]

        val dialog = Dialog("", skinData.skin, preferences.themeKey)
        dialog.apply {
            buttonTable.pad(4f, 4f, 0f, 4f)
            buttonTable.defaults().width(110f)
            pad(16f, 24f, 16f, 24f)
            contentTable.apply {
                add(Label(bundle.get("won"), skinData.skin, preferences.themeKey))
                row()
                add(
                    Label(
                        bundle.format("fromStack", state.statKeeper.removedFromStack),
                        skinData.skin,
                        preferences.themeKey
                    )
                ).align(Align.left)
                row()
                add(
                    Label(
                        bundle.format("usedUndo", state.statKeeper.undoCount),
                        skinData.skin,
                        preferences.themeKey
                    )
                ).align(Align.left)
                row()
                add(
                    Label(
                        bundle.format("longestChain", state.statKeeper.longestChain),
                        skinData.skin,
                        preferences.themeKey
                    )
                ).align(Align.left)
            }
            buttonTable.add(makeDialogButton(bundle.get("newGameShort")) {
                dialog.hide()
                paused = false
                state.init()
            })
            buttonTable.add(makeDialogButton(bundle.get("exit")) { Gdx.app.exit() })
        }
        paused = true
        dialog.show(stage)
    }

    private fun showMenu() {
        val skinData = skins[bundle.get("skinKey")]

        val dialog = Dialog("", skinData.skin, preferences.themeKey)
        dialog.pad(4f, 12f, 11f, 12f)
        dialog.buttonTable.apply {
            defaults().width(180f).pad(0f)
            add(makeDialogButton(bundle.get("return")) {
                dialog.hide()
                paused = false
            })
            row()
            add(makeDialogButton(bundle.get("newGame")) {
                paused = false
                state.init()
                dialog.hide()
            })
            row()
            add(
                makeDialogToggle(
                    " " + bundle.get("darkTheme"),
                    preferences.useDarkTheme
                ) { value ->
                    setTheme(value)
                    paused = false
                    dialog.hide()
                }.apply { align(Align.left) })
            row()
            add(
                makeDialogToggle(
                    " " + bundle.get("showAll"),
                    preferences.showAllCards,
                ) { value ->
                    setShowAllCards(value)
                    paused = false
                    dialog.hide()
                }.apply { align(Align.left) })
            row()
            add(
                makeDialogButton(
                    bundle.get("exit"),
                ) { Gdx.app.exit() }.apply { width = 140f })
                .align(Align.center)
                .padTop(skins[bundle.get("skinKey")].exitButtonTopMargin)
        }
        paused = true
        dialog.show(stage)
    }

    private fun setShowAllCards(show: Boolean): Boolean {
        if (show != preferences.showAllCards) {
            preferences.showAllCards = show
            state.setShowAllCards(show)
            preferences.save()
            return true
        }
        return false
    }

    private fun setTheme(useDarkTheme: Boolean): Boolean {
        if (preferences.useDarkTheme != useDarkTheme) {
            preferences.useDarkTheme = useDarkTheme
            backgroundColor = preferences.backgroundColor
            state.setTheme(useDarkTheme)
            preferences.save()
            setUi()
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
