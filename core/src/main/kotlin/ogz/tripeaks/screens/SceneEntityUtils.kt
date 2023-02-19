package ogz.tripeaks.screens

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Align
import com.ray3k.stripe.PopTable
import ktx.ashley.EngineEntity
import ktx.ashley.configureEntity
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.remove
import ktx.ashley.with
import ktx.collections.GdxIntArray
import ogz.tripeaks.assets.TextureAtlasAssets
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.assets.get
import ogz.tripeaks.ecs.AnimationComponent
import ogz.tripeaks.ecs.MultiSpriteComponent
import ogz.tripeaks.ecs.SpriteLayer
import ogz.tripeaks.ecs.SpriteLayerPool
import ogz.tripeaks.ecs.TransformComponent
import ogz.tripeaks.graphics.BackSprite
import ogz.tripeaks.graphics.CardRemovedAnimation
import ogz.tripeaks.graphics.CardSprite
import ogz.tripeaks.graphics.EmptySprite
import ogz.tripeaks.graphics.FaceSprite
import ogz.tripeaks.graphics.SmallFaceSprite
import ogz.tripeaks.models.Card
import ogz.tripeaks.screens.Constants.CARD_HEIGHT
import ogz.tripeaks.screens.Constants.CARD_WIDTH
import ogz.tripeaks.screens.Constants.CELL_PADDING_TOP
import ogz.tripeaks.screens.Constants.DISCARD_LEFT
import ogz.tripeaks.screens.Constants.DISSOLVE_TIME
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
import kotlin.math.ceil
import kotlin.math.floor

/** Helper functions for GameScreen for creating actors and entities. */
@Suppress("NOTHING_TO_INLINE", "SameParameterValue")
class SceneEntityUtils(
    private val layerPool: SpriteLayerPool,
    private val assets: AssetManager,
    private val engine: PooledEngine
) {
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
            onClick { action.invoke() }
        }

    fun undoButton(skin: UiSkin, action: () -> Unit): IconButton =
        IconButton(skin, assets[TextureAtlasAssets.Ui].findRegion("undo_${skin.resourcePostfix}")).apply {
            setSize(CARD_WIDTH.toFloat(), CARD_HEIGHT.toFloat())
            onClick { action.invoke() }
        }

    fun initDiscard(entity: Entity, discard: GdxIntArray, worldWidth: Float) {
        removeAndPoolComponents(entity)
        engine.configureEntity(entity) {
            with<TransformComponent> {
                position.set(
                    ceil(worldWidth / -2f) + DISCARD_LEFT.toFloat(),
                    (-WORLD_HEIGHT / 2 + VERTICAL_PADDING - 1).toFloat()
                )
            }
            with<MultiSpriteComponent>()
            updateDiscard(this, discard)
        }
    }

    fun updateDiscard(entity: Entity, discard: GdxIntArray) {
        engine.configureEntity(entity) { updateDiscard(this, discard) }
    }

    fun initStack(entity: Entity, stack: GdxIntArray, worldWidth: Float) {
        engine.configureEntity(entity) {
            addStackTransform(this, worldWidth)
            with<MultiSpriteComponent>()
            updateStack(this, stack, worldWidth)
        }
    }

    fun initStackShowing(entity: Entity, stack: GdxIntArray, worldWidth: Float) {
        removeAndPoolComponents(entity)
        engine.configureEntity(entity) {
            addStackTransform(this, worldWidth)
            with<MultiSpriteComponent>()
            updateStackShowing(this, stack, worldWidth)
        }
    }

    fun updateStack(entity: Entity, stack: GdxIntArray, worldWidth: Float) {
        engine.configureEntity(entity) { updateStack(this, stack, worldWidth) }
    }

    fun updateStackShowing(entity: Entity, stack: GdxIntArray, worldWidth: Float) {
        engine.configureEntity(entity) { updateStackShowing(this, stack, worldWidth) }
    }

    fun updateCardOpen(entity: Entity, card: Card, level: Int, dx: Float = 0f, dy: Float = 0f) {
        engine.configureEntity(entity) {
            with<MultiSpriteComponent> {
                z = level
                removeLayers(this)
                layers.add(baseLayer(this, dx, dy))
                layers.add(faceLayer(card, dx, dy))
            }
        }
    }

    fun updateCardClosed(entity: Entity, @Suppress("UNUSED_PARAMETER") card: Card, level: Int, dx: Float = 0f, dy: Float = 0f) {
        engine.configureEntity(entity) {
            with<MultiSpriteComponent> {
                z = level
                removeLayers(this)
                layers.add(baseLayer(this, dx, dy))
                layers.add(backLayer(dx, dy))
            }
        }
    }

    fun updateCardClosedShowing(entity: Entity, card: Card, level: Int, dx: Float = 0f, dy: Float = 0f) {
        engine.configureEntity(entity) {
            with<MultiSpriteComponent> {
                z = level
                removeLayers(this)
                layers.add(baseLayer(this, dx, dy))
                layers.add(faceLayer(card, dx, dy))
                layers.add(smallFaceLayer(card, dx, SMALL_FACE_V_PADDING))
            }
        }
    }

    fun initRemovalAnimation(card: Card, level: Int, worldPosition: Vector2) {
        val entity = engine.entity {
            with<TransformComponent> {
                position.set(worldPosition.x, worldPosition.y)
            }
            with<AnimationComponent> {
                timeRemaining = DISSOLVE_TIME
                animationType = CardRemovedAnimation
            }
        }
        updateCardOpen(entity, card, level, 0f)
    }

    fun removeAndPoolComponents(entity: Entity) {
        removeAndPoolSpriteComponent(entity)
        entity.removeAll()
    }

    fun removeAndPoolSpriteComponent(entity: Entity) {
        val sprites = entity.remove<MultiSpriteComponent>()
        if (sprites is MultiSpriteComponent) {
            sprites.layers.forEach { layerPool.free(it) }
        }
    }

    private inline fun baseLayer(component: MultiSpriteComponent, dx: Float, dy: Float): SpriteLayer =
        layerPool.obtain().apply {
            spriteType = CardSprite
            localPosition.set(dx, dy)
        }

    private inline fun faceLayer(card: Card, dx: Float, dy: Float): SpriteLayer = layerPool.obtain().apply {
        spriteType = FaceSprite(card)
        localPosition.set(
            ((CARD_WIDTH - FACE_WIDTH) / 2).toFloat() + dx,
            ((CARD_HEIGHT - FACE_HEIGHT) / 2).toFloat() + dy
        )
    }

    private inline fun smallFaceLayer(card: Card, dx: Float, dy: Float): SpriteLayer = layerPool.obtain().apply {
        spriteType = SmallFaceSprite(card)
        localPosition.set(
            (CARD_WIDTH - SMALL_FACE_WIDTH).toFloat() - SMALL_FACE_H_PADDING + dx,
            SMALL_FACE_V_PADDING + dy
        )
    }

    private inline fun backLayer(dx: Float, dy: Float): SpriteLayer = layerPool.obtain().apply {
        spriteType = BackSprite
        localPosition.set(dx, dy)
    }

    private inline fun emptyLayer(): SpriteLayer = layerPool.obtain().apply {
        spriteType = EmptySprite
    }

    private fun addStackTransform(entity: EngineEntity, width: Float) {
        entity.with<TransformComponent> {
            position.set(
                floor(width / 2f) - STACK_RIGHT.toFloat(),
                (-WORLD_HEIGHT / 2 + VERTICAL_PADDING - 1).toFloat()
            )
        }
    }

    private fun updateDiscard(entity: EngineEntity, discard: GdxIntArray) {
        entity.entity[MultiSpriteComponent.mapper]?.apply {
            removeLayers(this)
            if (discard.isEmpty) {
                layers.add(layerPool.obtain().apply {
                    spriteType = EmptySprite
                })
            } else {
                layers.add(baseLayer(this, 0f, 0f))
                layers.add(faceLayer(discard[discard.size - 1], 0f, 0f))
            }
        }
    }

    private fun updateStack(entity: EngineEntity, stack: GdxIntArray, worldWidth: Float) {
        entity.entity[MultiSpriteComponent.mapper]?.apply {
            removeLayers(this)
            if (stack.isEmpty) {
                layers.add(emptyLayer())
            } else {
                val count = stack.size - 1
                val step = 6
                for (i in 0 until count) {
                    val dx = (-i * step).toFloat()
                    layers.add(baseLayer(this, dx, 0f))
                    layers.add(backLayer(dx, 0f))
                }
                if (count >= 0) {
                    val dx = (-count * step).toFloat()
                    val card = stack[count]
                    layers.add(baseLayer(this, dx, 0f))
                    layers.add(faceLayer(card, dx, 0f))
                }
            }
        }
    }

    private fun updateStackShowing(entity: EngineEntity, stack: GdxIntArray, worldWidth: Float) {
        entity.entity[MultiSpriteComponent.mapper]?.apply {
            removeLayers(this)
            if (stack.isEmpty) {
                layers.add(emptyLayer())
            } else {
                val count = stack.size - 1
                val step = 6
                for (i in 0 until count) {
                    val card = stack[i]
                    val dx = (-i * step).toFloat()
                    val faceHeight = if ((card % 13) == 9) SMALL_10_HEIGHT else SMALL_FACE_HEIGHT
                    val dy = CARD_HEIGHT - faceHeight - SMALL_FACE_V_PADDING - SMALL_FACE_HEIGHT - CELL_PADDING_TOP + 1
                    layers.add(baseLayer(this, dx, 0f))
                    layers.add(smallFaceLayer(card, dx, dy))
                }
                if (count >= 0) {
                    val dx = (-count * step).toFloat()
                    val card = stack[count]
                    layers.add(baseLayer(this, dx, 0f))
                    layers.add(faceLayer(card, dx, 0f))
                }
            }
        }
    }

    private inline fun removeLayers(entity: Entity) {
        entity[MultiSpriteComponent.mapper]?.apply {
            layers.forEach { layerPool.free(it) }
            layers.clear()
        }
    }

    private inline fun removeLayers(component: MultiSpriteComponent) {
        component.layers.forEach { layerPool.free(it) }
        component.layers.clear()
    }
}