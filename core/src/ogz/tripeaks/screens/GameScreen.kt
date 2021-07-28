package ogz.tripeaks.screens

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.app.KtxScreen
import ktx.ashley.*
import ktx.collections.GdxMap
import ktx.collections.set
import ktx.collections.toGdxArray
import ktx.graphics.use
import ogz.tripeaks.*
import ogz.tripeaks.ecs.*
import ogz.tripeaks.game.BasicLayout
import ogz.tripeaks.game.GameState
import ogz.tripeaks.game.Layout
import ogz.tripeaks.util.*

class GameScreen(
    private val game: Game,
    private val assets: AssetManager,
    private val viewport: Viewport,
    private val batch: Batch,
    private val preferences: GamePreferences,
    private val skinData: SkinData
) : KtxScreen {

    private val engine = PooledEngine()
    private val entities = (0 until 52).map { engine.entity() }.toGdxArray()
    private val layouts = GdxMap<String, Layout>().apply {
        val basicLayout = BasicLayout()
        set(BasicLayout.TAG, basicLayout)
    }
    private var gameState =
        GameState(
            (0 until 52).shuffled().toIntArray(),
            preferences.startWithEmptyDiscard,
            layouts[BasicLayout.TAG]
        )
    private val sprites = SpriteCollection(assets, preferences.useDarkTheme)
    private val stage = Stage(viewport)
    private val renderingSystem = CardRenderingSystem(batch, sprites, gameState)
    private val animationRenderingSystem = CardAnimationRenderingSystem(batch, sprites, gameState)

    private val dealButton = ImageButton(
        skinData.skin,
        preferences.themeKey,
        Const.SPRITE_WIDTH,
        Const.SPRITE_HEIGHT,
        assets[TextureAtlasAssets.Ui].createSprite(if (preferences.useDarkTheme) "deal_dark" else "deal")
    ) { deal() }

    private val undoButton = ImageButton(
        skinData.skin,
        preferences.themeKey,
        Const.SPRITE_WIDTH,
        Const.SPRITE_HEIGHT,
        assets[TextureAtlasAssets.Ui].createSprite(if (preferences.useDarkTheme) "undo_dark" else "undo")
    ) { undo() }

    private val menuButton = ImageButton(
        skinData.skin,
        preferences.themeKey,
        Const.SPRITE_WIDTH,
        Const.SPRITE_WIDTH,
        assets[TextureAtlasAssets.Ui].createSprite(if (preferences.useDarkTheme) "menu_dark" else "menu")
    ) {}

    private var backgroundColor = preferences.backgroundColor

    override fun show() {
        engine.addSystem(renderingSystem)
        engine.addSystem(animationRenderingSystem)
        initUi()
        newGame()
    }

    override fun render(delta: Float) {
        if (Gdx.input.justTouched()) onTouch()
        viewport.apply()
        stage.act(delta)
        ScreenUtils.clear(backgroundColor)

        batch.enableBlending()
        batch.use(viewport.camera) {
            engine.update(delta)
            renderDiscard()
            renderStack()
        }
        batch.disableBlending()

        stage.draw()
    }

    private fun newGame() {
        entities.forEach { it.removeAll() } // Clear components
        gameState =
            GameState(
                (0 until 52).shuffled().toIntArray(),
                preferences.startWithEmptyDiscard,
                if (layouts.containsKey(preferences.layout)) layouts[preferences.layout] else layouts[BasicLayout.TAG]
            )
        gameState.sockets.withIndex().forEach { (index, socket) ->
            engine.configureEntity(entities[socket.card]) {
                with<CardRenderComponent> {
                    socketIndex = index
                    cardIndex = socket.card
                }
            }
        }
        renderingSystem.state = gameState
        animationRenderingSystem.state = gameState
        updateUi()
    }

    private fun initUi() {
        dealButton.setPosition(Const.STACK_POSITION.x + Const.CELL_WIDTH * 2f, Const.STACK_POSITION.y)
        undoButton.setPosition(Const.DISCARD_POSITION.x - Const.CELL_WIDTH * 2f, Const.DISCARD_POSITION.y)
        menuButton.setPosition(
            Const.CONTENT_WIDTH - Const.SPRITE_WIDTH - 2f,
            Const.CONTENT_HEIGHT - Const.SPRITE_WIDTH - Const.VERTICAL_PADDING - 3f
        )
        stage.actors.addAll(dealButton, undoButton, menuButton)
        Gdx.input.inputProcessor = stage
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
            if (gameState.won) won()
            else if (gameState.stalled) stalled()
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
    }

    private fun stalled() {
    }

    private fun won() {
    }

    private fun renderStack() {
        if (gameState.stack.isEmpty) return
        for (i in 0 until gameState.stack.size) {
            val x = Const.STACK_POSITION.x - i * 6f
            batch.draw(sprites.plate, x, Const.STACK_POSITION.y)
            batch.draw(sprites.back, x, Const.STACK_POSITION.y)
        }
    }

    private fun renderDiscard() {
        if (gameState.discard.isEmpty) return
        val cardIndex = gameState.discard.peek()
        batch.draw(sprites.plate, Const.DISCARD_POSITION.x, Const.DISCARD_POSITION.y)
        batch.draw(
            sprites.faces[cardIndex],
            Const.DISCARD_POSITION.x + Const.FACE_X,
            Const.DISCARD_POSITION.y + Const.FACE_Y
        )
    }

    private fun onTouch() {
        val touchPoint = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
        viewport.unproject(touchPoint)
        val column = (touchPoint.x / Const.CELL_WIDTH).toInt()
        val row = ((Const.CONTENT_HEIGHT - Const.VERTICAL_PADDING - touchPoint.y) / Const.CELL_HEIGHT).toInt()
        if (column <= gameState.layout.numberOfColumns && row <= gameState.layout.numberOfRows) {
            for (rowOffset in 1 downTo -1) {
                for (columnOffset in -1..1) {
                    val socket = gameState.layout.lookup(column + columnOffset, row + rowOffset)
                    if (socket != null && gameState.isOpen(socket.index)) {
                        take(socket.index)
                        return
                    }
                }
            }
        }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun dispose() {
        stage.dispose()
        super.dispose()
    }
}