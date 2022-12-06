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
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.ashley.entity
import ktx.ashley.get
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
import ogz.tripeaks.graphics.Animations
import ogz.tripeaks.graphics.CardRemovedAnimation
import ogz.tripeaks.graphics.CardSprite
import ogz.tripeaks.graphics.CustomViewport
import ogz.tripeaks.graphics.FaceRemovedAnimation
import ogz.tripeaks.graphics.FaceSprite
import ogz.tripeaks.graphics.HomeSprite
import ogz.tripeaks.graphics.ScreenTransitionAnimation
import ogz.tripeaks.graphics.SpriteSet
import ogz.tripeaks.ui.LabelButton

class DemoScreen(private val assets: AssetManager) : KtxScreen {

    private val batch = SpriteBatch()
    private val viewport = CustomViewport(MIN_WORLD_WIDTH, MAX_WORLD_WIDTH, WORLD_HEIGHT, OrthographicCamera())
    private val uiStage = Stage(CustomViewport(MIN_WORLD_WIDTH, MAX_WORLD_WIDTH, WORLD_HEIGHT, OrthographicCamera()))
    private val engine = PooledEngine()

    private var animationSet = Animations.Blinds
    private var spriteSet = SpriteSet(false, 0, assets)
    private var frameBuffer = FrameBuffer(Pixmap.Format.RGB888, MIN_WORLD_WIDTH, WORLD_HEIGHT, false)
    private var isDark = false
    private var time = 0f

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

    private fun switchAnimation() {
        animationSet = if (animationSet === Animations.Dissolve) Animations.Blinds else Animations.Dissolve
        engine.getSystem<AnimationSystem>().animationSet = animationSet
    }

    private fun setupStage() {
        uiStage.clear()

        val themeButton = LabelButton(Scene2DSkin.defaultSkin, "Switch Theme")
        themeButton.onClick(this::switchSkin)

        val animationButton = LabelButton(Scene2DSkin.defaultSkin, "Switch Animation")
        animationButton.onClick(this::switchAnimation)

        val table = Table(Scene2DSkin.defaultSkin).apply {
            align(Align.bottomLeft)
            add(themeButton).align(Align.bottomLeft)
            row()
            add(animationButton).align(Align.bottomLeft)
        }

        uiStage.actors.add(table)
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
                color.set(0.1f, 1f, 1f, 1f)
                z = 10
            }

            with<AnimationComponent> {
                timeRemaining = 2f
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
                this.color.set(0.02f, 1f, 1f, 1f)
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
                this.color.set(0.01f, 1f, 1f, 1f)
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