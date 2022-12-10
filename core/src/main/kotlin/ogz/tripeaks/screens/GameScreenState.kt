package ogz.tripeaks.screens

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Disposable
import ktx.app.clearScreen
import ktx.ashley.getSystem
import ktx.assets.disposeSafely
import ogz.tripeaks.ecs.AnimationSystem
import ogz.tripeaks.ecs.SpriteRenderingSystem
import ogz.tripeaks.graphics.Animations
import ogz.tripeaks.graphics.SpriteSet
import ogz.tripeaks.services.PooledMessageBox
import ogz.tripeaks.services.TouchDown

/**
 * Helper class to handle the secondary concerns for GameScreen. It keeps and monitors sprite set, animation set, and
 * dialog state. Its methods should be used for setting up relevant parameters for rendering, and updating the ECS.
 * It's also used as input processor for GameScreen.
 */
class GameScreenState(
    private val assets: AssetManager,
    private val engine: PooledEngine,
    private val messageBox: PooledMessageBox,
    dark: Boolean = false
) : Disposable, InputAdapter() {

    private val pixelateShader = ShaderProgram(
        javaClass.classLoader.getResource("shaders/basic.vert")?.readText(),
        javaClass.classLoader.getResource("shaders/pixelate.frag")?.readText()
    )

    private var aspectRatio = 1f

    private var handleTouchDown = this::onTouchDownWhenDialogNotShowing

    var setupRender: (SpriteBatch) -> Unit = this::setupWhenDialogNotShowing
        private set

    var runEngine: (Float) -> Unit = engine::update
        private set

    var animationSet = Animations.BLINK
        set(value) {
            field = value
            engine.getSystem<AnimationSystem>().animationSet = animationSet
        }

    var spriteSet = SpriteSet(dark, 0, assets)
        private set

    var isDark = false
        set(value) {
            field = value
            spriteSet = SpriteSet(value, 0, assets)
            engine.getSystem<SpriteRenderingSystem>().spriteSet = spriteSet
            Animations.setTheme(value)
        }

    var dialogShowing = false
        set(value) {
            field = value
            if (value) {
                setupRender =  this::setupWhenDialogShowing
                runEngine =  this::runWhenDialogShowing
                handleTouchDown =  this::onTouchDownWhenDialogShowing
            } else {
                setupRender = this::setupWhenDialogNotShowing
                runEngine = engine::update
                handleTouchDown = this::onTouchDownWhenDialogNotShowing
            }
        }

    fun onResize(worldWidth: Float, worldHeight: Float) {
        aspectRatio = worldHeight / worldWidth
    }

    fun setupFrameBufferRender(batch: SpriteBatch): Unit {
        clearScreen(spriteSet.background.r, spriteSet.background.g, spriteSet.background.b, 1f)
        batch.shader = animationSet.shaderProgram
        batch.enableBlending()
    }

    fun resetBatch(batch: SpriteBatch) {
        batch.disableBlending()
        batch.shader = null
        batch.setColor(1f, 1f, 1f, 1f)
    }

    override fun dispose() {
        pixelateShader.disposeSafely()
    }

    override fun touchDown(x: Int, y: Int, pointer: Int, button: Int): Boolean = handleTouchDown(x, y, pointer, button)

    private fun setupWhenDialogShowing(batch: SpriteBatch) {
        clearScreen(0f, 0f, 0f, 1f)
        batch.shader = pixelateShader
        batch.setColor(aspectRatio, 1f, 1f, 1f)
    }

    private fun setupWhenDialogNotShowing(batch: SpriteBatch) {
        clearScreen(0f, 0f, 0f, 1f)
        batch.shader = null
        batch.setColor(1f, 1f, 1f, 1f)
    }

    private fun runWhenDialogShowing(delta: Float) {
        engine.update(0f)
    }

    private fun onTouchDownWhenDialogNotShowing(x: Int, y: Int, pointer: Int, button: Int): Boolean {
        val message = messageBox.getMessage<TouchDown>()?.apply { reset() } ?: TouchDown()
        message.set(x, y, pointer, button)
        messageBox.send(message)
        return true
    }

    private fun onTouchDownWhenDialogShowing(x: Int, y: Int, pointer: Int, button: Int): Boolean {
        return false
    }
}


