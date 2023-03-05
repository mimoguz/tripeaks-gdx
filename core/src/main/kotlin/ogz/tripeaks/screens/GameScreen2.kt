package ogz.tripeaks.screens

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.ray3k.stripe.PopTable
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.inject.Context
import ogz.tripeaks.game.AnimationViewPool
import ogz.tripeaks.game.CardViewPool
import ogz.tripeaks.game.GameView
import ogz.tripeaks.graphics.CustomViewport
import ogz.tripeaks.models.GameState
import ogz.tripeaks.screens.stage.StalledDialog
import ogz.tripeaks.screens.stage.WinDialog
import ogz.tripeaks.services.Message
import ogz.tripeaks.services.MessageBox
import ogz.tripeaks.services.PlayerStatisticsService
import ogz.tripeaks.services.Receiver
import ogz.tripeaks.services.SettingsService

class GameScreen2(private val context: Context) : KtxScreen {
    // ************************************************************************
    // INJECT
    // ************************************************************************
    private val assets = context.inject<AssetManager>()
    private val batch = context.inject<SpriteBatch>()
    private val cardViewPool = context.inject<CardViewPool>()
    private val animationViewPool = context.inject<AnimationViewPool>()
    private val messageBox = context.inject<MessageBox>()
    private val playerStatistics = context.inject<PlayerStatisticsService>()
    private val settings = context.inject<SettingsService>()
    private val stage = context.inject<Stage>()
    private val viewport = context.inject<CustomViewport>()

    // ************************************************************************
    // STATE, HELPERS AND VIEW
    // ************************************************************************
    private var game: GameState? = null
    private val stageUtils = StageUtils(assets, stage)
    private val touchHandler = TouchHandler(messageBox)
    private val ui = GameUi(settings.get().spriteSet, {}, {}, {}) // TODO
    private val view = GameView(game, viewport.worldWidth)
    private val renderer = RenderHelper2(batch, viewport, settings, ui, view)

    // ************************************************************************
    // RECEIVERS
    // ************************************************************************
    private val touchDownReceiver = Receiver(this::onTouchDown)
    private val touchUpReceiver = Receiver(this::onTouchUp)

    // receivers

    init {
        messageBox.register(touchDownReceiver)
        messageBox.register(touchUpReceiver)
    }

    // Methods --------------------------
    // overrides

    // ************************************************************************
    // RECEIVERS
    // ************************************************************************
    override fun dispose() {
        messageBox.unregister(touchDownReceiver)
        messageBox.unregister(touchUpReceiver)
        renderer.disposeSafely()
    }

    override fun render(delta: Float) {
        // Update *************************************************************
        viewport.apply()
        stage.viewport.apply()
        stage.act(delta)
        view.update(delta, settings.get())
        // Render *************************************************************
        renderer.render(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        viewport.update(width, height)
        stage.viewport.update(width, height, true)
        ui.resize(viewport.worldWidth, viewport.worldHeight)
        view.resize(viewport.worldWidth)
        renderer.update()
    }


    // ************************************************************************
    // MESSAGE HANDLERS
    // ************************************************************************
    private fun onTouchDown(message: Message.Companion.TouchDown) {
        game?.let { game ->
            val pos = Vector2(message.screenX.toFloat(), message.screenY.toFloat())
            viewport.unproject(pos)

            if (ui.handlePressed(pos.x, pos.y)) return

            val layout = game.gameLayout
            val x = pos.x.toInt() + (layout.numberOfColumns / 2) * Constants.CELL_WIDTH.toInt()
            val y = viewport.worldHeight.toInt() / 2 - Constants.VERTICAL_PADDING.toInt() - pos.y.toInt()
            val column = x / Constants.CELL_WIDTH.toInt()
            val row = y / Constants.CELL_HEIGHT.toInt()

            // Touch was outside of the tableau
            if (column < 0 || column >= layout.numberOfColumns || row < 0 || row >= layout.numberOfRows) {
                return
            }

            // Search the top-most open socket that occupies the cell.
            for (rowOffset in 0 downTo -1) {
                for (columnOffset in 0 downTo -1) {
                    val socket = layout.lookup(column + columnOffset, row + rowOffset)
                    if (socket != null && game.take(socket.index)) {
                        view.syncSocket(socket)
                        if (!checkIfWon(game)) checkIfStalled(game)
                        return
                    }
                }
            }
        }
    }

    private fun onTouchUp(message: Message.Companion.TouchUp) {
        val pos = Vector2(message.screenX.toFloat(), message.screenY.toFloat())
        viewport.unproject(pos)
        ui.handleReleased(pos.x, pos.y)
    }

    // dialog callbacks
    // gui actions
    // setup methods

    // helpers

    private fun checkIfWon(gameSt: GameState): Boolean {
        if (gameSt.won) {
            playerStatistics.addWin(gameSt.statistics)
            val dialog = WinDialog(
                settings.get().skin,
                gameSt.statistics,
                this::winDialogCallback
            )
            onDialogShown(dialog)
            dialog.show(stage)
            return true
        }
        return false
    }

    private fun checkIfStalled(gameSt: GameState): Boolean {
        if (gameSt.stalled) {
            playerStatistics.addWin(gameSt.statistics)
            val dialog = StalledDialog(
                settings.get().skin,
                gameSt.statistics,
                this::stalledDialogCallback
            )
            onDialogShown(dialog)
            dialog.show(stage)
            return true
        }
        return false
    }

    private fun onDialogShown(dialog: PopTable) {
        touchHandler.silent = true
        touchHandler.dialog = dialog
        renderer.blurred = true
    }

    private fun onMenuShown(menu: PopTable) {
        touchHandler.silent = true
        touchHandler.dialog = menu
        renderer.blurred = false
    }

    private fun onDialogHidden() {
        touchHandler.silent = false
        touchHandler.dialog = null
        renderer.blurred = false
    }
}