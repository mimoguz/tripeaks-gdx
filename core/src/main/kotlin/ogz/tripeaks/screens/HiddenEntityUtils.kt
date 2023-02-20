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

class HiddenEntityUtils(
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
            spriteType = BackSprite
            localPosition.set(dx, 0f)
        })
    }
}