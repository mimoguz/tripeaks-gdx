package ogz.tripeaks

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Logger
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.assets.disposeSafely
import ktx.assets.toInternalFile
import ktx.graphics.use
import ogz.tripeaks.graphics.CustomViewport
import ogz.tripeaks.models.GameState
import ogz.tripeaks.services.PersistenceService
import java.time.Instant

class Main : KtxGame<KtxScreen>() {
    override fun create() {
        // Gdx.graphics.isContinuousRendering = false
        addScreen(FirstScreen())
        setScreen<FirstScreen>()
    }
}

class FirstScreen : KtxScreen {
    private val card = Texture("images/card.png".toInternalFile(), true).apply { setFilter(Nearest, Nearest) }
    private val shadow = Texture("images/shadow.png".toInternalFile(), true).apply { setFilter(Nearest, Nearest) }
    private val corner = Texture("images/corner.png".toInternalFile(), true).apply { setFilter(Nearest, Nearest) }

    private val batch = SpriteBatch()
    private val viewport = CustomViewport(160, 200, 100, OrthographicCamera())
    private val cardSprite = Sprite(card)
    private val shadowSprite = Sprite(shadow)
    private val logger = Logger(FirstScreen::class.simpleName)
    private val persistence = PersistenceService()
    private var state: GameState

    private var frameBuffer = FrameBuffer(Pixmap.Format.RGB888, 160, 100, false)
    private var time: Float = 0f

    init {
        logger.level = Logger.DEBUG
        val save = persistence.loadGame()
        state = save ?: GameState()

        val fragment = javaClass.classLoader.getResource("shaders/dissolve.frag")?.readText()
        val vertex = javaClass.classLoader.getResource("shaders/dissolve.vert")?.readText()
        batch.shader = ShaderProgram(vertex, fragment)
        logger.debug(batch.shader.log)
    }

    override fun render(delta: Float) {
        state.step()
        time += delta / 3f

        viewport.apply()

        frameBuffer.begin()
        clearScreen(red = 0f, green = 1f, blue = 0f)

        batch.enableBlending()
        batch.use {
            it.setColor(1f, time % 1f, 1f, 1f)
            it.draw(shadowSprite, -12f, -19f)
            it.draw(cardSprite, -12f, -18f)
            it.setColor(1f, 0f, 1f ,1f)

            it.draw(corner, frameBuffer.width / -2f, frameBuffer.height / 2f - 10f, 10f, 10f)
            it.draw(corner, frameBuffer.width / 2f - 10f, frameBuffer.height / -2f, 10f, 10f)
        }
        batch.disableBlending()
        frameBuffer.end(viewport.screenX, viewport.screenY, viewport.screenWidth, viewport.screenHeight)

        clearScreen(red = 0f, green = 0f, blue = 1f)
        val texture = frameBuffer.colorBufferTexture
        texture.setFilter(Nearest, Nearest)
        batch.use(viewport.camera) {
            it.draw(
                frameBuffer.colorBufferTexture,
                frameBuffer.width / -2f,
                frameBuffer.height / -2f,
                frameBuffer.width.toFloat(),
                frameBuffer.height.toFloat(),
                0f,
                0f,
                1f,
                1f
            )
        }
    }

    override fun dispose() {
        card.disposeSafely()
        shadow.disposeSafely()
        corner.disposeSafely()
        batch.disposeSafely()
    }

    override fun show() {
        super.show()
        val save = persistence.loadGame()
        state = save ?: GameState.startNew((0..51).shuffled().toIntArray(), null)
        logger.info("${Instant.now()} - Started: ${state.currentState}, ${state.stalled}")
    }

    override fun pause() {
        super.pause()
        persistence.saveGame(state)
        logger.info("${Instant.now()} - Paused: ${state.currentState}, ${state.stalled}")
    }

    override fun resume() {
        super.resume()
        val save = persistence.loadGame()
        state = save ?: GameState.startNew((0..51).shuffled().toIntArray(), null)
        logger.info("${Instant.now()} - Resumed: ${state.currentState}, ${state.stalled}")
        Gdx.graphics.requestRendering()
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        viewport.update(width, height)
        if (width > 0 && height > 0) {
            frameBuffer.disposeSafely()
            frameBuffer =
                FrameBuffer(Pixmap.Format.RGB888, viewport.worldWidth.toInt(), viewport.worldHeight.toInt(), false)
        }
    }
}
