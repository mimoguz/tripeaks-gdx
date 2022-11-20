package ogz.tripeaks

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
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
        Gdx.graphics.isContinuousRendering = false
        addScreen(FirstScreen())
        setScreen<FirstScreen>()
    }
}

class FirstScreen : KtxScreen {
    private val card = Texture("card.png".toInternalFile(), true).apply { setFilter(Nearest, Nearest) }
    private val shadow = Texture("shadow.png".toInternalFile(), true).apply { setFilter(Nearest, Nearest) }
    private val cardSprite = Sprite(card)
    private val shadowSprite = Sprite(shadow)
    private val corner = Texture("corner.png".toInternalFile(), true).apply { setFilter(Nearest, Nearest) }
    private val batch = SpriteBatch()
    private val logger = Logger(FirstScreen::class.simpleName)
    private val persistence = PersistenceService()
    private var state: GameState

    private var frameBuffer = FrameBuffer(Pixmap.Format.RGB888, 160, 100, false)
    private var viewport = CustomViewport(160, 200, 100, OrthographicCamera())

    init {
        logger.level = Logger.DEBUG
        val save = persistence.loadGame()
        state = save ?: GameState()
        state.unstall()
    }

    override fun render(delta: Float) {
        state.step()

        cardSprite.rotation = ((state.currentState * -10) % 360).toFloat()
        cardSprite.x = -120f + (state.currentState % 240).toFloat()
        cardSprite.y = -18f

        shadowSprite.rotation = cardSprite.rotation
        shadowSprite.x = cardSprite.x
        shadowSprite.y = cardSprite.y - 2f

        viewport.apply()

        frameBuffer.begin()
        clearScreen(red = 0f, green = 1f, blue = 0f)
        batch.enableBlending()
        batch.use {
            val alpha = 1f - (state.currentState % 240) / 240f
            shadowSprite.draw(it, alpha)
            cardSprite.draw(it, alpha)
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
        persistence.saveJson(state)
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
