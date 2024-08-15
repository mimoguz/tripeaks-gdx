package ogz.tripeaks.screens

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.Logger
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.app.clearScreen
import ogz.tripeaks.Constants
import ogz.tripeaks.graphics.BlurredRenderer
import ogz.tripeaks.graphics.SimpleRenderer
import ogz.tripeaks.models.GameState
import ogz.tripeaks.services.SettingsService
import ogz.tripeaks.views.GameView
import kotlin.reflect.KClass

private val logger = Logger("states", Logger.DEBUG)

sealed interface GameScreenState {

    fun setGame(game: GameState?)
    fun handleTouchDown(worldX: Int, worldY: Int): TouchResult
    fun handleTouchUp(worldX: Int, worldY: Int)
    fun update()
    fun render(deltaTime: Float)
    fun reset(animated: Boolean)

}

class GameScreenSwitch : GameScreenState, Disposable {

    private var game: GameState? = null
    private val states: MutableMap<String, GameScreenState> = mutableMapOf()
    private var currentState: GameScreenState = NoopGameScreenState

    override fun setGame(game: GameState?) {
        this.game = game
        for (st in states.values) {
            st.setGame(game)
        }
    }

    override fun handleTouchDown(worldX: Int, worldY: Int): TouchResult {
        return currentState.handleTouchDown(worldX, worldY)
    }

    override fun handleTouchUp(worldX: Int, worldY: Int) {
        currentState.handleTouchUp(worldX, worldY)
    }

    override fun update() {
        currentState.update()
    }

    override fun render(deltaTime: Float) {
        currentState.render((deltaTime))
    }

    override fun reset(animated: Boolean) {
        currentState.reset(animated)
    }

    fun <T> addState(cls: KClass<T>, state: T) where T : GameScreenState {
        states[cls.simpleName ?: "anonymous"] = state
        state.setGame(game)
    }

    fun <T> switch(cls: KClass<T>) where T : GameScreenState {
        currentState = states[cls.simpleName] ?: NoopGameScreenState
    }

    override fun dispose() {
        for (st in states) {
            if (st is Disposable) {
                st.dispose()
            }
        }
        states.clear()
    }

}

data object NoopGameScreenState : GameScreenState {

    override fun setGame(game: GameState?) {}

    override fun handleTouchDown(worldX: Int, worldY: Int): TouchResult {
        logger.debug("Noop: touch down")
        return TouchResult.CONTINUE
    }

    override fun handleTouchUp(worldX: Int, worldY: Int) {
        logger.debug("Noop: touch up")
    }

    override fun update() {
        logger.debug("Noop: update")
    }

    override fun render(deltaTime: Float) {
        clearScreen(0f, 0f, 0f)
    }

    override fun reset(animated: Boolean) {
        logger.debug("Noop: reset")
    }

}

class PausedGameScreenState(
    private val batch: SpriteBatch,
    private val viewport: Viewport,
    private val settings: SettingsService,
    private val ui: GameUi,
    private val view: GameView,
    private val renderer: BlurredRenderer,
) : GameScreenState {

    private var game: GameState? = null
    private var t = Constants.BLUR_TIME

    override fun setGame(game: GameState?) {
        this.game = game
    }

    override fun handleTouchDown(worldX: Int, worldY: Int): TouchResult {
        logger.debug("Paused: touch down")
        return TouchResult.CONTINUE
    }

    override fun handleTouchUp(worldX: Int, worldY: Int) {
        logger.debug("Paused: touch up")
    }

    override fun update() {
        renderer.resize(viewport.worldWidth.toInt(), viewport.worldHeight.toInt())
        logger.debug("Paused: update")
    }

    override fun render(deltaTime: Float) {
        val currentSettings = settings.get()
        renderer.draw(
            (t / Constants.BLUR_TIME).coerceAtLeast(0.1f),
            batch,
            viewport,
            currentSettings.spriteSet.background,
        ) { batch ->
            view.draw(batch, currentSettings, viewport.worldWidth, viewport.worldHeight)
            batch.color = Color.WHITE
            ui.draw(batch, currentSettings.spriteSet)
        }
        t = (t + deltaTime).coerceAtMost(Constants.BLUR_TIME)
    }

    override fun reset(animated: Boolean) {
        t = if (animated) 0f else Constants.BLUR_TIME
    }

}

class PlayingGameScreenState(
    private val batch: SpriteBatch,
    private val viewport: Viewport,
    private val settings: SettingsService,
    private val ui: GameUi,
    private val view: GameView,
    private val renderer: SimpleRenderer
) : GameScreenState {

    private var game: GameState? = null

    override fun setGame(game: GameState?) {
        this.game = game
    }

    override fun handleTouchDown(worldX: Int, worldY: Int): TouchResult {
        val result = game?.let { game ->
            logger.debug("Playing: touch down")

            if (ui.handlePressed(worldX, worldY)) return TouchResult.UI

            val layout = game.gameLayout
            val x = worldX + (layout.numberOfColumns / 2) * Constants.CELL_WIDTH.toInt()
            val y =
                viewport.worldHeight.toInt() / 2 - Constants.VIEWPORT_VERTICAL_PADDING.toInt() - worldY
            val col = x / Constants.CELL_WIDTH.toInt()
            val row = y / Constants.CELL_HEIGHT.toInt()

            // Touch was outside of the tableau
            if (col < 0 || col >= layout.numberOfColumns || row < 0 || row >= layout.numberOfRows) {
                return TouchResult.CONTINUE
            }

            // Search the top-most open socket that occupies the cell.
            for (rowOffset in 0 downTo -1) {
                for (columnOffset in 0 downTo -1) {
                    val socket = layout.lookup(col + columnOffset, row + rowOffset)
                    if (socket != null && game.take(socket.index)) {
                        view.syncSocket(socket)
                        ui.undoButton.enabled = game.canUndo
                        if (game.won) return TouchResult.WON
                        if (game.stalled) return TouchResult.STALLED
                        return TouchResult.CONTINUE
                    }
                }
            }
            return TouchResult.CONTINUE
        }

        return result ?: TouchResult.CONTINUE
    }

    override fun handleTouchUp(worldX: Int, worldY: Int) {
        ui.handleReleased(worldX, worldY)
        logger.debug("Playing: touch up")
    }

    override fun update() {
        renderer.resize(viewport.worldWidth.toInt(), viewport.worldHeight.toInt())
    }

    override fun render(deltaTime: Float) {
        val currentSettings = settings.get()
        renderer.draw(
            batch,
            viewport,
            currentSettings.spriteSet.background,
        ) { batch ->
            view.draw(batch, currentSettings, viewport.worldWidth, viewport.worldHeight)
            batch.color = Color.WHITE
            ui.draw(batch, currentSettings.spriteSet)
        }
    }

    override fun reset(animated: Boolean) {
        // Pass
    }

}

class TransitionGameScreenState(
    private val batch: SpriteBatch,
    private val viewport: Viewport,
    private val settings: SettingsService,
    private val ui: GameUi,
    private val view: GameView,
    private val renderer: SimpleRenderer,
    private val callback: () -> Unit
) : GameScreenState {

    private var game: GameState? = null
    private var time = 0f
    private val vertexColor = Color()

    private val anim = settings.get().animationStrategy

    override fun setGame(game: GameState?) {
        this.game = game
    }

    override fun handleTouchDown(worldX: Int, worldY: Int): TouchResult {
        return TouchResult.CONTINUE
    }

    override fun handleTouchUp(worldX: Int, worldY: Int) {
        // Pass
    }

    override fun update() {
        // Pass
    }

    override fun render(deltaTime: Float) {
        time += deltaTime
        anim.screenTransition(time, vertexColor)

        val currentSettings = settings.get()
        val titleTexture = currentSettings.spriteSet.title
        val x = MathUtils.floor(titleTexture.width * -0.5f).toFloat()
        val y = MathUtils.floor(titleTexture.height * -0.5f).toFloat()

        renderer.draw(
            batch,
            viewport,
            currentSettings.spriteSet.background,
        ) { batch ->
            view.draw(batch, currentSettings, viewport.worldWidth, viewport.worldHeight)
            ui.draw(batch, currentSettings.spriteSet)

            // Draw title transition
            anim.resize(viewport.worldWidth, viewport.worldHeight)
            batch.shader = anim.shaderProgram
            batch.color = vertexColor
            batch.draw(
                titleTexture,
                x,
                y,
                titleTexture.width.toFloat(),
                titleTexture.height.toFloat()
            )
            batch.color = Color.WHITE
        }

        if (time >= Constants.DISSOLVE_TIME) {
            callback.invoke()
            time = Constants.DISSOLVE_TIME
        }
    }

    override fun reset(animated: Boolean) {
        // Pass
    }

}

enum class TouchResult {
    CONTINUE, UI, STALLED, WON
}