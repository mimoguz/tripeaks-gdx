package ogz.tripeaks

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Logger
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.ashley.entity
import ktx.ashley.with
import ktx.assets.disposeSafely
import ktx.graphics.moveTo
import ktx.graphics.use
import ktx.scene2d.Scene2DSkin
import ogz.tripeaks.assets.FontAssets
import ogz.tripeaks.assets.TextureAssets
import ogz.tripeaks.assets.TextureAtlasAssets
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.assets.get
import ogz.tripeaks.ecs.AnimationComponent
import ogz.tripeaks.ecs.AnimationSystem
import ogz.tripeaks.ecs.RenderComponent
import ogz.tripeaks.ecs.SpriteRenderingSystem
import ogz.tripeaks.ecs.TransformComponent
import ogz.tripeaks.graphics.CustomViewport
import ogz.tripeaks.models.GameState
import ogz.tripeaks.services.PersistenceService
import java.time.Instant

class DemoScreen(private val assets: AssetManager) : KtxScreen {

    private val batch = SpriteBatch()
    private val dissolveShader: ShaderProgram
    private val viewport = CustomViewport(160, 200, 100, OrthographicCamera())
    private val uiStage = Stage(CustomViewport(160, 200, 100, OrthographicCamera()))
    private val logger = Logger(DemoScreen::class.simpleName)
    private val persistence = PersistenceService()
    private val engine = PooledEngine()
    private var state: GameState
    private var container = Table(Scene2DSkin.defaultSkin)

    private var frameBuffer = FrameBuffer(Pixmap.Format.RGB888, 160, 100, false)
    private var isDark = false

    init {
        logger.level = Logger.DEBUG
        val save = persistence.loadGame()
        state = save ?: GameState()

        val fragment = javaClass.classLoader.getResource("shaders/dissolve.frag")?.readText()
        val vertex = javaClass.classLoader.getResource("shaders/dissolve.vert")?.readText()
        dissolveShader = ShaderProgram(vertex, fragment)
        logger.debug(batch.shader.log)
    }

    override fun render(delta: Float) {
        state.step()
        viewport.apply()
        uiStage.viewport.apply()
        uiStage.act(delta)

        batch.shader = dissolveShader
        frameBuffer.begin()
        clearScreen(0.388235f, 0.662745f, 0.278431f, 1f)
        batch.enableBlending()
        batch.use {
            engine.update(delta)
            it.setColor(1f, 1f, 1f, 1f)
        }
        batch.disableBlending()
        frameBuffer.end(viewport.screenX, viewport.screenY, viewport.screenWidth, viewport.screenHeight)

        batch.shader = null
        clearScreen(0f, 0f, 0f, 1f)
        val texture = frameBuffer.colorBufferTexture
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)
        batch.use(viewport.camera) {
            it.draw(
                frameBuffer.colorBufferTexture,
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
        uiStage.draw()
    }

    override fun dispose() {
        assets.disposeSafely()
        batch.disposeSafely()
        uiStage.disposeSafely()
    }

    override fun show() {
        super.show()
        val save = persistence.loadGame()
        state = save ?: GameState.startNew((0..51).shuffled().toIntArray(), null)
        logger.info("${Instant.now()} - Started: ${state.currentState}, ${state.stalled}")
        setupStage()
        Gdx.input.inputProcessor = uiStage
        setupECS()
    }

    private fun setupStage() {
        uiStage.clear()
        container = Table(Scene2DSkin.defaultSkin).apply {
            align(Align.bottomLeft)
            setSize(viewport.worldWidth, viewport.worldHeight)
            add(TextButton("Bottom-left", Scene2DSkin.defaultSkin)).expand().align(Align.bottomLeft)
            add(TextButton("Top-right", Scene2DSkin.defaultSkin).apply {
                addListener(object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {
                        isDark = !isDark
                        Scene2DSkin.defaultSkin =
                            if (isDark)
                                UiSkin(
                                    assets[TextureAtlasAssets.Ui],
                                    assets[FontAssets.GamePixels],
                                    Color(242f / 255f, 204f / 255f, 143f / 255f, 1f),
                                    Color(184f / 255f, 55f / 255f, 68f / 255f, 1f),
                                    "dark"
                                )
                            else
                                UiSkin(
                                    assets[TextureAtlasAssets.Ui],
                                    assets[FontAssets.GamePixels],
                                    Color(76f / 244f, 56f / 255f, 77f / 255f, 1f),
                                    Color(224 / 244f, 122f / 255f, 95f / 255f, 1f),
                                    "light"
                                )
                        setupStage()
                    }
                })
            }).expand().align(Align.topRight)
        }
        uiStage.actors.add(container)
    }

    private fun setupECS() {
        val entities = engine.entities.toList()
        entities.forEach(engine::removeEntity)
        engine.apply {
            removeAllSystems()
            addSystem(AnimationSystem())
            addSystem(SpriteRenderingSystem(batch))
        }

        engine.entity {
            with<TransformComponent> {
                position = Vector2(viewport.worldWidth * -0.5f, viewport.worldHeight * -0.5f)
            }
            with<RenderComponent> {
                sprite = Sprite(assets[TextureAssets.LightTitle])
                color.set(0.01f, 1f, 1f ,1f)
                z = 10
            }
            with<AnimationComponent> {
                timeRemaining = 3f
                step = { render, _, animation ->
                    render.color.set(0.01f, 1f - animation.timeRemaining / 3f, 1f, 1f)
                    animation.timeRemaining >= 0
                }
            }
        }

        val cardDissolve = { render: RenderComponent, transform: TransformComponent, animation: AnimationComponent ->
            val t = animation.timeRemaining % 1f
            render.color.set(render.color.r, 1f - t, render.color.g, render.color.a)
            transform.rotation = t * 360f
            animation.timeRemaining >= 0f
        }

        engine.entity {
            val sprite = assets[TextureAtlasAssets.Cards].findRegion("light_card")
            with<TransformComponent> {
                origin = Vector2(
                    MathUtils.floor(sprite.regionWidth * 0.5f).toFloat(),
                    MathUtils.floor(sprite.regionHeight * 0.5f).toFloat()
                )
                position = origin.cpy().scl(-1f, -1f)
            }
            with<RenderComponent> {
                this.sprite = sprite
                color.set(0.5f, 1f, 1f, 1f)
                z = 1
            }
            with<AnimationComponent> {
                timeRemaining = 10000f
                step = cardDissolve
            }
        }

        engine.entity {
            val sprite = assets[TextureAtlasAssets.Cards].findRegion("light_card_1")
            with<TransformComponent> {
                origin = Vector2(
                    MathUtils.floor(sprite.regionWidth * 0.5f).toFloat(),
                    MathUtils.floor(sprite.regionHeight * 0.5f).toFloat()
                )
                position = origin.cpy().scl(-1f, -1f)
            }
            with<RenderComponent> {
                this.sprite = sprite
                color.set(1f, 1f, 1f, 1f)
                z = 2
            }
            with<AnimationComponent> {
                timeRemaining = 10000f
                step = cardDissolve
            }
        }
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

        uiStage.viewport.update(width, height)
        // UI alignment hack
        uiStage.viewport.camera.moveTo(Vector2(viewport.worldWidth * 0.5f, viewport.worldHeight * 0.5f))
        container.setSize(viewport.worldWidth, viewport.worldHeight)
        container.invalidate()
        // end UI alignment hack

        if (width > 0 && height > 0) {
            frameBuffer.disposeSafely()
            frameBuffer = FrameBuffer(
                Pixmap.Format.RGB888,
                viewport.worldWidth.toInt(),
                viewport.worldHeight.toInt(),
                false
            )
        }
    }
}