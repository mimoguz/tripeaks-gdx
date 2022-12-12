package ogz.tripeaks.screens

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Logger
import com.badlogic.gdx.utils.Pool
import ktx.app.KtxScreen
import ktx.ashley.entity
import ktx.ashley.getSystem
import ktx.ashley.with
import ktx.assets.disposeSafely
import ktx.scene2d.Scene2DSkin
import ogz.tripeaks.assets.FontAssets
import ogz.tripeaks.assets.TextureAtlasAssets
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.assets.get
import ogz.tripeaks.ecs.AnimationComponent
import ogz.tripeaks.ecs.AnimationSystem
import ogz.tripeaks.ecs.RenderComponent
import ogz.tripeaks.ecs.SpriteRenderingSystem
import ogz.tripeaks.ecs.TransformComponent
import ogz.tripeaks.graphics.Animations
import ogz.tripeaks.graphics.CardRemovedAnimation
import ogz.tripeaks.graphics.CardSprite
import ogz.tripeaks.graphics.CustomViewport
import ogz.tripeaks.graphics.FaceRemovedAnimation
import ogz.tripeaks.graphics.FaceSprite
import ogz.tripeaks.graphics.HomeSprite
import ogz.tripeaks.graphics.ScreenTransitionAnimation
import ogz.tripeaks.graphics.SpriteSet
import ogz.tripeaks.models.GameState
import ogz.tripeaks.services.PersistenceService
import ogz.tripeaks.services.PooledMessageBox
import ogz.tripeaks.services.Receiver
import ogz.tripeaks.services.TouchDown
import ogz.tripeaks.ui.LabelButton

class GameScreen(private val assets: AssetManager) : KtxScreen, Receiver<TouchDown> {

    private val logger = Logger(GameScreen::class.java.simpleName)

    private val batch = SpriteBatch()
    private val viewport = CustomViewport(MIN_WORLD_WIDTH, MAX_WORLD_WIDTH, WORLD_HEIGHT, OrthographicCamera())
    private val uiStage = Stage(CustomViewport(MIN_WORLD_WIDTH, MAX_WORLD_WIDTH, WORLD_HEIGHT, OrthographicCamera()))
    private val engine = PooledEngine()
    private val messageBox = PooledMessageBox()
    private val renderHelper = RenderHelper(batch, viewport, engine)
    private val touchHandler = TouchHandler(messageBox)

    private var spriteSet = SpriteSet(false, 0, assets)
    private var animationSet = Animations.DISSOLVE

    private var frameBuffer = FrameBuffer(Pixmap.Format.RGB888, MIN_WORLD_WIDTH, WORLD_HEIGHT, false)

    private var play: GameState? = null

    init {
        logger.level = Logger.INFO
        renderHelper.fbShader = animationSet.shaderProgram
        renderHelper.clearColor = LIGHT_UI_BG
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

        play = PersistenceService().loadGame() ?: GameState.startNew(IntArray(52) { it }, null)
        play?.statistics?.unregister()
        play?.statistics?.register(messageBox)

        engine.addSystem(AnimationSystem(animationSet))
        engine.addSystem(SpriteRenderingSystem(batch, spriteSet))
        messageBox.addPool(object : Pool<TouchDown>() {
            override fun newObject(): TouchDown = TouchDown()
        })
        messageBox.register(this)
        Gdx.input.inputProcessor = InputMultiplexer(uiStage, touchHandler)
        setupStage()
        setupECS()
    }

    override fun resume() {
        super.resume()
        play = play ?: PersistenceService().loadGame()
    }

    override fun pause() {
        super.pause()
        play?.also { PersistenceService().saveGame(it) }
    }

    override fun dispose() {
        messageBox.unregister(this)
        renderHelper.disposeSafely()
        assets.disposeSafely()
        batch.disposeSafely()
        uiStage.disposeSafely()
        messageBox.disposeSafely()
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

    override fun receive(message: TouchDown) {
        val pos = Vector2(message.screenX.toFloat(), message.screenY.toFloat())
        viewport.unproject(pos)
        logger.info("Touch event {x: ${pos.x}, y: ${pos.y}, pointer:${message.pointer}, button: ${message.button}}")
        messageBox.returnMessage<TouchDown>(message)
    }

    private fun switchSkin() {
        spriteSet = SpriteSet(!spriteSet.isDark, 0, assets)
        engine.getSystem<SpriteRenderingSystem>().spriteSet = spriteSet
        renderHelper.clearColor = if (spriteSet.isDark) DARK_UI_BG else LIGHT_UI_BG
        Animations.setTheme(spriteSet.isDark)
        Scene2DSkin.defaultSkin =
            if (spriteSet.isDark)
                UiSkin(
                    assets[TextureAtlasAssets.Ui],
                    assets[FontAssets.GamePixels],
                    DARK_UI_TEXT,
                    DARK_UI_EMPHASIS,
                    "dark"
                )
            else
                UiSkin(
                    assets[TextureAtlasAssets.Ui],
                    assets[FontAssets.GamePixels],
                    LIGHT_UI_TEXT,
                    LIGHT_UI_EMPHASIS,
                    "light"
                )
        setupStage()
    }

    private fun switchAnimation() {
        animationSet =
            if (animationSet === Animations.DISSOLVE) Animations.BLINK
            else Animations.DISSOLVE
        renderHelper.fbShader = animationSet.shaderProgram
        engine.getSystem<AnimationSystem>().animationSet = animationSet
    }

    private fun setupStage() {
        uiStage.clear()

        val themeButton = LabelButton(Scene2DSkin.defaultSkin, "Switch Theme")
        themeButton.onClick(this::switchSkin)

        val animationButton = LabelButton(Scene2DSkin.defaultSkin, "Switch Animation")
        animationButton.onClick(this::switchAnimation)

        val dialogButton = LabelButton(Scene2DSkin.defaultSkin, "Open dialog")
        dialogButton.onClick(this::openDialog)

        val table = Table(Scene2DSkin.defaultSkin).apply {
            pad(2f)
            align(Align.bottomLeft)
            add(themeButton).align(Align.bottomLeft).padBottom(2f)
            row()
            add(animationButton).align(Align.bottomLeft).padBottom(2f)
            row()
            add(dialogButton).align(Align.bottomLeft)
        }

        uiStage.actors.add(table)
    }

    private fun openDialog() {
        val dialog = Dialog("", Scene2DSkin.defaultSkin).also { dialog ->
            dialog.contentTable.apply {
                add(Label("UI test", Scene2DSkin.defaultSkin))
                pad(4f, 8f, 4f, 8f)
            }
            dialog.buttonTable.apply {
                add(LabelButton(Scene2DSkin.defaultSkin, "Close").apply {
                    onClick {
                        dialog.hide()
                        this@GameScreen.touchHandler.slient = false
                        this@GameScreen.renderHelper.blurred = false
                    }
                })
            }
        }
        touchHandler.slient = true
        renderHelper.blurred = true
        dialog.show(uiStage)
    }

    private fun setupECS() {
        engine.removeAllEntities()

        engine.entity {
            val spriteType = HomeSprite
            val sprite = spriteType.get(spriteSet)

            with<TransformComponent> {
                position = Vector2(sprite.regionWidth * -0.5f, sprite.regionHeight * -0.5f)
            }

            with<RenderComponent> {
                this.spriteType = spriteType
                color.set(0.1f, 1f, 1f, 1f)
                z = 10
            }

            with<AnimationComponent> {
                timeRemaining = 2f
                animationType = ScreenTransitionAnimation
            }
        }

        val x = 50f

        // Card
        engine.entity {
            val spriteType = CardSprite
            val sprite = spriteType.get(spriteSet)

            with<TransformComponent> {
                origin = Vector2(
                    MathUtils.floor(sprite.regionWidth * 0.5f).toFloat(),
                    MathUtils.floor(sprite.regionHeight * 0.5f).toFloat()
                )
                position = origin.cpy().scl(-1f, -1f).add(x, 0f)
            }

            with<RenderComponent> {
                this.spriteType = spriteType
                this.color.set(0.02f, 1f, 1f, 1f)
                z = 0
            }

            with<AnimationComponent> {
                timeRemaining = 10000f
                animationType = CardRemovedAnimation
            }
        }

        // Card face
        engine.entity {
            val spriteType = FaceSprite(1)
            val sprite = spriteType.get(spriteSet)
            with<TransformComponent> {
                origin = Vector2(
                    MathUtils.floor(sprite.regionWidth * 0.5f).toFloat(),
                    MathUtils.floor(sprite.regionHeight * 0.5f).toFloat()
                )
                position = origin.cpy().scl(-1f, -1f).add(x, 0f)
            }
            with<RenderComponent> {
                this.spriteType = spriteType
                this.color.set(0.01f, 1f, 1f, 1f)
                z = 1
            }
            with<AnimationComponent> {
                timeRemaining = 10000f
                animationType = FaceRemovedAnimation
            }
        }
    }

    companion object {
        const val MIN_WORLD_WIDTH = 300
        const val MAX_WORLD_WIDTH = 360
        const val WORLD_HEIGHT = 168
        val DARK_UI_TEXT = rgb(242, 204, 143)
        val DARK_UI_EMPHASIS = rgb(184, 55, 68)
        val LIGHT_UI_TEXT = rgb(76, 56, 77)
        val LIGHT_UI_EMPHASIS = rgb(224, 122, 95)
        val DARK_UI_BG = Color.valueOf("232433ff")
        val LIGHT_UI_BG = Color.valueOf("63a347ff")

        private fun rgb(r: Int, g: Int, b: Int): Color = Color(r / 255f, g / 255f, b / 255f, 1f)
    }
}