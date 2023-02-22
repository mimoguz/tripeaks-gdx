package ogz.tripeaks.screens

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.ashley.utils.ImmutableArray
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
import ktx.ashley.entity
import ktx.ashley.getSystem
import ktx.ashley.with
import ktx.assets.disposeSafely
import ktx.collections.gdxArrayOf
import ktx.inject.Context
import ogz.tripeaks.assets.TextureAtlasAssets
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.assets.get
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
import ogz.tripeaks.models.GameState
import ogz.tripeaks.screens.Constants.CARD_HEIGHT
import ogz.tripeaks.screens.Constants.CARD_WIDTH
import ogz.tripeaks.screens.Constants.HORIZONTAL_PADDING
import ogz.tripeaks.screens.Constants.VERTICAL_PADDING
import ogz.tripeaks.services.MessageBox
import ogz.tripeaks.services.PersistenceService
import ogz.tripeaks.services.PlayerStatisticsService
import ogz.tripeaks.services.Receiver
import ogz.tripeaks.services.SettingsService
import ogz.tripeaks.ui.GameButton
import ogz.tripeaks.ui.GameUi
import ogz.tripeaks.ui.TopLeft
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
    private val ui = GameUi()

    private val engine = PooledEngine()
    private val renderHelper = RenderHelper(batch, viewport, engine, ui)
    private val touchHandler = TouchHandler(messageBox)
    private var spriteSet: SpriteSet
    private var animationSet: AnimationSet
    private var frameBuffer =
        FrameBuffer(Pixmap.Format.RGB888, Constants.MIN_WORLD_WIDTH.toInt(), Constants.WORLD_HEIGHT.toInt(), false)

    private val animationSetChangedReceiver = Receiver<Msg.AnimationSetChanged> { onAnimationSetChanged(it) }
    private val showAllChangedReceiver = Receiver<Msg.ShowAllChanged> { onShowAllChanged(it) }
    private val skinChangedReceiver = Receiver<Msg.SkinChanged> { onSkinChanged(it) }
    private val spriteSetChangedReceiver = Receiver<Msg.SpriteSetChanged> { onSpriteSetChanged(it) }
    private val touchReceiver = Receiver<Msg.TouchDown> { onTouch(it) }
    private val entities: ImmutableArray<Entity>
    private val stackEntity: Entity = engine.entity()
    private val discardEntity: Entity = engine.entity()
    private val stageUtils = StageUtils(assets, uiStage)

    private var undoButton = stageUtils.undoButton(settings.skin, this::undo)
    private var dealButton = stageUtils.dealButton(settings.skin, this::deal)

    private var play: GameState? = null
    private var entityUtils: EntityUtils? = null

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

        val es = gdxArrayOf<Entity>(false, 52)
        for (card in 0 until 52) {
            es.add(engine.entity())
        }
        entities = ImmutableArray(es)
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

        val game = PersistenceService().loadGameState() ?: settings.getNewGame()
        entityUtils = if (settings.get().showAll) {
            ShowingEntityUtils(game, engine, layerPool, assets, entities, stackEntity, discardEntity)
        } else {
            ShowingEntityUtils(game, engine, layerPool, assets, entities, stackEntity, discardEntity)
        }
        play = game

        playerStatistics.updatePlayed()

        initEcs()
        engine.addSystem(AnimationSystem(animationSet, layerPool))
        engine.addSystem(MultiSpriteRenderingSystem(batch, spriteSet))
        Gdx.input.inputProcessor = InputMultiplexer(uiStage, touchHandler)
        setupStage(settings.skin)
    }

    override fun resume() {
        val game = play ?: PersistenceService().loadGameState() ?: context.inject()
        play = game
        entityUtils = if (settings.get().showAll) {
            ShowingEntityUtils(game, engine, layerPool, assets, entities, stackEntity, discardEntity)
        } else {
            ShowingEntityUtils(game, engine, layerPool, assets, entities, stackEntity, discardEntity)
        }
        setupTableau()
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
        entityUtils?.moveDiscard(viewport.worldWidth)
        entityUtils?.moveStack(viewport.worldWidth)
        ui.update(viewport.worldWidth, viewport.worldHeight)
    }

    private fun onTouch(message: Msg.TouchDown) {
        play?.let { gameState ->
            val pos = Vector2(message.screenX.toFloat(), message.screenY.toFloat())
            viewport.unproject(pos)

            if (ui.handlePressed(pos.x, pos.y)) {
                return
            }

            val layout = gameState.gameLayout

            val x = pos.x.toInt() + (layout.numberOfColumns / 2) * Constants.CELL_WIDTH.toInt()
            val y = viewport.worldHeight.toInt() / 2 - Constants.VERTICAL_PADDING.toInt() - pos.y.toInt()
            val column = x / Constants.CELL_WIDTH.toInt()
            val row = y / Constants.CELL_HEIGHT.toInt()

            logger.info("Row: $row, Column: $column")

            // Touch was outside of the tableau
            if (column < 0 || column >= layout.numberOfColumns || row < 0 || row >= layout.numberOfRows) {
                return
            }

            // Search the top-most open socket that occupies the cell.
            for (rowOffset in 0 downTo -1) {
                for (columnOffset in 0 downTo -1) {
                    val socket = layout.lookup(column + columnOffset, row + rowOffset)
                    if (socket != null && gameState.take(socket.index)) {
                        entityUtils?.updateSocket(socket.index)
                        val blocked = layout[socket.index].blocks
                        for (s in blocked) {
                            entityUtils?.updateSocket(s)
                        }
                        entityUtils?.updateDiscard(viewport.worldWidth)
                        entityUtils?.addRemovalAnimation(socket.index)
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
        play?.let { game ->
            entityUtils = if (msg.showAll) {
                ShowingEntityUtils(game, engine, layerPool, assets, entities, stackEntity, discardEntity)
            } else {
                ShowingEntityUtils(game, engine, layerPool, assets, entities, stackEntity, discardEntity)
            }
            setupTableau()
        }
    }

    private fun setupStage(skin: UiSkin) {
        uiStage.clear()

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

        val menuButton = stageUtils.menuButton(skin, menu) { onMenuShown(menu) }
        dealButton = stageUtils.dealButton(skin, this::deal)
        undoButton = stageUtils.undoButton(skin, this::undo)

        val table = Table(skin).apply {
            pad(
                VERTICAL_PADDING,
                HORIZONTAL_PADDING,
                VERTICAL_PADDING - 1.0f,
                HORIZONTAL_PADDING
            )
            add(menuButton)
                .width(CARD_WIDTH)
                .height(CARD_WIDTH)
                .expand()
                .top()
                .right()
                .colspan(2)
            row()
            add(undoButton)
                .width(CARD_WIDTH)
                .height(CARD_HEIGHT)
                .bottom()
                .left()
            add(dealButton)
                .width(CARD_WIDTH)
                .height(CARD_HEIGHT)
                .expand()
                .bottom()
                .right()
        }

        table.setFillParent(true)
        uiStage.addActor(table)

        ui.clear()
        ui.add(GameButton(
            skin,
            assets[TextureAtlasAssets.Ui].findRegion("menu_${skin.resourcePostfix}"),
            TopLeft(Vector2(4f, 4f)),
        ) {}.apply {
            setSize(CARD_WIDTH, CARD_WIDTH)
        })
    }

    private fun printStatistics() {
        val stats = playerStatistics.get()
        logger.info("Played: ${stats.played}, Won: ${stats.won}")
    }

    private fun deal() {
        play?.let { gameState ->
            if (gameState.deal()) {
                entityUtils?.updateDiscard(viewport.worldWidth)
                entityUtils?.updateStack(viewport.worldWidth)
            }
            dealButton.isDisabled = !gameState.canDeal
            undoButton.isDisabled = !gameState.canUndo
        }
    }

    private fun undo() {
        play?.let { gameState ->
            val target = gameState.undo()
            entityUtils?.updateDiscard(viewport.worldWidth)
            dealButton.isDisabled = !gameState.canDeal
            undoButton.isDisabled = !gameState.canUndo

            when (target) {
                Int.MIN_VALUE -> {}
                -1 -> entityUtils?.updateStack(viewport.worldWidth)
                else -> {
                    val socket = gameState.gameLayout[target]
                    entityUtils?.updateSocket(socket.index)
                    for (blocked in socket.blocks) {
                        entityUtils?.updateSocket(blocked)
                    }
                }
            }
        }
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

    private fun initEcs() {
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

    private fun setupTableau() {
        play?.let { gameState ->
            for (s in 0 until gameState.gameLayout.numberOfSockets) {
                entityUtils?.initSocket(s)
            }
            entityUtils?.initStack(viewport.worldWidth)
            entityUtils?.initDiscard(viewport.worldWidth)
        }
    }
}