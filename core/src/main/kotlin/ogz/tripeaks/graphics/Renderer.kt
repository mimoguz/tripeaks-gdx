package ogz.tripeaks.graphics

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.app.clearScreen
import ktx.graphics.color
import ktx.graphics.use
import ogz.tripeaks.Constants
import ogz.tripeaks.assets.ShaderSourceAssets
import ogz.tripeaks.assets.get

interface Renderer : Disposable {

    fun draw(
        batch: SpriteBatch,
        viewport: Viewport,
        clearColor: Color,
        paint: (SpriteBatch) -> Unit
    )

    fun draw(
        t: Float,
        batch: SpriteBatch,
        viewport: Viewport,
        clearColor: Color,
        paint: (SpriteBatch) -> Unit
    ): Unit = draw(batch, viewport, clearColor, paint)

    fun resize(worldWidth: Int, worldHeight: Int)

}

class SimpleRenderer : Renderer {

    override fun draw(
        batch: SpriteBatch,
        viewport: Viewport,
        clearColor: Color,
        paint: (SpriteBatch) -> Unit
    ) {
        clearScreen(clearColor.r, clearColor.g, clearColor.b, clearColor.a)
        batch.shader = null
        batch.enableBlending()
        batch.use(viewport.camera) { batch ->
            paint(batch)
        }
    }

    override fun resize(worldWidth: Int, worldHeight: Int) {
        // Pass
    }

    override fun dispose() {
        // Pass
    }

}

class BlurredRenderer(assets: AssetManager) : Renderer {

    private var frameBuffer = FrameBuffer(
        Pixmap.Format.RGB888,
        Constants.MIN_WORLD_WIDTH.toInt(),
        Constants.WORLD_HEIGHT.toInt(),
        false
    )

    private val blurShader: ShaderProgram

    init {
        val vert = assets[ShaderSourceAssets.Vert].string
        val frag = assets[ShaderSourceAssets.Pixelate].string
        blurShader = ShaderProgram(vert, frag)
    }

    override fun draw(
        batch: SpriteBatch,
        viewport: Viewport,
        clearColor: Color,
        paint: (SpriteBatch) -> Unit
    ) {
        draw(1f, batch, viewport, clearColor, paint)
    }

    override fun draw(
        t: Float,
        batch: SpriteBatch,
        viewport: Viewport,
        clearColor: Color,
        paint: (SpriteBatch) -> Unit
    ) {
        frameBuffer.begin()
        clearScreen(clearColor.r, clearColor.g, clearColor.b, clearColor.a)
        batch.shader = null
        batch.color = Color.WHITE
        batch.enableBlending()
        batch.use(viewport.camera) { batch ->
            paint(batch)
        }
        frameBuffer.end(
            viewport.screenX,
            viewport.screenY,
            viewport.screenWidth,
            viewport.screenHeight
        )

        val texture = frameBuffer.colorBufferTexture
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)
        clearScreen(clearColor.r, clearColor.g, clearColor.b, clearColor.a)
        batch.shader = blurShader
        batch.color = color(t, 1f, 1f, 1f)
        val w = viewport.worldWidth
        val h = viewport.worldHeight
        val x = MathUtils.floor(w * -0.5f).toFloat()
        val y = MathUtils.floor((h * -0.5f)).toFloat()
        batch.use(viewport.camera) { batch ->
            batch.shader.setUniformf("u_worldSize", w, h)
            batch.draw(texture, x, y, w, h, 0f, 0f, 1f, 1f)
        }
        batch.color = Color.WHITE
    }

    override fun resize(worldWidth: Int, worldHeight: Int) {
        if (worldHeight > 0 && worldWidth > 0) {
            frameBuffer = FrameBuffer(
                Pixmap.Format.RGB888,
                worldWidth,
                worldHeight,
                false
            )
        }
    }

    override fun dispose() {
        blurShader.dispose()
    }

}