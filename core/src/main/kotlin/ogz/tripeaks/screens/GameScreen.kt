package ogz.tripeaks.screens

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Logger
import com.ray3k.stripe.PopTable
import com.ray3k.stripe.PopTable.PopTableStyle
import ktx.app.KtxScreen
import ktx.ashley.configureEntity
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.getSystem
import ktx.ashley.remove
import ktx.ashley.with
import ktx.assets.disposeSafely
import ktx.collections.GdxArray
import ktx.collections.gdxArrayOf
import ktx.inject.Context
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.ecs.AnimationComponent
import ogz.tripeaks.ecs.AnimationSystem
import ogz.tripeaks.ecs.MultiSpriteComponent
import ogz.tripeaks.ecs.MultiSpriteRenderingSystem
import ogz.tripeaks.ecs.SpriteLayerPool
import ogz.tripeaks.ecs.TransformComponent
import ogz.tripeaks.graphics.AnimationSet
import ogz.tripeaks.graphics.CustomViewport
import ogz.tripeaks.graphics.HomeSprite
import ogz.tripeaks.graphics.ScreenTransitionAnimation
import ogz.tripeaks.graphics.SpriteSet
import ogz.tripeaks.models.AnimationType
import ogz.tripeaks.models.GameState
import ogz.tripeaks.models.SocketState
import ogz.tripeaks.models.layout.Layout
import ogz.tripeaks.models.layout.Socket
import ogz.tripeaks.services.MessageBox
import ogz.tripeaks.services.PersistenceService
import ogz.tripeaks.services.PlayerStatisticsService
import ogz.tripeaks.services.Receiver
import ogz.tripeaks.services.SettingsService
import ogz.tripeaks.ui.LabelButton
import ogz.tripeaks.services.Message.Companion as Msg

class GameScreen(private val context: Context) : KtxScreen {
    private val logger = Logger(GameScreen::class.java.simpleName)

    private val assets = context.inject<AssetManager>()
    private val batch = context.inject<SpriteBatch>()
    private val layerPool = context.inject<SpriteLayerPool>()
    private val messageBox = context.inject<MessageBox>()
    private val playerStatistics = context.inject<PlayerStatisticsService>()
    private val settings = context.inject<SettingsService>()
    private val uiStage = context.inject<Stage>()
    private val viewport = context.inject<CustomViewport>()

    private val engine = PooledEngine()
    private val renderHelper = RenderHelper(batch, viewport, engine)
    private val touchHandler = TouchHandler(messageBox)
    private var spriteSet: SpriteSet
    private var animationSet: AnimationSet
    private var frameBuffer =
        FrameBuffer(Pixmap.Format.RGB888, Constants.MIN_WORLD_WIDTH, Constants.WORLD_HEIGHT, false)

    private val animationSetChangedReceiver = Receiver<Msg.AnimationSetChanged> { onAnimationSetChanged(it) }
    private val showAllChangedReceiver = Receiver<Msg.ShowAllChanged> { onShowAllChanged(it) }
    private val skinChangedReceiver = Receiver<Msg.SkinChanged> { onSkinChanged(it) }
    private val spriteSetChangedReceiver = Receiver<Msg.SpriteSetChanged> { onSpriteSetChanged(it) }
    private val touchReceiver = Receiver<Msg.TouchDown> { onTouch(it) }
    private val entities: GdxArray<Entity> = gdxArrayOf(true, 52)
    private val stackEntity: Entity = engine.entity()
    private val discardEntity: Entity = engine.entity()
    private val entityUtils = SceneEntityUtils(layerPool, assets, engine)

    private var play: GameState? = null

    init {
        logger.level = Logger.INFO
        animationSet = settings.animationSet
        spriteSet = settings.spriteSet
        renderHelper.fbShader = animationSet.shaderProgram
        renderHelper.clearColor = settings.spriteSet.background

        messageBox.register(animationSetChangedReceiver)
        messageBox.register(showAllChangedReceiver)
        messageBox.register(skinChangedReceiver)
        messageBox.register(spriteSetChangedReceiver)
        messageBox.register(touchReceiver)

        for (entity in 0 until 52) {
            entities.add(engine.entity())
        }
    }

    override fun render(delta: Float) {
        viewport.apply()
        uiStage.viewport.apply()
        uiStage.act(delta)
        renderHelper.render(frameBuffer, delta)
        uiStage.draw()
    }

    override fun show() {
        super.show()

        play = PersistenceService().loadGameState() ?: settings.getNewGame()
        playerStatistics.updatePlayed()

        initEcs()
        engine.addSystem(AnimationSystem(animationSet, layerPool))
        engine.addSystem(MultiSpriteRenderingSystem(batch, spriteSet))
        Gdx.input.inputProcessor = InputMultiplexer(uiStage, touchHandler)
        setupStage(settings.skin)
    }

    override fun resume() {
        play = play ?: PersistenceService().loadGameState() ?: context.inject()
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
        messageBox.unregister(touchReceiver)

        renderHelper.disposeSafely()
        frameBuffer.disposeSafely()
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        viewport.update(width, height)
        uiStage.viewport.update(width, height, true)
        if (viewport.worldHeight > 0 && viewport.worldWidth > 0) {
            frameBuffer = FrameBuffer(
                Pixmap.Format.RGB888,
                viewport.worldWidth.toInt(),
                viewport.worldHeight.toInt(),
                false
            )
        }
        initStackAndDiscard()
    }

    private fun initStackAndDiscard() {
        play?.let { gameState ->
            if (settings.get().showAll) {
                entityUtils.initStackShowing(stackEntity, gameState.stack, viewport.worldWidth)
            } else {
                entityUtils.initStack(stackEntity, gameState.stack, viewport.worldWidth)
            }
            entityUtils.initDiscard(discardEntity, gameState.discard, viewport.worldWidth)
        }
    }

    private fun onTouch(message: Msg.TouchDown) {
        play?.let { game ->
            val pos = Vector2(message.screenX.toFloat(), message.screenY.toFloat())
            viewport.unproject(pos)

            val layout = game.gameLayout

            val x = pos.x.toInt() + (layout.numberOfColumns / 2) * Constants.CELL_WIDTH
            val y = viewport.worldHeight.toInt() / 2 - Constants.VERTICAL_PADDING - pos.y.toInt()
            val column = x / Constants.CELL_WIDTH
            val row = y / Constants.CELL_HEIGHT

            logger.info("Row: $row, Column: $column")

            // Touch was outside of the tableau
            if (column < 0 || column >= layout.numberOfColumns || row < 0 || row >= layout.numberOfRows) {
                return
            }

            // Search the top-most open socket that occupies the cell.
            for (rowOffset in 0 downTo -1) {
                for (columnOffset in 0 downTo -1) {
                    val socket = layout.lookup(column + columnOffset, row + rowOffset)
                    if (socket != null && game.take(socket.index)) {
                        logger.info("Take ${socket.index}")
                        updateSocket(socket.index, game)
                        val blocked = layout[socket.index].blocks
                        for (s in blocked) {
                            updateSocket(s, game)
                        }
                        entityUtils.updateDiscard(discardEntity, game.discard)
                        // TODO: Removed animation
                        return
                    }
                }
            }
        }
    }

    private fun onSkinChanged(msg: Msg.SkinChanged) {
        // TODO: Move shader theme selection to a uniform
        setupStage(msg.skin)
    }

    private fun onSpriteSetChanged(msg: Msg.SpriteSetChanged) {
        engine.getSystem<MultiSpriteRenderingSystem>().spriteSet = msg.spriteSet
        renderHelper.clearColor = msg.spriteSet.background
    }

    private fun onAnimationSetChanged(msg: Msg.AnimationSetChanged) {
        animationSet = msg.animationSet
        renderHelper.fbShader = animationSet.shaderProgram
        engine.getSystem<AnimationSystem>().animationSet = animationSet
    }

    private fun onShowAllChanged(msg: Msg.ShowAllChanged) {
        println("Show all: ${msg.showAll}")
    }

    private fun setupStage(skin: UiSkin) {
        uiStage.clear()

        val animationButton = LabelButton(skin, "Switch Animation")
        animationButton.onClick {
            val currentSettings = settings.get()
            currentSettings.animation =
                if (currentSettings.animation == AnimationType.BlinkAnim) AnimationType.DissolveAnim
                else AnimationType.BlinkAnim
            settings.update(currentSettings)
        }

        val menu = PopTable(skin["menu", PopTableStyle::class.java]).apply {
            add(Label("Menu test", skin))
            pad(12f, 12f, 12f, 12f)
            isHideOnUnfocus = true
            isModal = true
            addListener {
                if (isHidden) {
                    onMenuHidden()
                    true
                } else {
                    false
                }
            }
        }

        val menuButton = entityUtils.menuButton(uiStage, skin, assets, menu) { onMenuShown(menu) }
        val dealButton = entityUtils.dealButton(skin, this::openDialog)
        val undoButton = entityUtils.undoButton(skin) {
            val currentSettings = settings.get()
            currentSettings.darkTheme = !currentSettings.darkTheme
            settings.update(currentSettings)
        }

        val table = Table(skin).apply {
            pad(
                Constants.VERTICAL_PADDING.toFloat(),
                Constants.HORIZONTAL_PADDING.toFloat(),
                Constants.VERTICAL_PADDING.toFloat() - 1.0f,
                Constants.HORIZONTAL_PADDING.toFloat()
            )
            add(menuButton)
                .width(Constants.CARD_WIDTH.toFloat())
                .height(Constants.CARD_WIDTH.toFloat())
                .expand()
                .top()
                .right()
                .colspan(2)
            row()
            add(undoButton)
                .width(Constants.CARD_WIDTH.toFloat())
                .height(Constants.CARD_HEIGHT.toFloat())
                .bottom()
                .left()
            add(dealButton)
                .width(Constants.CARD_WIDTH.toFloat())
                .height(Constants.CARD_HEIGHT.toFloat())
                .expand()
                .bottom()
                .right()
        }

        table.setFillParent(true)
        uiStage.addActor(table)
    }

    private fun printStatistics() {
        val stats = playerStatistics.get()
        logger.info("Played: ${stats.played}, Won: ${stats.won}")
    }

    private fun openDialog() {
        val skin = settings.skin
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
    }

    private fun onDialogShown(dialog: PopTable) {
        touchHandler.silent = true
        touchHandler.dialog = dialog
        renderHelper.blurred = true
    }

    private fun onMenuShown(menu: PopTable) {
        touchHandler.silent = true
        touchHandler.dialog = menu
    }

    private fun onDialogHidden() {
        touchHandler.silent = false
        touchHandler.dialog = null
        renderHelper.blurred = false
    }

    private fun initEcs() {
        removeComponents()
        setupTableau()

        engine.entity {
            val bg = HomeSprite
            val bgSprite = bg.get(spriteSet)

            with<TransformComponent> {
                position = Vector2(bgSprite.regionWidth * -0.5f, bgSprite.regionHeight * -0.5f)
            }

            with<MultiSpriteComponent> {
                color.set(0.1f, 1f, 1f, 1f)
                z = 10
                layers.add(layerPool.obtain().apply {
                    spriteType = bg
                })
            }

            with<AnimationComponent> {
                timeRemaining = 2f
                animationType = ScreenTransitionAnimation
            }
        }

//        val x = 50f

//        // Card
//        engine.entity {
//            val card = CardSprite
//            val cardSprite = card.get(spriteSet)
//            val face = FaceSprite(1)
//            val faceSprite = face.get(spriteSet)
//
//            with<TransformComponent> {
//                origin = Vector2(
//                    MathUtils.floor(cardSprite.regionWidth * 0.5f).toFloat(),
//                    MathUtils.floor(cardSprite.regionHeight * 0.5f).toFloat()
//                )
//                position = origin.cpy().scl(-1f, -1f).add(x, 0f)
//            }
//
//            with<MultiSpriteComponent> {
//                this.color.set(0.02f, 1f, 1f, 1f)
//                z = 0
//                layers.add(layerPool.obtain().apply {
//                    spriteType = card
//                })
//                layers.add(layerPool.obtain().apply {
//                    spriteType = face
//                    localPosition.set(
//                        (cardSprite.regionWidth - faceSprite.regionWidth) * 0.5f,
//                        (cardSprite.regionHeight - faceSprite.regionHeight) * 0.5f
//                    )
//                })
//            }
//
//            with<AnimationComponent> {
//                timeRemaining = 10000f
//                animationType = CardRemovedAnimation
//            }
//        }
    }

    private fun removeComponents() {
        entities.forEach(entityUtils::removeAndPoolComponents)
        entityUtils.removeAndPoolComponents(stackEntity)
    }

    private fun setupTableau() {
        play?.let { gameState ->
            for (s in 0 until gameState.gameLayout.numberOfSockets) {
                initSocket(s, gameState)
            }
            initStackAndDiscard()
        }
    }

    private fun initSocket(socketIndex: Int, gameState: GameState) {
        val socket = gameState.socket(socketIndex)
        val socketState = gameState.socketState(socketIndex)
        val entity = entities[socketState.card]
        engine.configureEntity(entity) {
            with<TransformComponent> {
                position.set(socketPosition(socket, gameState.gameLayout))
                origin.set((Constants.CARD_WIDTH / 2).toFloat(), (Constants.CARD_HEIGHT / 2).toFloat())
            }
        }
        updateSocket(entity, socket, socketState,gameState.isOpen(socketIndex))
    }

    private fun updateSocket(socketIndex: Int, gameState: GameState) {
        val socket = gameState.socket(socketIndex)
        val socketState = gameState.socketState(socketIndex)
        val entity = entities[socketState.card]
        updateSocket(entity, socket, socketState,gameState.isOpen(socketIndex))
    }

    private fun updateSocket(entity: Entity, socket: Socket, socketState: SocketState, isOpen: Boolean) {
        println("${socket.index} -> $isOpen")
        when {
            socketState.isEmpty -> entityUtils.removeAndPoolSpriteComponent(entity)
            isOpen -> entityUtils.updateCardOpen(entity, socketState.card, socket.z)
            settings.get().showAll -> entityUtils.updateCardClosedShowing(entity, socketState.card, socket.z)
            else -> entityUtils.updateCardClosed(entity, socket.z)
        }
    }

    private fun socketPosition(socket: Socket, layout: Layout): Vector2 {
        val maxY = viewport.worldHeight.toInt() / 2 - Constants.VERTICAL_PADDING - Constants.CARD_HEIGHT + 1
        val minX = -(layout.numberOfColumns / 2) * Constants.CELL_WIDTH
        return Vector2(
            (minX + socket.column * Constants.CELL_WIDTH + Constants.CELL_PADDING_LEFT).toFloat(),
            (maxY - socket.row * Constants.CELL_HEIGHT - Constants.CELL_PADDING_TOP).toFloat()
        )
    }
}