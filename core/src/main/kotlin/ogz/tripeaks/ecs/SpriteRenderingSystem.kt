package ogz.tripeaks.ecs

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.ashley.allOf
import ktx.ashley.get
import ogz.tripeaks.graphics.SpriteSet

class SpriteRenderingSystem(private val batch: SpriteBatch, private val spriteSet: SpriteSet) : SortedIteratingSystem(
    allOf(RenderComponent::class, TransformComponent::class).get(),
    compareBy { it[RenderComponent.mapper]?.z }) {

    override fun update(deltaTime: Float) {
        forceSort()
        super.update(deltaTime)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity[RenderComponent.mapper]?.let { render ->
            render.spriteType?.let { spriteType ->
                entity[TransformComponent.mapper]?.let { transform ->
                    batch.color = render.color
                    val sprite = spriteType.get(spriteSet)
                    batch.draw(
                        sprite,
                        transform.position.x,
                        transform.position.y,
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