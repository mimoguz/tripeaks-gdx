package ogz.tripeaks.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.ray3k.stripe.PopTable
import ktx.app.KtxScreen
import ktx.inject.Context
import ogz.tripeaks.Constants.HORIZONTAL_PADDING
import ogz.tripeaks.Constants.VIEWPORT_VERTICAL_PADDING
import ogz.tripeaks.assets.BundleAssets
import ogz.tripeaks.assets.get
import ogz.tripeaks.graphics.CustomViewport
import ogz.tripeaks.models.GameState
import ogz.tripeaks.screens.stage.OptionsDialog
import ogz.tripeaks.screens.stage.OptionsDialogResult
import ogz.tripeaks.screens.stage.StalledDialog
import ogz.tripeaks.screens.stage.StalledDialogResult
import ogz.tripeaks.screens.stage.StatisticsDialog
import ogz.tripeaks.screens.stage.WinDialog
import ogz.tripeaks.screens.stage.WinDialogResult
import ogz.tripeaks.services.PersistenceService
import ogz.tripeaks.services.PlayerStatisticsService
import ogz.tripeaks.services.SettingsService
import ogz.tripeaks.ui.Menu
import ogz.tripeaks.views.GameView

class GameScreen(private val context: Context) : KtxScreen, InputAdapter() {

    // ************************************************************************
    // INJECT
    // ************************************************************************
    private val batch = context.inject<SpriteBatch>()
    private val playerStatistics = context.inject<PlayerStatisticsService>()
    private val persistence = context.inject<PersistenceService>()
    private val settings = context.inject<SettingsService>()
    private val viewport = context.inject<CustomViewport>()
    private val assets = context.inject<AssetManager>()
    private val stage = context.inject<Stage>()
    private var menu: Menu? = null

    // ************************************************************************
    // STATE, HELPERS AND VIEW
    // ************************************************************************
    private var game: GameState? = null
    private val ui = GameUi(
        settings.get().spriteSet,
        this::onDeal,
        this::onUndo,
        this::onShowMenu
    )
    private val view = GameView(game, viewport.worldWidth)
    private val switch = GameScreenSwitch()
    private val menuActions = listOf(
        Pair(assets[BundleAssets.Bundle]["restart"], this::onRestartCurrentGame),
        Pair(assets[BundleAssets.Bundle]["newGame"], this::onNewGame),
        Pair(assets[BundleAssets.Bundle]["options"], this::onShowOptions),
        Pair(assets[BundleAssets.Bundle]["statistics"], this::onShowStatistics),
        Pair(assets[BundleAssets.Bundle]["exit"], this::onExit),
    )

    init {
        stage.clear()

        switch.apply {
            addState(
                PausedGameScreenState::class,
                PausedGameScreenState(batch, viewport, settings, ui, view, context.inject())
            )
            addState(
                PlayingGameScreenState::class,
                PlayingGameScreenState(batch, viewport, settings, ui, view, context.inject())
            )
            addState(
                TransitionGameScreenState::class,
                TransitionGameScreenState(batch, viewport, settings, ui, view, context.inject()) {
                    switch.switch(PlayingGameScreenState::class)
                }
            )
            switch(TransitionGameScreenState::class)
        }
    }

    override fun dispose() {
        switch.dispose()
    }

    override fun pause() {
        game?.also { PersistenceService().saveGameState(it) }
        super.pause()
    }

    override fun render(delta: Float) {
        viewport.apply()
        stage.viewport.apply()
        stage.act(delta)

        view.update(delta, settings.get())

        switch.render(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        viewport.update(width, height)
        stage.viewport.update(width, height, true)
        ui.resize(viewport.worldWidth, viewport.worldHeight)
        view.resize(viewport.worldWidth)
        switch.update()
    }

    override fun resume() {
        setupGame(game ?: persistence.loadGameState() ?: context.inject())
        super.resume()
    }

    override fun show() {
        super.show()
        setupGame(persistence.loadGameState() ?: settings.getNewGame())
        Gdx.input.inputProcessor = InputMultiplexer(stage, this)
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val pos = toWorld(screenX, screenY)
        when (switch.handleTouchDown(pos.x.toInt(), pos.y.toInt())) {
            TouchResult.WON -> onWon()
            TouchResult.STALLED -> onStalled()
            // TODO: Better way to handle menu visibility
            // This is a hack to be able to reopen menu if it was closed by touching
            // outside of it. It's not perfect, doesn't handle the cases if the player
            // presses undo or deal buttons.
            TouchResult.CONTINUE -> menu = null
            TouchResult.UI -> {}
        }
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val pos = toWorld(screenX, screenY)
        switch.handleTouchUp(pos.x.toInt(), pos.y.toInt())
        return true
    }

    // ************************************************************************
    // DIALOG CALLBACKS
    // ************************************************************************

    private fun optionsDialogCallback(result: OptionsDialogResult) {
        onDialogHidden()
        when (result) {
            is OptionsDialogResult.Types.Return -> {}
            is OptionsDialogResult.Types.Apply -> settings.update(result.settingsData)
        }
    }

    private fun stalledDialogCallback(result: StalledDialogResult) {
        when (result) {
            StalledDialogResult.NEW_GAME -> {
                addStat()
                startNewGame()
            }

            StalledDialogResult.RESTART -> {
                addStat()
                restartCurrentGame()
            }

            StalledDialogResult.RETURN -> {}
        }
    }

    private fun winDialogCallback(result: WinDialogResult) {
        when (result) {
            WinDialogResult.NEW_GAME -> {
                addStat()
                startNewGame()
            }

            WinDialogResult.RETURN -> game?.let { updateButtons(it) }
        }
    }

    // ************************************************************************
    // ACTIONS
    // ************************************************************************

    private fun onWon() {
        game?.also { game ->
            val dialog = WinDialog(
                settings.get().skin,
                assets,
                game.statistics,
                this::winDialogCallback
            )
            showDialog(dialog, true)
        }
    }

    private fun onStalled() {
        game?.also {
            val dialog = StalledDialog(
                settings.get().skin,
                assets,
                this::stalledDialogCallback
            )
            showDialog(dialog, true)
        }
    }

    private fun onDeal() {
        game?.let { game ->
            game.deal()
            updateButtons(game)
            if (game.stalled) onStalled()
        }
    }

    private fun onExit() {
        game?.let { persistence.saveGameState(it) }
        Gdx.app.exit()
    }

    private fun onShowOptions() {
        val optionsDialog = OptionsDialog(
            settings.get().skin,
            assets,
            settings.getData(),
            this::optionsDialogCallback
        )
        showDialog(optionsDialog)
    }

    private fun onShowStatistics() {
        val statsDialog = StatisticsDialog(
            settings.get().skin,
            assets,
            playerStatistics.get(),
        )
        showDialog(statsDialog)
    }

    private fun onUndo() {
        game?.let { game ->
            val target = game.undo()
            if (target >= 0) {
                view.syncSocket(game.gameLayout[target])
            }
            updateButtons(game)
        }
    }

    private fun onNewGame() {
        addStat()
        menu = null
        startNewGame()
    }

    private fun onRestartCurrentGame() {
        addStat()
        menu = null
        restartCurrentGame()
    }

    private fun onShowMenu() {
        val currentMenu = menu
        if (currentMenu != null) {
            currentMenu.hide()
            menu = null
            return
        }
        val newMenu = Menu(settings.get().skin, menuActions)
        menu = newMenu
        newMenu.show(stage)
        newMenu.setPosition(
            stage.width - newMenu.width - HORIZONTAL_PADDING,
            stage.height - newMenu.height - ui.menuButton.height - 2f * VIEWPORT_VERTICAL_PADDING + 2f
        )
    }

    private fun onDialogHidden() {
        switch.switch(PlayingGameScreenState::class)
    }

    // ************************************************************************
    // HELPERS
    // ************************************************************************

    private fun updateButtons(game: GameState) {
        ui.dealButton.enabled = game.canDeal && !game.won
        ui.undoButton.enabled = game.canUndo && !game.won
    }

    private fun toWorld(screenX: Int, screenY: Int): Vector2 {
        val pos = Vector2(screenX.toFloat(), screenY.toFloat())
        viewport.unproject(pos)
        return pos
    }

    private fun showDialog(dialog: PopTable, animated: Boolean = false) {
        dialog.addListener {
            if (dialog.isHidden) {
                onDialogHidden()
                true
            } else {
                false
            }
        }
        switch.switch(PausedGameScreenState::class)
        switch.reset(animated)
        menu = null
        dialog.show(stage)
    }

    private fun setupGame(game: GameState) {
        this.game = game
        view.currentGame = game
        switch.setGame(game)
        updateButtons(game)
    }

    private fun startNewGame() {
        setupGame(settings.getNewGame())
    }

    private fun restartCurrentGame() {
        game?.let { setupGame(it.t0Copy()) }
    }

    private fun addStat() {
        game?.let { game ->
            if (game.won) {
                playerStatistics.addWin(game.statistics)
            } else if (game.wasPlayed) {
                playerStatistics.addLose(game.statistics)
            }
        }
    }

}
