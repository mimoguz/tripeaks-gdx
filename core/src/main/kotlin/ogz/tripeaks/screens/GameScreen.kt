package ogz.tripeaks.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.ray3k.stripe.PopTable
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.inject.Context
import ogz.tripeaks.game.GameView
import ogz.tripeaks.graphics.CustomViewport
import ogz.tripeaks.models.GameState
import ogz.tripeaks.screens.Constants.CELL_HEIGHT
import ogz.tripeaks.screens.Constants.CELL_WIDTH
import ogz.tripeaks.screens.Constants.HORIZONTAL_PADDING
import ogz.tripeaks.screens.Constants.VERTICAL_PADDING
import ogz.tripeaks.screens.stage.StalledDialog
import ogz.tripeaks.screens.stage.StalledDialogResult
import ogz.tripeaks.screens.stage.WinDialog
import ogz.tripeaks.screens.stage.WinDialogResult
import ogz.tripeaks.services.Message
import ogz.tripeaks.services.MessageBox
import ogz.tripeaks.services.PersistenceService
import ogz.tripeaks.services.PlayerStatisticsService
import ogz.tripeaks.services.Receiver
import ogz.tripeaks.services.SettingsService
import ogz.tripeaks.ui.Menu

class GameScreen(private val context: Context) : KtxScreen {
    // ************************************************************************
    // INJECT
    // ************************************************************************
    private val batch = context.inject<SpriteBatch>()
    private val messageBox = context.inject<MessageBox>()
    private val playerStatistics = context.inject<PlayerStatisticsService>()
    private val persistence = context.inject<PersistenceService>()
    private val settings = context.inject<SettingsService>()
    private val stage = context.inject<Stage>()
    private val viewport = context.inject<CustomViewport>()

    // ************************************************************************
    // STATE, HELPERS AND VIEW
    // ************************************************************************
    private var game: GameState? = null
    private val touchHandler = TouchHandler(messageBox)
    private val ui = GameUi(
        settings.get().spriteSet,
        this::dealAction,
        this::undoAction,
        this::showMenuAction
    )
    private val view = GameView(game, viewport.worldWidth)
    private val renderer = RenderHelper2(batch, viewport, settings, ui, view)
    private val menuActions = listOf(
        Pair("New Game", this::newGameAction),
        Pair("Exit", this::exitAction)
    )

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

    // ************************************************************************
    // OVERRIDES
    // ************************************************************************
    override fun dispose() {
        messageBox.unregister(touchDownReceiver)
        messageBox.unregister(touchUpReceiver)
        renderer.disposeSafely()
    }

    override fun pause() {
        game?.also { PersistenceService().saveGameState(it) }
        super.pause()
    }

    override fun render(delta: Float) {
        // Update
        viewport.apply()
        stage.viewport.apply()
        stage.act(delta)
        view.update(delta, settings.get())

        // Render
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

    override fun resume() {
        val gameSt = game ?: persistence.loadGameState() ?: context.inject()
        game = gameSt
        setupGame(gameSt)
        super.resume()
    }

    override fun show() {
        super.show()
        val gameSt = persistence.loadGameState() ?: settings.getNewGame()
        game = gameSt
        setupGame(gameSt)
        Gdx.input.inputProcessor = InputMultiplexer(stage, touchHandler)
    }

    // ************************************************************************
    // MESSAGE HANDLERS
    // ************************************************************************
    private fun onTouchDown(message: Message.Companion.TouchDown) {
        game?.let { game ->
            val pos = Vector2(message.screenX.toFloat(), message.screenY.toFloat())
            viewport.unproject(pos)

            if (ui.handlePressed(pos.x, pos.y)) {
                return
            }

            val layout = game.gameLayout
            val x = pos.x.toInt() + (layout.numberOfColumns / 2) * CELL_WIDTH.toInt()
            val y = viewport.worldHeight.toInt() / 2 - VERTICAL_PADDING.toInt() - pos.y.toInt()
            val col = x / CELL_WIDTH.toInt()
            val row = y / CELL_HEIGHT.toInt()

            // Touch was outside of the tableau
            if (col < 0 || col >= layout.numberOfColumns || row < 0 || row >= layout.numberOfRows) {
                return
            }

            // Search the top-most open socket that occupies the cell.
            for (rowOffset in 0 downTo -1) {
                for (columnOffset in 0 downTo -1) {
                    val socket = layout.lookup(col + columnOffset, row + rowOffset)
                    if (socket != null && game.take(socket.index)) {
                        view.syncSocket(socket)
                        if (!checkIfWon(game)) {
                            checkIfStalled(game)
                        }
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

    // ************************************************************************
    // CALLBACKS
    // ************************************************************************
    private fun stalledDialogCallback(result: StalledDialogResult) {
        onDialogHidden()
        when (result) {
            StalledDialogResult.NEW_GAME -> {
                game?.let { playerStatistics.addLose(it.statistics) }
                startNewGame()
            }
            StalledDialogResult.RETURN -> {}
        }
    }

    private fun winDialogCallback(result: WinDialogResult) {
        onDialogHidden()
        when (result) {
            WinDialogResult.NEW_GAME -> startNewGame()
            WinDialogResult.EXIT -> Gdx.app.exit()
        }
    }

    // ************************************************************************
    // ACTIONS
    // ************************************************************************
    private fun dealAction() {
        game?.let { game ->
            game.deal()
            ui.dealButton.enabled = game.canDeal
            ui.undoButton.enabled = game.canUndo
            checkIfStalled(game)
        }
    }

    private fun exitAction() {
        Gdx.app.exit()
    }

    private fun undoAction() {
        game?.let { game ->
            val target = game.undo()
            if (target >= 0) {
                view.syncSocket(game.gameLayout[target])
            }
            ui.dealButton.enabled = game.canDeal
            ui.undoButton.enabled = game.canUndo
        }
    }

    private fun newGameAction() {
        game?.let { game ->
            if (game.won) {
                playerStatistics.addWin(game.statistics)
            } else if (game.wasPlayed) {
                playerStatistics.addLose(game.statistics)
            }
        }
        startNewGame()
    }

    private fun showMenuAction() {
        val menu = Menu(settings.get().skin, menuActions)
        menu.apply {
            addListener {
                if (isHidden) {
                    onDialogHidden()
                    true
                } else {
                    false
                }
            }
        }
        onMenuShown(menu)
        menu.show(stage)
        menu.setPosition(
            stage.width - menu.width - HORIZONTAL_PADDING,
            stage.height - menu.height - ui.menuButton.height - 2f * VERTICAL_PADDING
        )
        println("-")
    }

    // ************************************************************************
    // HELPERS
    // ************************************************************************
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

    private fun setupGame(gameSt: GameState) {
        view.currentGame = gameSt
        ui.undoButton.enabled = gameSt.canUndo
        ui.dealButton.enabled = gameSt.canDeal
    }

    private fun startNewGame() {
        val gameSt = settings.getNewGame()
        game = gameSt
        setupGame(gameSt)
    }
}