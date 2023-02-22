package ogz.tripeaks.screens

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.assets.AssetManager
import ogz.tripeaks.ecs.MultiSpriteComponent
import ogz.tripeaks.ecs.SpriteLayerPool
import ogz.tripeaks.graphics.FaceSprite
import ogz.tripeaks.graphics.SmallFaceSprite
import ogz.tripeaks.models.Card
import ogz.tripeaks.models.GameState
import ogz.tripeaks.screens.Constants.CARD_HEIGHT
import ogz.tripeaks.screens.Constants.CARD_WIDTH
import ogz.tripeaks.screens.Constants.CELL_PADDING_LEFT
import ogz.tripeaks.screens.Constants.CELL_PADDING_TOP
import ogz.tripeaks.screens.Constants.SMALL_10_HEIGHT
import ogz.tripeaks.screens.Constants.SMALL_FACE_HEIGHT
import ogz.tripeaks.screens.Constants.SMALL_FACE_H_PADDING
import ogz.tripeaks.screens.Constants.SMALL_FACE_V_PADDING
import ogz.tripeaks.screens.Constants.SMALL_FACE_WIDTH
import kotlin.math.truncate

class ShowingEntityUtils(
    game: GameState,
    engine: PooledEngine,
    layerPool: SpriteLayerPool,
    assets: AssetManager,
    cardEntities: ImmutableArray<Entity>,
    stackEntity: Entity,
    discardEntity: Entity
) : AbstractEntityUtils(
    game = game,
    engine = engine,
    layerPool = layerPool,
    assets = assets,
    cardEntities = cardEntities,
    stackEntity = stackEntity,
    discardEntity = discardEntity
) {
    private val dxBack = CARD_WIDTH + CELL_PADDING_LEFT - SMALL_FACE_WIDTH - SMALL_FACE_H_PADDING

    override fun addBack(component: MultiSpriteComponent, card: Card, dx: Float, dy: Float) {
        addFaceLayer(component, card, dx, dy)
        addSmallFaceLayer(component, card, dx + dxBack, dy + SMALL_FACE_V_PADDING)
    }

    override fun addStackBack(component: MultiSpriteComponent, card: Card, dx: Float, dy: Float) {
        component.layers.add(layerPool.obtain().apply {
            spriteType = SmallFaceSprite(card)
            val sh = if (card % 13 == 9) SMALL_10_HEIGHT else SMALL_FACE_HEIGHT
            val dyBack = CARD_HEIGHT - sh - SMALL_FACE_V_PADDING - SMALL_FACE_HEIGHT - CELL_PADDING_TOP + 1
            addSmallFaceLayer(component, card, dx + dxBack, dy + dyBack)
        })
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun addSmallFaceLayer(component: MultiSpriteComponent, card: Card, dx: Float, dy: Float) {
        component.layers.add(layerPool.obtain().apply {
            spriteType = SmallFaceSprite(card)
            localPosition.set(dx, dy)
        })
    }
}