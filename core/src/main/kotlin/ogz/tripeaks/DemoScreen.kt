package ogz.tripeaks

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
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
import ktx.ashley.with
import ktx.assets.disposeSafely
import ktx.graphics.use
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
import ogz.tripeaks.screens.GameScreenState
import ogz.tripeaks.services.PooledMessageBox
import ogz.tripeaks.services.Receiver
import ogz.tripeaks.services.TouchDown
import ogz.tripeaks.ui.LabelButton

class DemoScreen(private val assets: AssetManager) : KtxScreen, Receiver<TouchDown> {

    private val logger = Logger(DemoScreen::class.java.simpleName)

    private val batch = SpriteBatch()
    private val viewport = CustomViewport(MIN_WORLD_WIDTH, MAX_WORLD_WIDTH, WORLD_HEIGHT, OrthographicCamera())
    private val uiStage = Stage(CustomViewport(MIN_WORLD_WIDTH, MAX_WORLD_WIDTH, WORLD_HEIGHT, OrthographicCamera()))
    private val engine = PooledEngine()
    private val messageBox = PooledMessageBox()
    private val gameScreenState = GameScreenState(assets, engine, messageBox, false)

    private var frameBuffer = FrameBuffer(Pixmap.Format.RGB888, MIN_WORLD_WIDTH, WORLD_HEIGHT, false)
    private var time = 0f

    override fun render(delta: Float) {
        viewport.apply()
        uiStage.viewport.apply()
        uiStage.act(delta)
        time = (time + delta) % 1f

        frameBuffer.begin()
        gameScreenState.setupFrameBufferRender(batch)
        batch.use {
            gameScreenState.runEngine(delta)
        }
        gameScreenState.resetBatch(batch)
        frameBuffer.end(viewport.screenX, viewport.screenY, viewport.screenWidth, viewport.screenHeight)

        val texture = frameBuffer.colorBufferTexture
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)
        gameScreenState.setupRender(batch)
        batch.use(viewport.camera) {
            it.draw(
                texture,
                viewport.worldWidth * -0.5f,
                viewport.worldHeight * -0.5f,
                viewport.worldWidth,
                viewport.worldHeight,
                0f,
                0f,
                1f,
                1f
            )
        }
        gameScreenState.resetBatch(batch)
        uiStage.draw()
    }

    init {
        logger.level = Logger.INFO
    }

    override fun show() {
        super.show()
        engine.addSystem(AnimationSystem(gameScreenState.animationSet))
        engine.addSystem(SpriteRenderingSystem(batch, gameScreenState.spriteSet))
        messageBox.addPool(object : Pool<TouchDown>() {
            override fun newObject(): TouchDown = TouchDown()
        })
        messageBox.register(this)
        Gdx.input.inputProcessor = InputMultiplexer(uiStage, gameScreenState)
        setupStage()
        setupECS()
    }

    override fun dispose() {
        assets.disposeSafely()
        batch.disposeSafely()
        uiStage.disposeSafely()
        gameScreenState.disposeSafely()
        messageBox.disposeSafely()
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        viewport.update(width, height)
        uiStage.viewport.update(width, height, true)
        frameBuffer =
            FrameBuffer(Pixmap.Format.RGB888, viewport.worldWidth.toInt(), viewport.worldHeight.toInt(), false)
        gameScreenState.onResize(viewport.worldWidth, viewport.worldHeight)
    }

    override fun receive(msg: TouchDown) {
        val pos = Vector2(msg.x.toFloat(), msg.y.toFloat())
        viewport.unproject(pos)
        logger.info("Touch event {x: ${pos.x}, y: ${pos.y}, pointer:${msg.pointer}, button: ${msg.button}}")
        messageBox.returnMessage<TouchDown>(msg)
    }

    private fun onTouchDown(x: Int, y: Int, pointer: Int, button: Int) {
        val pos = Vector2(x.toFloat(), y.toFloat())
        viewport.unproject(pos)
        logger.info("Touch event {x: ${pos.x}, y: ${pos.y}, pointer:$pointer, button: $button}")
    }

    private fun switchSkin() {
        gameScreenState.isDark = !gameScreenState.isDark
        Scene2DSkin.defaultSkin =
            if (gameScreenState.isDark)
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
        gameScreenState.animationSet =
            if (gameScreenState.animationSet === Animations.DISSOLVE) Animations.BLINK
            else Animations.DISSOLVE
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
                        this@DemoScreen.gameScreenState.dialogShowing = false
                    }
                })
            }
        }
        gameScreenState.dialogShowing = true
        dialog.show(uiStage)
    }

    private fun setupECS() {
        engine.removeAllEntities()

        engine.entity {
            val spriteType = HomeSprite
            val sprite = spriteType.get(gameScreenState.spriteSet)

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
            val sprite = spriteType.get(gameScreenState.spriteSet)

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
            val sprite = spriteType.get(gameScreenState.spriteSet)
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

        private fun rgb(r: Int, g: Int, b: Int): Color = Color(r / 255f, g / 255f, b / 255f, 1f)
    }
}