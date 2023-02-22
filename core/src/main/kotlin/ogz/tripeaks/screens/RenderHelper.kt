package ogz.tripeaks.screens

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.app.clearScreen
import ktx.assets.disposeSafely
import ktx.graphics.use
import ogz.tripeaks.ui.GameUi

class RenderHelper(private val batch: SpriteBatch, private val viewport: Viewport, private val engine: PooledEngine, val ui: GameUi? = null) : Disposable {
    private var renderMethod = this::renderScreen
    private val blurShader = ShaderProgram(
        javaClass.classLoader.getResource("shaders/basic.vert")!!.readText(),
        javaClass.classLoader.getResource("shaders/pixelate_avg.frag")!!.readText()
    )

    var fbShader: ShaderProgram? = null

    var clearColor: Color = Color(0f, 0f, 0f, 1f)

    var blurred: Boolean = false
        set(value) {
            field = value
            renderMethod = if (value) this::renderScreenBlurred else this::renderScreen
        }

    fun render(frameBuffer: FrameBuffer, delta: Float) {
        frameBuffer.begin()
        clear()
        batch.shader = fbShader
        batch.enableBlending()
        batch.use {
            engine.update(delta)
            batch.setColor(1f, 1f ,1f, 1f)
            ui?.render(it)
        }
        frameBuffer.end(viewport.screenX, viewport.screenY, viewport.screenWidth, viewport.screenHeight)

        val texture = frameBuffer.colorBufferTexture
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)
        renderMethod(texture)
    }

    override fun dispose() {
        blurShader.disposeSafely()
    }

    private fun renderScreen(texture: Texture) {
        clear()
        batch.shader = null
        val w = viewport.worldWidth
        val h = viewport.worldHeight
        batch.use(viewport.camera) {
            it.draw(texture, w * -0.5f, h * -0.5f, w, h, 0f, 0f, 1f, 1f)
        }
    }

    private fun renderScreenBlurred(texture: Texture) {
        clear()
        batch.shader = blurShader
        val w = viewport.worldWidth
        val h = viewport.worldHeight
        batch.use(viewport.camera) {
            it.shader.setUniformf("u_worldSize", viewport.worldWidth, viewport.worldHeight)
            it.draw(texture, w * -0.5f, h * -0.5f, w, h, 0f, 0f, 1f, 1f)
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun clear() {
        clearScreen(clearColor.r, clearColor.g, clearColor.b,  clearColor.a)
    }
}