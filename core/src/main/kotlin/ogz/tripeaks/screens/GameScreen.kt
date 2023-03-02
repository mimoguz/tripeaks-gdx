package ogz.tripeaks.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Logger
import com.ray3k.stripe.PopTable
import com.ray3k.stripe.PopTable.PopTableStyle
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.collections.GdxArray
import ktx.collections.sortBy
import ktx.inject.Context
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.game.AnimationView
import ogz.tripeaks.game.AnimationViewPool
import ogz.tripeaks.game.CardView
import ogz.tripeaks.game.CardViewPool
import ogz.tripeaks.game.DiscardView
import ogz.tripeaks.game.StackView
import ogz.tripeaks.graphics.CustomViewport
import ogz.tripeaks.models.GameState
import ogz.tripeaks.models.layout.Socket
import ogz.tripeaks.screens.Constants.CARD_WIDTH
import ogz.tripeaks.screens.Constants.HORIZONTAL_PADDING
import ogz.tripeaks.screens.Constants.VERTICAL_PADDING
import ogz.tripeaks.screens.stage.StalledDialog
import ogz.tripeaks.screens.stage.StalledDialogResult
import ogz.tripeaks.screens.stage.WinDialog
import ogz.tripeaks.screens.stage.WinDialogResult
import ogz.tripeaks.services.MessageBox
import ogz.tripeaks.services.PersistenceService
import ogz.tripeaks.services.PlayerStatisticsService
import ogz.tripeaks.services.Receiver
import ogz.tripeaks.services.SettingsService
import ogz.tripeaks.ui.GameUi
import ogz.tripeaks.services.Message.Companion as Msg

class GameScreen(private val context: Context) : KtxScreen {
    private val logger = Logger(GameScreen::class.java.simpleName)

    private val assets = context.inject<AssetManager>()
    private val batch = context.inject<SpriteBatch>()
    private val cardViewPool = context.inject<CardViewPool>()
    private val animationViewPool = context.inject<AnimationViewPool>()
    private val messageBox = context.inject<MessageBox>()
    private val playerStatistics = context.inject<PlayerStatisticsService>()
    private val settings = context.inject<SettingsService>()
    private val uiStage = context.inject<Stage>()
    private val viewport = context.inject<CustomViewport>()

    private val ui = GameUi()
    private val cards = GdxArray<CardView>(false, 32)
    private val stack = StackView()
    private val discard = DiscardView()
    private val animations = GdxArray<AnimationView>(false, 16)
    private val finishedAnimations = GdxArray<AnimationView>(false, 16)

    private val renderHelper =
        RenderHelper(batch, viewport, settings, cards, animations, discard, stack)
    private val touchHandler = TouchHandler(messageBox)
    private val animationSetChangedReceiver =
        Receiver<Msg.AnimationSetChanged> { onAnimationSetChanged(it) }
    private val showAllChangedReceiver = Receiver<Msg.ShowAllChanged> { onShowAllChanged(it) }
    private val skinChangedReceiver = Receiver<Msg.SkinChanged> { onSkinChanged(it) }
    private val spriteSetChangedReceiver = Receiver<Msg.SpriteSetChanged> { onSpriteSetChanged(it) }
    private val touchDownReceiver = Receiver<Msg.TouchDown> { onTouchDown(it) }
    private val touchUpReceiver = Receiver<Msg.TouchUp> { onTouchUp(it) }
    private val stageUtils = StageUtils(assets, uiStage)

    private var undoButton = stageUtils.undoButton(settings.get().skin, this::undo)
    private var dealButton = stageUtils.dealButton(settings.get().skin, this::deal)

    private var play: GameState? = null

    init {
        logger.level = Logger.INFO

        messageBox.register(animationSetChangedReceiver)
        messageBox.register(showAllChangedReceiver)
        messageBox.register(skinChangedReceiver)
        messageBox.register(spriteSetChangedReceiver)
        messageBox.register(touchDownReceiver)
        messageBox.register(touchUpReceiver)

        renderHelper.ui = ui
    }

    override fun render(delta: Float) {
        val currentSettings = settings.get()
        viewport.apply()
        uiStage.viewport.apply()
        uiStage.act(delta)
        play?.let { game ->
            cards.forEach { it.update(game) }
            animations.forEach { anim ->
                if (anim.update(delta, currentSettings.animationStrategy)) {
                    finishedAnimations.add(anim)
                    animationViewPool.free(anim)
                }
            }
            animations.removeAll(finishedAnimations, true)
            finishedAnimations.clear()
        }
        renderHelper.render(delta)
        uiStage.draw()
    }

    override fun show() {
        super.show()
        val game = PersistenceService().loadGameState() ?: settings.getNewGame()
        play = game
        setupGame(game)
        playerStatistics.updatePlayed()
        setupStage(settings.get().skin)
        Gdx.input.inputProcessor = InputMultiplexer(uiStage, touchHandler)
    }

    override fun resume() {
        val game = play ?: PersistenceService().loadGameState() ?: context.inject()
        play = game
        setupGame(game)
        super.resume()
    }

    override fun pause() {
        play?.also { PersistenceService().saveGameState(it) }
        super.pause()
    }

    override fun dispose() {
        messageBox.unregister(animationSetChangedReceiver)
        messageBox.unregister(showAllChangedReceiver)
        messageBox.unregister(skinChangedReceiver)
        messageBox.unregister(spriteSetChangedReceiver)
        messageBox.unregister(touchDownReceiver)
        messageBox.unregister(touchUpReceiver)
        renderHelper.disposeSafely()
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        viewport.update(width, height)
        uiStage.viewport.update(width, height, true)
        stack.move(viewport.worldWidth)
        discard.move(viewport.worldWidth)
        ui.update(viewport.worldWidth, viewport.worldHeight)
        renderHelper.update()
    }

    private fun onTouchDown(message: Msg.TouchDown) {
        play?.let { gameState ->
            val pos = Vector2(message.screenX.toFloat(), message.screenY.toFloat())
            viewport.unproject(pos)

            if (ui.handlePressed(pos.x, pos.y)) return

            val layout = gameState.gameLayout
            val x = pos.x.toInt() + (layout.numberOfColumns / 2) * Constants.CELL_WIDTH.toInt()
            val y = viewport.worldHeight.toInt() / 2 - VERTICAL_PADDING.toInt() - pos.y.toInt()
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
                    if (socket != null && gameState.take(socket.index)) {
                        openSocket(gameState, socket)
                        if (!checkIfWon(gameState)) checkIfStalled(gameState)
                        return
                    }
                }
            }
        }
    }

    private fun checkIfWon(gameState: GameState): Boolean {
        if (gameState.won) {
            playerStatistics.addWin(gameState.statistics)
            val dialog = WinDialog(
                settings.get().skin,
                gameState.statistics,
                this::winDialogCallback
            )
            onDialogShown(dialog)
            dialog.show(uiStage)
            return true
        }
        return false
    }

    private fun checkIfStalled(gameState: GameState): Boolean {
        if (gameState.stalled) {
            playerStatistics.addWin(gameState.statistics)
            val dialog = StalledDialog(
                settings.get().skin,
                gameState.statistics,
                this::stalledDialogCallback
            )
            onDialogShown(dialog)
            dialog.show(uiStage)
            return true
        }
        return false
    }

    private fun openSocket(gameState: GameState, socket: Socket) {
        val card = gameState.socketState(socket.index).card
        val view = cards.find { it.card == card }
        val layout = gameState.gameLayout
        view?.apply {
            update(gameState)
            animations.add(animationViewPool.obtain().apply {
                set(card, view.x, view.y)
            })
        }
        val blocked = layout[socket.index].blocks
        for (s in blocked) {
            val blockedCard = gameState.socketState(s).card
            cards.find { it.card == blockedCard }?.update(gameState)
        }
        undoButton.enabled = gameState.canUndo
    }

    private fun winDialogCallback(result: WinDialogResult) {
        onDialogHidden()
        when (result) {
            WinDialogResult.NEW_GAME -> {
                val game = settings.getNewGame()
                play = game
                setupGame(game)
            }
            WinDialogResult.EXIT -> Gdx.app.exit()
        }
    }

    private fun stalledDialogCallback(result: StalledDialogResult) {
        onDialogHidden()
        when (result) {
            StalledDialogResult.NEW_GAME -> {
                play?.let { playerStatistics.addLose(it.statistics) }
                val game = settings.getNewGame()
                play = game
                setupGame(game)
            }
            StalledDialogResult.RETURN -> {}
        }
    }

    private fun setupGame(game: GameState) {
        cards.forEach(cardViewPool::free)
        cards.clear()

        animations.forEach(animationViewPool::free)
        animations.clear()

        for (i in 0 until game.gameLayout.numberOfSockets) {
            val socket = game.socket(i)
            val state = game.socketState(i)
            cards.add(cardViewPool.obtain().apply {
                put(state.card, socket, game.gameLayout)
            })
        }
        cards.sortBy { it.socket?.z ?: Int.MIN_VALUE }
        stack.stack = game.stack
        discard.discard = game.discard
        undoButton.enabled = game.canUndo
        dealButton.enabled = game.canDeal
    }

    private fun onTouchUp(message: Msg.TouchUp) {
        play?.let {
            val pos = Vector2(message.screenX.toFloat(), message.screenY.toFloat())
            viewport.unproject(pos)
            ui.handleReleased(pos.x, pos.y)
        }
    }

    private fun onSkinChanged(msg: Msg.SkinChanged) {
        // TODO: Move shader theme selection to a uniform
        setupStage(settings.get().skin)
    }

    private fun onSpriteSetChanged(msg: Msg.SpriteSetChanged) {
    }

    private fun onAnimationSetChanged(msg: Msg.AnimationSetChanged) {
    }

    private fun onShowAllChanged(msg: Msg.ShowAllChanged) {
    }

    private fun setupStage(skin: UiSkin) {
        uiStage.clear()

        val table = Table(skin).apply {
            pad(VERTICAL_PADDING, HORIZONTAL_PADDING, VERTICAL_PADDING, HORIZONTAL_PADDING)
        }
        table.debug = true
        table.setFillParent(true)
        uiStage.addActor(table)

        val empty = Label("", skin).apply {
            this.setSize(CARD_WIDTH, CARD_WIDTH)
        }

        table.add(empty).expandX().expandY().align(Align.topRight)

        val menuButton = stageUtils.mainMenuButton(
            skin,
            empty,
            this::onMenuShown,
            this::onMenuHidden,
            listOf(
                Pair("New Game", this::newGameAction),
                Pair("Menu item 2") { println("Menu item 2") },
                Pair("Menu item 3") { println("Menu item 3") }
            )
        )

        dealButton = stageUtils.dealButton(skin, this::deal)
        undoButton = stageUtils.undoButton(skin, this::undo)

        ui.clear()
        ui.add(menuButton)
        ui.add(dealButton)
        ui.add(undoButton)

        play?.let { game ->
            dealButton.enabled = game.canDeal
            undoButton.enabled = game.canUndo
        }
    }

    private fun printStatistics() {
        val stats = playerStatistics.get()
        logger.info("Played: ${stats.played}, Won: ${stats.won}")
    }

    /** Menu action for new game item. */
    private fun newGameAction() {
        play?.let { game ->
            if (game.won) {
                playerStatistics.addWin(game.statistics)
            } else if (game.wasPlayed) {
                playerStatistics.addLose(game.statistics)
            }
        }
        val game = settings.getNewGame()
        play = game
        setupGame(game)
    }

    private fun deal() {
        play?.let { gameState ->
            gameState.deal()
            dealButton.enabled = gameState.canDeal
            undoButton.enabled = gameState.canUndo
            checkIfStalled(gameState)
        }
    }

    private fun undo() {
        play?.let { gameState ->
            val target = gameState.undo()
            dealButton.enabled = gameState.canDeal
            undoButton.enabled = gameState.canUndo

            when (target) {
                Int.MIN_VALUE -> {}
                -1 -> {}
                else -> {
                    // TODO: This repeats on onTouch method too
                    val socket = gameState.gameLayout[target]
                    val card = gameState.socketState(socket.index).card
                    cards.find { it.card == card }?.update(gameState)
                    val blocked = gameState.gameLayout[socket.index].blocks
                    for (s in blocked) {
                        val blockedCard = gameState.socketState(s).card
                        cards.find { it.card == blockedCard }?.update(gameState)
                    }
                }
            }
        }
    }

    private fun openDialog() {
        val skin = settings.get().skin
        val dialog = PopTable(skin[PopTableStyle::class.java]).apply {
            add(Label("Dialog test", skin))
            pad(12f, 12f, 12f, 12f)
            isHideOnUnfocus = true
            isModal = true
            addListener {
                if (isHidden) {
                    onDialogHidden()
                    true
                } else {
                    false
                }
            }
        }

//        Dialog("", skin).also { dialog ->
//            dialog.contentTable.apply {
//                add(Label("UI test", skin))
//                pad(4f, 8f, 4f, 8f)
//            }
//            dialog.buttonTable.apply {
//                add(LabelButton(skin, "Close").apply {
//                    onClick {
//                        dialog.hide()
//                        this@GameScreen.touchHandler.slient = false
//                        this@GameScreen.renderHelper.blurred = false
//                    }
//                })
//            }
//        }
        onDialogShown(dialog)
        dialog.show(uiStage)
    }

    private fun onMenuHidden() {
        touchHandler.silent = false
        touchHandler.dialog = null
        renderHelper.blurred = false
    }

    private fun onDialogShown(dialog: PopTable) {
        touchHandler.silent = true
        touchHandler.dialog = dialog
        renderHelper.blurred = true
    }

    private fun onMenuShown(menu: PopTable) {
        touchHandler.silent = true
        touchHandler.dialog = menu
        renderHelper.blurred = true
    }

    private fun onDialogHidden() {
        touchHandler.silent = false
        touchHandler.dialog = null
        renderHelper.blurred = false
    }
}