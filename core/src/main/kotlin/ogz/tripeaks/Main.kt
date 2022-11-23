package ogz.tripeaks

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Logger
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.assets.disposeSafely
import ktx.graphics.use
import ogz.tripeaks.assets.TextureAtlasAssets
import ogz.tripeaks.assets.get
import ogz.tripeaks.assets.load
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
    private val assets = AssetManager()
    private val batch = SpriteBatch()
    private val viewport = CustomViewport(160, 200, 100, OrthographicCamera())
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
        if (!assets.isFinished) {
            assets.update()
            Gdx.graphics.requestRendering()
            return
        }

        state.step()
        time += delta

        viewport.apply()

        val cards = assets[TextureAtlasAssets.Cards]
        val back = cards.findRegion("light_card")
        val face = cards.findRegion("light_card_1")

        frameBuffer.begin()
        clearScreen(0.388235f, 0.662745f, 0.278431f, 1f)
        batch.enableBlending()
        batch.use {
            it.setColor(1f, time % 1f, 1f, 1f)
            it.draw(
                back,
                -12f,
                -18f - (time % 1f) * 50f,
                back.originalWidth / 2f,
                back.originalHeight / 2f,
                back.originalWidth.toFloat(),
                back.originalHeight.toFloat(),
                1f,
                1f,
                (time * 540f) % 360f
            )
            it.draw(
                face,
                -12f + 5f,
                -18f + 4f - (time % 1f) * 50f,
                face.originalWidth / 2f - 6f * (time % 1f),
                face.originalHeight / 2f + 16f * (time % 1f),
                face.originalWidth.toFloat(),
                face.originalHeight.toFloat(),
                1f,
                1f,
                (time * 540f) % 360f
            )
            it.setColor(1f, 0f, 1f, 1f)
        }
        batch.disableBlending()
        frameBuffer.end(viewport.screenX, viewport.screenY, viewport.screenWidth, viewport.screenHeight)

        clearScreen(0f, 0f, 0f, 1f)
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
        assets.disposeSafely()
        batch.disposeSafely()
    }

    override fun show() {
        super.show()

        for (asset in TextureAtlasAssets.values()) {
            assets.load(asset)
        }

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
