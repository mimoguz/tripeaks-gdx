package ogz.tripeaks.ecs

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.ashley.allOf
import ktx.ashley.get
import ogz.tripeaks.graphics.SpriteSet

class MultiSpriteRenderingSystem(private val batch: SpriteBatch, var spriteSet: SpriteSet) : SortedIteratingSystem(
    allOf(MultiSpriteComponent::class, TransformComponent::class).get(),
    compareBy { it[MultiSpriteComponent.mapper]?.z }) {

    override fun update(deltaTime: Float) {
        forceSort()
        super.update(deltaTime)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity[TransformComponent.mapper]?.let { transform ->
            entity[MultiSpriteComponent.mapper]?.let { sprites ->
                sprites.layers.forEach { layer ->
                    layer.spriteType?.let { spriteType ->
                        val sprite = spriteType.get(spriteSet)
                        batch.color = sprites.color
                        batch.draw(
                            sprite,
                            transform.position.x + layer.localPosition.x,
                            transform.position.y + layer.localPosition.y,
                            transform.origin.x,
                            transform.origin.y,
                            sprite.regionWidth.toFloat(),
                            sprite.regionHeight.toFloat(),
                            transform.scale.x,
                            transform.scale.y,
                            transform.rotation
                        )
                    }
                }
            }
        }
    }
}