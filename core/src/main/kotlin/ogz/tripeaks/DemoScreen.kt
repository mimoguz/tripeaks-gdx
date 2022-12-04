package ogz.tripeaks

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
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
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.ashley.entity
import ktx.ashley.getSystem
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
import ogz.tripeaks.graphics.AnimationSet
import ogz.tripeaks.graphics.CardRemovedAnimation
import ogz.tripeaks.graphics.CardSprite
import ogz.tripeaks.graphics.CustomViewport
import ogz.tripeaks.graphics.FaceRemovedAnimation
import ogz.tripeaks.graphics.FaceSprite
import ogz.tripeaks.graphics.HomeSprite
import ogz.tripeaks.graphics.ScreenTransitionAnimation
import ogz.tripeaks.graphics.SpriteSet

class DemoScreen(private val assets: AssetManager) : KtxScreen {

    private val batch = SpriteBatch()
    private val viewport = CustomViewport(MIN_WORLD_WIDTH, MAX_WORLD_WIDTH, WORLD_HEIGHT, OrthographicCamera())
    private val uiStage = Stage(CustomViewport(MIN_WORLD_WIDTH, MAX_WORLD_WIDTH, WORLD_HEIGHT, OrthographicCamera()))
    private val engine = PooledEngine()
    private val animationSet: AnimationSet

    private var spriteSet = SpriteSet(false, 0, assets)
    private var frameBuffer = FrameBuffer(Pixmap.Format.RGB888, MIN_WORLD_WIDTH, WORLD_HEIGHT, false)
    private var isDark = false
    private var time = 0f

    init {
        val faceHeight = FaceSprite(1).get(spriteSet).regionHeight
        val cardHeight = CardSprite.get(spriteSet).regionHeight

        val fragment = javaClass.classLoader.getResource("shaders/dissolve.frag")?.readText()
        val vertex = javaClass.classLoader.getResource("shaders/dissolve.vert")?.readText()

        animationSet = AnimationSet(
            cardRemoved = { render, transform, animation, delta ->
                val st = animation.timeRemaining % 2f
                if (st > 1f) {
                    transform.position.set(transform.position.x, cardHeight * -0.5f)
                    transform.scale.set(1.001f, 1.001f)
                    render.color.set(render.color.r, 1f, 1f, 1f)
                } else {
                    val rt = 1.0f - st
                    render.color.set(render.color.r, st, 1f, 1f)
                    transform.position.set(transform.position.x, transform.position.y - delta * rt * 400f)
                    transform.scale.set(transform.scale.x + delta * rt, transform.scale.y + delta * 6f * rt)
                }
                animation.timeRemaining > 0f
            },

            faceRemoved = { render, transform, animation, delta ->
                val st = animation.timeRemaining % 2f
                if (st > 1f) {
                    transform.position.set(transform.position.x, faceHeight * -0.5f)
                    transform.scale.set(1f, 1f)
                    render.color.set(render.color.r, 1f, 1f, 1f)
                } else {
                    val rt = 1.0f - st
                    render.color.set(render.color.r, st, 1f, 1f)
                    transform.position.set(transform.position.x, transform.position.y - delta * (1.0f - st) * 350f)
                    transform.scale.set(
                        transform.scale.x + delta * rt,
                        transform.scale.y + delta * 8f * rt
                    )
                }
                animation.timeRemaining > 0f
            },

            screenTransition = { render, _, animation, _ ->
                if (animation.timeRemaining <= 0.5f) {
                    render.color.set(render.color.r, animation.timeRemaining * 2f, 1f, 1f)
                }
                animation.timeRemaining >= 0
            },

            shaderProgram = ShaderProgram(vertex, fragment)
        )
    }

    override fun render(delta: Float) {
        viewport.apply()
        uiStage.viewport.apply()
        uiStage.act(delta)
        time = (time + delta) % 1f

        frameBuffer.begin()
        clearScreen(spriteSet.background.r, spriteSet.background.g, spriteSet.background.b, 1f)
        batch.shader = animationSet.shaderProgram
        batch.enableBlending()
        batch.use {
            engine.update(delta)
        }
        batch.disableBlending()
        batch.shader = null
        frameBuffer.end(viewport.screenX, viewport.screenY, viewport.screenWidth, viewport.screenHeight)

        clearScreen(0f, 0f, 0f, 1f)
        val texture = frameBuffer.colorBufferTexture
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)
        batch.use(viewport.camera) {
            it.setColor(1f, 1f, 1f, 1f)
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
            it.setColor(1f, 1f, 1f, 1f)
        }
        uiStage.draw()
    }

    override fun show() {
        super.show()
        Gdx.input.inputProcessor = uiStage
        setupStage()
        setupECS()
    }

    override fun dispose() {
        assets.disposeSafely()
        batch.disposeSafely()
        uiStage.disposeSafely()
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        viewport.update(width, height)
        uiStage.viewport.update(width, height, true)
        frameBuffer =
            FrameBuffer(Pixmap.Format.RGB888, viewport.worldWidth.toInt(), viewport.worldHeight.toInt(), false)
    }

    private fun switchSkin() {
        isDark = !isDark
        Scene2DSkin.defaultSkin =
            if (isDark)
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
        spriteSet = SpriteSet(isDark, 0, assets)
        engine.getSystem<SpriteRenderingSystem>().spriteSet = spriteSet
        setupStage()
    }

    private fun setupStage() {
        uiStage.clear()

        val container = Table(Scene2DSkin.defaultSkin).apply {
            align(Align.bottomLeft)
            val button = TextButton("Switch Theme", Scene2DSkin.defaultSkin).apply {
                addListener(object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {
                        switchSkin()
                    }
                })
            }
            add(button)
        }

        uiStage.actors.add(container)
    }

    private fun setupECS() {
        engine.apply {
            removeAllSystems()
            removeAllEntities()
            addSystem(AnimationSystem(animationSet))
            addSystem(SpriteRenderingSystem(batch, spriteSet))
        }

        engine.entity {
            val spriteType = HomeSprite
            val sprite = spriteType.get(spriteSet)

            with<TransformComponent> {
                position = Vector2(sprite.regionWidth * -0.5f, sprite.regionHeight * -0.5f)
            }

            with<RenderComponent> {
                this.spriteType = spriteType
                color.set(0.01f, 1f, 1f, 1f)
                z = 10
            }

            with<AnimationComponent> {
                timeRemaining = 5f
                animationType = ScreenTransitionAnimation
            }
        }

        // Card
        engine.entity {
            val spriteType = CardSprite
            val sprite = spriteType.get(spriteSet)

            with<TransformComponent> {
                origin = Vector2(
                    MathUtils.floor(sprite.regionWidth * 0.5f).toFloat(),
                    MathUtils.floor(sprite.regionHeight * 0.5f).toFloat()
                )
                position = origin.cpy().scl(-1f, -1f)
            }

            with<RenderComponent> {
                this.spriteType = spriteType
                this.color.set(0.8f, 1f, 1f, 1f)
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
            val sprite = spriteType.get(spriteSet)
            with<TransformComponent> {
                origin = Vector2(
                    MathUtils.floor(sprite.regionWidth * 0.5f).toFloat(),
                    MathUtils.floor(sprite.regionHeight * 0.5f).toFloat()
                )
                position = origin.cpy().scl(-1f, -1f)
            }
            with<RenderComponent> {
                this.spriteType = spriteType
                this.color.set(1f, 1f, 1f, 1f)
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