package ogz.tripeaks.screens

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.app.KtxScreen
import ktx.ashley.configureEntity
import ktx.ashley.entity
import ktx.ashley.remove
import ktx.ashley.with
import ktx.collections.GdxMap
import ktx.collections.set
import ktx.collections.toGdxArray
import ktx.graphics.use
import ogz.tripeaks.*
import ogz.tripeaks.ecs.CardAnimationComponent
import ogz.tripeaks.ecs.CardAnimationRenderingSystem
import ogz.tripeaks.ecs.CardRenderComponent
import ogz.tripeaks.ecs.CardRenderingSystem
import ogz.tripeaks.game.GameState
import ogz.tripeaks.game.layout.Layout
import ogz.tripeaks.screens.controls.MyImageButton
import ogz.tripeaks.screens.dialogs.EndGameDialog
import ogz.tripeaks.screens.dialogs.GameMenu
import ogz.tripeaks.screens.dialogs.StalledDialog
import ogz.tripeaks.util.GamePreferences
import ogz.tripeaks.util.SkinData
import ogz.tripeaks.util.SpriteCollection
import kotlin.math.roundToInt

class GameScreen(
    private val game: Game,
    private val assets: AssetManager,
    private val viewport: Viewport,
    private val batch: Batch,
    private val preferences: GamePreferences,
    private val skinData: SkinData,
    private val layoutList: List<Layout>
) : KtxScreen, InputAdapter() {

    private val engine = PooledEngine()
    private val entities = (0 until 52).map { engine.entity() }.toGdxArray()
    private val sprites = SpriteCollection(assets, preferences.useDarkTheme)
    private val stage = Stage(viewport)
    private var menu: GameMenu? = null
    private var backgroundColor = preferences.backgroundColor
    private var stalled = false

    private val layouts = GdxMap<String, Layout>().also { map ->
        layoutList.forEach { map.set(it.tag, it) }
    }

    private var gameState = GameState.new(
        (0 until 52).shuffled().toIntArray(),
        preferences.startWithEmptyDiscard,
        layouts.get(preferences.layout, layoutList.first())
    )

    private var dealButton = makeDealButton()
    private var undoButton = makeUndoButton()
    private var menuButton = makeMenuButton()

    override fun show() {
        stageSetup()
        Gdx.input.inputProcessor = InputMultiplexer(stage, this)
        if (!load()) newGame()
    }

    override fun resume() {
        show()
        super.resume()
    }

    override fun pause() {
        if (!gameState.won) {
            save()
        } else {
            GameState.clearSave()
        }
        super.pause()
    }

    override fun render(delta: Float) {
        viewport.apply()
        stage.act(delta)
        ScreenUtils.clear(backgroundColor)
        engine.getSystem(CardRenderingSystem::class.java).allOpen = preferences.showAllCards

        batch.enableBlending()
        batch.use(viewport.camera) {
            engine.update(delta)
            renderDiscard()
            renderStack()
        }
        batch.disableBlending()

        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun dispose() {
        stage.dispose()
        super.dispose()
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (menu != null) {
            removeGameMenu()
            return true
        }

        val touchPoint = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
        viewport.unproject(touchPoint)

        val x = touchPoint.x -
                ((Const.CONTENT_WIDTH - Const.CELL_WIDTH * gameState.layout.numberOfColumns) * 0.5f + 0.5f)
                    .roundToInt()
                    .toFloat()
        val y = Const.CONTENT_HEIGHT - touchPoint.y - Const.VERTICAL_PADDING
        if (y < 0f || x < 0f) return false

        val column = (x / Const.CELL_WIDTH).toInt()
        val row = (y / Const.CELL_HEIGHT).toInt()

        if (column <= gameState.layout.numberOfColumns && row <= gameState.layout.numberOfRows) {
            for (rowOffset in 0 downTo -1) {
                for (columnOffset in 0 downTo -1) {
                    val socket = gameState.layout.lookup(column + columnOffset, row + rowOffset)
                    if (socket != null && gameState.isOpen(socket.index)) {
                        take(socket.index)
                        return true
                    }
                }
            }
        }

        return false
    }

    private fun newGame() {
        entities.forEach { it.removeAll() } // Clear components
        gameState =
            GameState.new(
                (0 until 52).shuffled().toIntArray(),
                preferences.startWithEmptyDiscard,
                layouts.get(preferences.layout, layoutList.first())
            )
        stalled = false
        initECS()
        updateUi()
    }

    private fun initECS() {
        entities.forEach { it.removeAll() } // Clear components
        gameState.sockets.withIndex().forEach { (index, socket) ->
            if (!socket.isEmpty) {
                engine.configureEntity(entities[socket.card]) {
                    with<CardRenderComponent> {
                        socketIndex = index
                        cardIndex = socket.card
                    }
                }
            }
        }
        engine.apply {
            removeAllSystems()
            addSystem(CardRenderingSystem(batch, sprites, gameState))
            addSystem(CardAnimationRenderingSystem(batch, sprites, gameState))
        }
    }

    private fun stageSetup() {
        stage.clear()
        stage.actors.addAll(dealButton, undoButton, menuButton)
    }

    private fun take(socketIndex: Int) {
        if (gameState.take(socketIndex)) {
            val card = gameState.sockets[socketIndex].card
            engine.configureEntity(entities[card]) {
                entity.remove<CardRenderComponent>()
                with<CardAnimationComponent> {
                    this.socketIndex = socketIndex
                    cardIndex = card
                    time = 0f
                }
            }
            updateUi()
        }
    }

    private fun undo() {
        gameState.undo()?.let { socketIndex ->
            if (socketIndex >= 0) {
                engine.configureEntity(entities[gameState.sockets[socketIndex].card]) {
                    entity.remove<CardAnimationComponent>()
                    with<CardRenderComponent> {
                        this.socketIndex = socketIndex
                        cardIndex = gameState.sockets[socketIndex].card
                    }
                }
            }
            updateUi()
        }
    }

    private fun deal() {
        if (gameState.deal()) updateUi()
    }

    private fun updateUi() {
        dealButton.isDisabled = !gameState.canDeal || gameState.won
        undoButton.isDisabled = !gameState.canUndo || gameState.won
        dealButton.touchable = if (dealButton.isDisabled) Touchable.disabled else Touchable.enabled
        undoButton.touchable = if (undoButton.isDisabled) Touchable.disabled else Touchable.enabled
        if (gameState.won) won()
        else if (gameState.stalled) stalled()
    }

    private fun stalled() {
        if (!stalled) {
            stalled = true
            val dialog =
                StalledDialog(
                    skinData,
                    preferences.themeKey,
                    assets[BundleAssets.Bundle]
                ).apply {
                    val remove = {
                        hide()
                        stage.actors.removeValue(this, true)
                    }
                    exitButton.setAction { Gdx.app.exit() }
                    newGameButton.setAction {
                        newGame()
                        remove()
                    }
                    undoButton.setAction {
                        undo()
                        remove()
                    }
                    returnButton.setAction { remove() }
                }
            dialog.show(stage)
        }
    }

    private fun won() {
        gameState.onWin()
        val dialog =
            EndGameDialog(
                skinData,
                preferences.themeKey,
                gameState.statistics.cardsRemovedFromStack,
                gameState.statistics.currentLongestChainLength,
                gameState.statistics.undoCount,
                assets[BundleAssets.Bundle]
            ).apply {
                exitButton.setAction { Gdx.app.exit() }
                newGameButton.setAction {
                    newGame()
                    hide()
                    stage.actors.removeValue(this, true)
                }
            }
        dialog.show(stage)
    }

    private fun renderStack() {
        if (gameState.stack.isEmpty) {
            batch.draw(
                sprites.emptyCollection,
                Const.STACK_POSITION.x + Const.SPRITE_X,
                Const.STACK_POSITION.y + Const.SPRITE_Y
            )
        } else if (preferences.showAllCards) renderStackOpen() else renderStackClosed()
    }

    private fun renderStackOpen() {
        for (i in 0 until gameState.stack.size - 1) {
            val x = Const.STACK_POSITION.x - i * 6f + Const.SPRITE_X
            batch.draw(sprites.card, x, Const.STACK_POSITION.y + Const.SPRITE_Y)
            val cardIndex = gameState.stack[i]
            val sprite = sprites.smallFaces[cardIndex]
            batch.draw(
                sprite,
                x + Const.SPRITE_WIDTH - sprite.width - 2f,
                Const.STACK_POSITION.y + Const.SPRITE_HEIGHT - sprite.height - 3f
            )
        }

        val cardIndex = gameState.stack.peek()
        val sprite = sprites.faces[cardIndex]
        val x = Const.STACK_POSITION.x - (gameState.stack.size - 1) * 6f + Const.SPRITE_X
        batch.draw(sprites.card, x, Const.STACK_POSITION.y + Const.SPRITE_Y)
        batch.draw(
            sprite,
            x + Const.FACE_X,
            Const.STACK_POSITION.y + Const.SPRITE_Y + Const.FACE_Y
        )
    }

    private fun renderStackClosed() {
        for (i in 0 until gameState.stack.size) {
            val x = Const.STACK_POSITION.x - i * 6f + Const.SPRITE_X
            batch.draw(sprites.card, x, Const.STACK_POSITION.y + Const.SPRITE_Y)
            batch.draw(sprites.back, x, Const.STACK_POSITION.y + Const.SPRITE_Y)
        }
    }

    private fun renderDiscard() {
        if (gameState.discard.isEmpty) {
            batch.draw(
                sprites.emptyCollection,
                Const.DISCARD_POSITION.x + Const.SPRITE_X,
                Const.DISCARD_POSITION.y + Const.SPRITE_Y
            )
        } else {
            batch.draw(
                sprites.card,
                Const.DISCARD_POSITION.x + Const.SPRITE_X,
                Const.DISCARD_POSITION.y + Const.SPRITE_Y
            )
            val cardIndex = gameState.discard.peek()
            batch.draw(
                sprites.faces[cardIndex],
                Const.DISCARD_POSITION.x + Const.FACE_X + Const.SPRITE_X,
                Const.DISCARD_POSITION.y + Const.FACE_Y + Const.SPRITE_Y
            )
        }
    }

    private fun showHideGameMenu() {
        if (menu != null) {
            removeGameMenu()
        } else {
            val newMenu = GameMenu(
                skinData,
                preferences,
                layoutList,
                assets[BundleAssets.Bundle],
                assets[TextureAtlasAssets.Ui],
                menuButton
            ).apply {
                newGameButton.setAction {
                    removeGameMenu()
                    newGame()
                }
                exitButton.setAction {
                    removeGameMenu()
                    Gdx.app.exit()
                }
                onThemeChanged = ::themeChanged
                onNewDialogShown = ::removeGameMenu
            }
            stage.addActor(newMenu)
            newMenu.isVisible = true
            menu = newMenu
        }
    }

    private fun removeGameMenu() {
        menu?.let {
            it.isVisible = false
            stage.actors.removeValue(it, true)
        }
        menu = null
    }

    private fun save() {
        gameState.save()
    }

    private fun load(): Boolean {
        val state = GameState.load(layouts)
        if (state == null) return false
        stalled = state.stalledOnce
        gameState = state
        initECS()
        updateUi()
        return true
    }

    private fun themeChanged(useDarkTheme: Boolean) {
        sprites.set(useDarkTheme)
        undoButton = makeUndoButton()
        dealButton = makeDealButton()
        menuButton = makeMenuButton()
        backgroundColor = preferences.backgroundColor
        stageSetup()
        updateUi()
    }

    private fun makeDealButton() = MyImageButton(
        assets[TextureAtlasAssets.Ui].createSprite(if (preferences.useDarkTheme) "deal_dark" else "deal"),
        skinData,
        preferences.themeKey,
    ).apply {
        setAction { if (menu != null) removeGameMenu() else deal() }
        setSize(Const.SPRITE_WIDTH, Const.SPRITE_HEIGHT)
        setPosition(
            Const.STACK_POSITION.x + Const.CELL_WIDTH * 2f + Const.SPRITE_X,
            Const.STACK_POSITION.y + Const.SPRITE_Y
        )
    }

    private fun makeUndoButton() = MyImageButton(
        assets[TextureAtlasAssets.Ui].createSprite(if (preferences.useDarkTheme) "undo_dark" else "undo"),
        skinData,
        preferences.themeKey
    ).apply {
        setAction { if (menu != null) removeGameMenu() else undo() }
        setSize(Const.SPRITE_WIDTH, Const.SPRITE_HEIGHT)
        setPosition(
            Const.DISCARD_POSITION.x - Const.CELL_WIDTH * 2f + Const.SPRITE_X,
            Const.DISCARD_POSITION.y + Const.SPRITE_Y
        )
    }

    private fun makeMenuButton() = MyImageButton(
        assets[TextureAtlasAssets.Ui].createSprite(if (preferences.useDarkTheme) "menu_dark" else "menu"),
        skinData,
        preferences.themeKey
    ).apply {
        setAction { showHideGameMenu() }
        setSize(Const.SPRITE_WIDTH, Const.SPRITE_WIDTH)
        setPosition(
            Const.STACK_POSITION.x + Const.CELL_WIDTH * 2f + Const.SPRITE_X,
            Const.CONTENT_HEIGHT - width - Const.VERTICAL_PADDING - Const.SPRITE_Y
        )
    }
}