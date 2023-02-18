package ogz.tripeaks.screens

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Align
import com.ray3k.stripe.PopTable
import ktx.ashley.EngineEntity
import ktx.ashley.remove
import ktx.ashley.with
import ktx.collections.GdxIntArray
import ogz.tripeaks.assets.TextureAtlasAssets
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.assets.get
import ogz.tripeaks.ecs.MultiSpriteComponent
import ogz.tripeaks.ecs.SpriteLayer
import ogz.tripeaks.ecs.SpriteLayerPool
import ogz.tripeaks.ecs.TransformComponent
import ogz.tripeaks.graphics.BackSprite
import ogz.tripeaks.graphics.CardSprite
import ogz.tripeaks.graphics.FaceSprite
import ogz.tripeaks.graphics.SmallFaceSprite
import ogz.tripeaks.models.Card
import ogz.tripeaks.screens.Constants.CARD_HEIGHT
import ogz.tripeaks.screens.Constants.CARD_WIDTH
import ogz.tripeaks.screens.Constants.CELL_PADDING_TOP
import ogz.tripeaks.screens.Constants.FACE_HEIGHT
import ogz.tripeaks.screens.Constants.FACE_WIDTH
import ogz.tripeaks.screens.Constants.SMALL_10_HEIGHT
import ogz.tripeaks.screens.Constants.SMALL_FACE_HEIGHT
import ogz.tripeaks.screens.Constants.SMALL_FACE_H_PADDING
import ogz.tripeaks.screens.Constants.SMALL_FACE_V_PADDING
import ogz.tripeaks.screens.Constants.SMALL_FACE_WIDTH
import ogz.tripeaks.screens.Constants.STACK_RIGHT
import ogz.tripeaks.screens.Constants.VERTICAL_PADDING
import ogz.tripeaks.screens.Constants.WORLD_HEIGHT
import ogz.tripeaks.ui.IconButton
import kotlin.math.floor

/** Helper functions for GameScreen for creating actors and entities. */
class SceneEntityUtils(private val layerPool: SpriteLayerPool, private val assets: AssetManager) {
    fun menuButton(stage: Stage, skin: UiSkin, assets: AssetManager, menu: PopTable, onShow: () -> Unit): IconButton =
        IconButton(skin, assets[TextureAtlasAssets.Ui].findRegion("menu_${skin.resourcePostfix}")).apply {
            setSize(CARD_WIDTH.toFloat(), CARD_WIDTH.toFloat())
            menu.attachToActor(this, Align.bottomRight, Align.bottomLeft, 0f, -VERTICAL_PADDING.toFloat())
            onClick {
                onShow.invoke()
                menu.show(stage)
            }
        }

    fun dealButton(skin: UiSkin, action: () -> Unit): IconButton =
        IconButton(skin, assets[TextureAtlasAssets.Ui].findRegion("deal_${skin.resourcePostfix}")).apply {
            setSize(CARD_WIDTH.toFloat(), CARD_HEIGHT.toFloat())
            onClick {
                action.invoke()
            }
        }

    fun undoButton(skin: UiSkin, action: () -> Unit): IconButton =
        IconButton(skin, assets[TextureAtlasAssets.Ui].findRegion("undo_${skin.resourcePostfix}")).apply {
            setSize(CARD_WIDTH.toFloat(), CARD_HEIGHT.toFloat())
            onClick {
                action.invoke()
            }
        }

    fun setupStack(entity: EngineEntity, stack: GdxIntArray, width: Float) {
        val count = stack.size
        val step = 6
        entity.with<TransformComponent> {
            position.set(
                floor(width / 2f) - STACK_RIGHT.toFloat(), (-WORLD_HEIGHT / 2 + VERTICAL_PADDING - 1).toFloat()
            )
        }
        entity.with<MultiSpriteComponent> {
            color.set(1f, 1f, 1f, 1f)
            for (i in 0 until count) {
                val dx = (-i * step).toFloat()
                layers.add(baseLayer(this, 0, dx, 0f))
                layers.add(backLayer(dx, 0f))
            }
        }
    }

    fun setupStackShowing(entity: EngineEntity, stack: GdxIntArray, width: Float) {
        val count = stack.size - 1
        val step = 6
        entity.with<TransformComponent> {
            position.set(
                floor(width / 2f) - STACK_RIGHT.toFloat(),
                (-WORLD_HEIGHT / 2 + VERTICAL_PADDING - 1).toFloat()
            )
        }
        entity.with<MultiSpriteComponent> {
            color.set(1f, 1f, 1f, 1f)
            for (i in 0 until count) {
                val card = stack[i]
                val dx = (-i * step).toFloat()
                val faceHeight = if ((card % 13) == 9) SMALL_10_HEIGHT else SMALL_FACE_HEIGHT
                val dy = CARD_HEIGHT - faceHeight - SMALL_FACE_V_PADDING - SMALL_FACE_HEIGHT - CELL_PADDING_TOP + 1
                layers.add(baseLayer(this, 0, dx, 0f))
                layers.add(smallFaceLayer(card, dx, dy))
            }
            if (count >= 0) {
                val dx = (-count * step).toFloat()
                val card = stack[count]
                layers.add(baseLayer(this, 0, dx, 0f))
                layers.add(faceLayer(card, dx, 0f))
            }
        }
    }

    fun setupCardOpen(entity: EngineEntity, card: Card, level: Int, dx: Float = 0f, dy: Float = 0f) {
        entity.with<MultiSpriteComponent> {
            z = level
            layers.add(baseLayer(this, level, dx, dy))
            baseLayer(this, level, dx, dy)
            layers.add(faceLayer(card, dx, dy))
        }
    }

    fun setupCardClosed(entity: EngineEntity, level: Int, dx: Float = 0f, dy: Float = 0f) {
        entity.with<MultiSpriteComponent> {
            z = level
            layers.add(baseLayer(this, level, dx, dy))
            layers.add(backLayer(dx, dy))
        }
    }

    fun setupCardClosedShowing(entity: EngineEntity, card: Card, level: Int, dx: Float = 0f, dy: Float = 0f) {
        entity.with<MultiSpriteComponent> {
            z = level
            layers.add(baseLayer(this, level, dx, dy))
            layers.add(faceLayer(card, dx, dy))
            layers.add(smallFaceLayer(card, dx, SMALL_FACE_V_PADDING))
        }
    }

    fun removeAndPoolComponents(entity: Entity): Unit {
        val sprites = entity.remove<MultiSpriteComponent>()
        if (sprites is MultiSpriteComponent) {
            sprites.layers.forEach { layerPool.free(it) }
        }
        entity.removeAll()
    }

    private fun baseLayer(component: MultiSpriteComponent, level: Int, dx: Float, dy: Float): SpriteLayer =
        layerPool.obtain().apply {
            spriteType = CardSprite
            localPosition.set(dx, dy)
        }

    private fun faceLayer(card: Card, dx: Float, dy: Float): SpriteLayer = layerPool.obtain().apply {
        spriteType = FaceSprite(card)
        localPosition.set(
            ((CARD_WIDTH - FACE_WIDTH) / 2).toFloat() + dx,
            ((CARD_HEIGHT - FACE_HEIGHT) / 2).toFloat() + dy
        )
    }

    private fun smallFaceLayer(card: Card, dx: Float, dy: Float): SpriteLayer = layerPool.obtain().apply {
        spriteType = SmallFaceSprite(card)
        localPosition.set(
            (CARD_WIDTH - SMALL_FACE_WIDTH).toFloat() - SMALL_FACE_H_PADDING + dx,
            SMALL_FACE_V_PADDING + dy
        )
    }

    private fun backLayer(dx: Float, dy: Float): SpriteLayer = layerPool.obtain().apply {
        spriteType = BackSprite
        localPosition.set(dx, dy)
    }
}