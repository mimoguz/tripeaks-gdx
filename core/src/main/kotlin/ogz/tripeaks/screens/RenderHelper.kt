package ogz.tripeaks.screens

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.app.clearScreen
import ktx.assets.disposeSafely
import ktx.collections.GdxArray
import ktx.graphics.use
import ogz.tripeaks.game.AnimationView
import ogz.tripeaks.game.CardView
import ogz.tripeaks.game.DiscardView
import ogz.tripeaks.game.StackView
import ogz.tripeaks.services.SettingsService
import ogz.tripeaks.ui.GameUi

class RenderHelper(
    private val batch: SpriteBatch,
    private val viewport: Viewport,
    private val settings: SettingsService,
    private val cards: GdxArray<CardView>,
    private val animations: GdxArray<AnimationView>,
    private val discard: DiscardView,
    private val stack: StackView,
    var ui: GameUi? = null
) : Disposable {
    private var renderMethod = this::renderScreen

    private val blurShader = ShaderProgram(
        javaClass.classLoader.getResource("shaders/basic.vert")!!.readText(),
        javaClass.classLoader.getResource("shaders/pixelate.frag")!!.readText()
    )

    private var frameBuffer =
        FrameBuffer(
            Pixmap.Format.RGB888,
            Constants.MIN_WORLD_WIDTH.toInt(),
            Constants.WORLD_HEIGHT.toInt(),
            false
        )

    var blurred: Boolean = false
        set(value) {
            field = value
            renderMethod = if (value) this::renderScreenBlurred else this::renderScreen
        }

    fun render(delta: Float) {
        val currentSettings = settings.get()
        frameBuffer.begin()
        clear(currentSettings.spriteSet.background)
        batch.shader = settings.get().animationStrategy.shaderProgram
        batch.enableBlending()
        batch.use { batch ->
            batch.color = Color.WHITE
            stack.draw(batch, currentSettings.spriteSet, currentSettings.drawingStrategy)
            discard.draw(batch, currentSettings.spriteSet)
            for (card in cards) {
                card.draw(batch, currentSettings.spriteSet, currentSettings.drawingStrategy)
            }
            for (anim in animations) {
                anim.draw(batch, currentSettings.spriteSet)
            }
            batch.color = Color.WHITE
            ui?.render(batch, currentSettings.spriteSet)
        }
        frameBuffer.end(
            viewport.screenX,
            viewport.screenY,
            viewport.screenWidth,
            viewport.screenHeight
        )

        val texture = frameBuffer.colorBufferTexture
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)
        clear(currentSettings.spriteSet.background)
        renderMethod(texture)
    }

    fun update() {
        if (viewport.worldHeight > 0 && viewport.worldWidth > 0) {
            frameBuffer = FrameBuffer(
                Pixmap.Format.RGB888,
                viewport.worldWidth.toInt(),
                viewport.worldHeight.toInt(),
                false
            )
        }
    }

    override fun dispose() {
        blurShader.disposeSafely()
        frameBuffer.disposeSafely()
    }

    private fun renderScreen(texture: Texture) {
        batch.shader = null
        val w = viewport.worldWidth
        val h = viewport.worldHeight
        batch.use(viewport.camera) {
            it.draw(texture, w * -0.5f, h * -0.5f, w, h, 0f, 0f, 1f, 1f)
        }
    }

    private fun renderScreenBlurred(texture: Texture) {
        batch.shader = blurShader
        val w = viewport.worldWidth
        val h = viewport.worldHeight
        batch.use(viewport.camera) {
            it.shader.setUniformf("u_worldSize", viewport.worldWidth, viewport.worldHeight)
            it.draw(texture, w * -0.5f, h * -0.5f, w, h, 0f, 0f, 1f, 1f)
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun clear(clearColor: Color) {
        clearScreen(clearColor.r, clearColor.g, clearColor.b, clearColor.a)
    }
}