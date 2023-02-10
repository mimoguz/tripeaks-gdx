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
import ogz.tripeaks.graphics.BackSprite
import ogz.tripeaks.graphics.CardSprite
import ogz.tripeaks.graphics.CustomViewport
import ogz.tripeaks.graphics.FaceSprite
import ogz.tripeaks.graphics.HomeSprite
import ogz.tripeaks.graphics.ScreenTransitionAnimation
import ogz.tripeaks.graphics.SpriteSet
import ogz.tripeaks.models.AnimationType
import ogz.tripeaks.models.GameState
import ogz.tripeaks.models.layout.Layout
import ogz.tripeaks.models.layout.Socket
import ogz.tripeaks.screens.Constants.MIN_WORLD_WIDTH
import ogz.tripeaks.screens.Constants.WORLD_HEIGHT
import ogz.tripeaks.services.MessageBox
import ogz.tripeaks.services.PersistenceService
import ogz.tripeaks.services.PlayerStatisticsService
import ogz.tripeaks.services.Receiver
import ogz.tripeaks.services.SettingsService
import ogz.tripeaks.ui.LabelButton
import ogz.tripeaks.services.Message.Companion as Msg

class GameScreen(private val context: Context) : KtxScreen {
    private val logger = Logger(GameScreen::class.java.simpleName)
    private val messageBox = context.inject<MessageBox>()
    private val assets = context.inject<AssetManager>()
    private val batch = context.inject<SpriteBatch>()
    private val viewport = context.inject<CustomViewport>()
    private val uiStage = context.inject<Stage>()
    private val settings = context.inject<SettingsService>()
    private val playerStatistics = context.inject<PlayerStatisticsService>()
    private val layerPool = context.inject<SpriteLayerPool>()
    private val engine = PooledEngine()
    private val renderHelper = RenderHelper(batch, viewport, engine)
    private val touchHandler = TouchHandler(messageBox)
    private var spriteSet: SpriteSet
    private var animationSet: AnimationSet
    private var frameBuffer = FrameBuffer(Pixmap.Format.RGB888, MIN_WORLD_WIDTH, WORLD_HEIGHT, false)

    private val animationSetChangedReceiver = Receiver<Msg.AnimationSetChanged> { onAnimationSetChanged(it) }
    private val showAllChangedReceiver = Receiver<Msg.ShowAllChanged> { onShowAllChanged(it) }
    private val skinChangedReceiver = Receiver<Msg.SkinChanged> { onSkinChanged(it) }
    private val spriteSetChangedReceiver = Receiver<Msg.SpriteSetChanged> { onSpriteSetChanged(it) }
    private val touchReceiver = Receiver<Msg.TouchDown> { onTouch(it) }
    private val entities: GdxArray<Entity> = gdxArrayOf(true, 52)

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
                        val card = game.socketState(socket.index).card
                        val sprites = entities[card][MultiSpriteComponent.mapper]
                        if (sprites is MultiSpriteComponent) {
                            sprites.layers.forEach { layerPool.free(it) }
                        }
                        entities[card].remove<MultiSpriteComponent>()
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

        val menuButton = GameScreenUtils.menuButton(uiStage, skin, assets, menu, this::onMenuShown)
        val dealButton = GameScreenUtils.dealButton(skin, assets, this::openDialog)
        val undoButton = GameScreenUtils.undoButton(skin, assets) {
            val currentSettings = settings.get()
            currentSettings.darkTheme = !currentSettings.darkTheme
            settings.update(currentSettings)
        }

        val table = Table(skin).apply {
            pad(
                Constants.VERTICAL_PADDING.toFloat(),
                Constants.HORIZONTAL_PADDING.toFloat(),
                Constants.VERTICAL_PADDING.toFloat(),
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
                    touchHandler.slient = false
                    renderHelper.blurred = false
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
        touchHandler.slient = true
        renderHelper.blurred = true
        dialog.show(uiStage)
    }

    private fun onMenuShown() {
        touchHandler.slient = true
    }

    private fun onMenuHidden() {
        touchHandler.slient = false
    }

    private fun onDialogShown() {
        touchHandler.slient = true
        renderHelper.blurred = true
    }

    private fun onDialogHidden() {
        touchHandler.slient = false
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
        entities.forEach { entity ->
            val sprites = entity.remove<MultiSpriteComponent>()
            if (sprites is MultiSpriteComponent) {
                sprites.layers.forEach { layerPool.free(it) }
            }
            entity.removeAll()
        }
    }

    private fun setupTableau() {
        play?.let { gameState ->
            for (s in 0 until gameState.gameLayout.numberOfSockets) {
                val socket = gameState.socket(s)
                val socketState = gameState.socketState(s)
                val entity = entities[socketState.card]
                engine.configureEntity(entity) {
                    with<TransformComponent> {
                        position.set(socketPosition(socket, gameState.gameLayout))
                        origin.set((Constants.CARD_WIDTH / 2).toFloat(), (Constants.CARD_HEIGHT / 2).toFloat())
                    }
                    if (!socketState.isEmpty) {
                        with<MultiSpriteComponent> {
                            z = socket.z
                            color.set(1f, 1f, 1f, 1f)
                            layers.add(layerPool.obtain().apply {
                                spriteType = CardSprite
                            })
                            if (gameState.isOpen(s)) {
                                layers.add(layerPool.obtain().apply {
                                    spriteType = FaceSprite(socketState.card)
                                    localPosition.set(
                                        ((Constants.CARD_WIDTH - Constants.FACE_WIDTH) / 2).toFloat(),
                                        ((Constants.CARD_HEIGHT - Constants.FACE_HEIGHT) / 2).toFloat()
                                    )
                                })
                            } else {
                                layers.add(layerPool.obtain().apply {
                                    spriteType = BackSprite
                                })
                            }
                        }
                    }
                }
            }
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