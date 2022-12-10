package ogz.tripeaks.screens

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.app.clearScreen
import ktx.ashley.getSystem
import ktx.assets.disposeSafely
import ktx.graphics.use
import ogz.tripeaks.ecs.AnimationSystem
import ogz.tripeaks.ecs.SpriteRenderingSystem
import ogz.tripeaks.graphics.Animations
import ogz.tripeaks.graphics.SpriteSet
import ogz.tripeaks.services.PooledMessageBox
import ogz.tripeaks.services.TouchDown

/**
 * Helper class to handle the secondary concerns for GameScreen.
 * It's also an input processor that relays a TouchDown message on touch.
 */
class GameScreenState(
    private val assets: AssetManager,
    private val engine: PooledEngine,
    private val messageBox: PooledMessageBox,
    private val viewport: Viewport,
    dark: Boolean = false
) : Disposable, InputAdapter() {

    private val pixelateShader = ShaderProgram(
        javaClass.classLoader.getResource("shaders/basic.vert")?.readText(),
        javaClass.classLoader.getResource("shaders/pixelate.frag")?.readText()
    )

    private var runEngine: (Float) -> Unit = engine::update
    private var handleTouchDown = this::onTouchDownWhenDialogNotShowing

    var renderScreen: (SpriteBatch, Texture) -> Unit = this::renderWhenDialogNotShowing
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
                renderScreen = this::renderWhenDialogShowing
                runEngine = this::runWhenDialogShowing
                handleTouchDown = this::onTouchDownWhenDialogShowing
            } else {
                renderScreen = this::renderWhenDialogNotShowing
                runEngine = engine::update
                handleTouchDown = this::onTouchDownWhenDialogNotShowing
            }
        }

    fun renderFrameBuffer(batch: SpriteBatch, delta: Float) {
        clearScreen(spriteSet.background.r, spriteSet.background.g, spriteSet.background.b, 1f)
        batch.shader = animationSet.shaderProgram
        batch.enableBlending()
        batch.use {
            runEngine(delta)
        }
        resetBatch(batch)
    }

    override fun dispose() {
        pixelateShader.disposeSafely()
    }

    override fun touchDown(x: Int, y: Int, pointer: Int, button: Int): Boolean = handleTouchDown(x, y, pointer, button)

    private fun renderWhenDialogShowing(batch: SpriteBatch, fbTexture: Texture) {
        clearScreen(0f, 0f, 0f, 1f)
        batch.shader = pixelateShader
        batch.use(viewport.camera) {
            it.setColor(1f, 1f, 1f, 1f)
            it.shader.setUniformf("u_worldSize", viewport.worldWidth, viewport.worldHeight)
            it.draw(
                fbTexture,
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
        resetBatch(batch)
    }

    private fun renderWhenDialogNotShowing(batch: SpriteBatch, fbTexture: Texture) {
        batch.shader = null
        batch.setColor(1f, 1f, 1f, 1f)
        batch.use(viewport.camera) {
            it.setColor(1f, 1f, 1f, 1f)
            it.draw(
                fbTexture,
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
        resetBatch(batch)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun runWhenDialogShowing(delta: Float) {
        engine.update(0f)
    }

    private fun onTouchDownWhenDialogNotShowing(x: Int, y: Int, pointer: Int, button: Int): Boolean {
        val message = messageBox.getMessage<TouchDown>()?.apply { reset() } ?: TouchDown()
        message.set(x, y, pointer, button)
        messageBox.send(message)
        return true
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onTouchDownWhenDialogShowing(x: Int, y: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    private fun resetBatch(batch: SpriteBatch) {
        batch.disableBlending()
        batch.shader = null
    }
}


