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
import ogz.tripeaks.screens.Constants.SMALL_FACE_H_PADDING

class ShowingEntityUtils(
    game: GameState,
    engine: PooledEngine,
    layerPool: SpriteLayerPool,
    assets: AssetManager,
    cardEntities: ImmutableArray<Entity>,
    stackEntity: Entity,
    discardEntity: Entity
) : AbstractEntityUtils(
    game,
    engine,
    layerPool,
    assets,
    cardEntities,
    stackEntity,
    discardEntity
) {
    override fun addBack(component: MultiSpriteComponent, card: Card, dx: Float, dy: Float) {
        component.layers.add(layerPool.obtain().apply {
            spriteType = FaceSprite(card)
            localPosition.set(dx, 0f)
        })
        component.layers.add(layerPool.obtain().apply {
            spriteType = SmallFaceSprite(card)
            localPosition.set(dx - SMALL_FACE_H_PADDING, dy)
        })
    }
}