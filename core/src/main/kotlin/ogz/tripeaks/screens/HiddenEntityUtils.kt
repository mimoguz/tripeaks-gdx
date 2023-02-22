package ogz.tripeaks.screens

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.assets.AssetManager
import ogz.tripeaks.ecs.MultiSpriteComponent
import ogz.tripeaks.ecs.SpriteLayerPool
import ogz.tripeaks.graphics.BackSprite
import ogz.tripeaks.models.Card
import ogz.tripeaks.models.GameState
import ogz.tripeaks.screens.Constants.SMALL_10_HEIGHT
import ogz.tripeaks.screens.Constants.SMALL_FACE_HEIGHT
import ogz.tripeaks.screens.Constants.SMALL_FACE_H_PADDING
import ogz.tripeaks.screens.Constants.SMALL_FACE_V_PADDING

class HiddenEntityUtils(
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
    override fun addBack(component: MultiSpriteComponent, card: Card, dx: Float, dy: Float) {
        component.layers.add(layerPool.obtain().apply {
            spriteType = BackSprite
            localPosition.set(dx, dy)
        })
    }

    override fun addStackBack(component: MultiSpriteComponent, card: Card, dx: Float, dy: Float) {
        component.layers.add(layerPool.obtain().apply {
            spriteType = BackSprite
            localPosition.set(dx, dy)
        })
    }
}