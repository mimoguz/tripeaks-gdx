package ogz.tripeaks.ecs

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.ashley.allOf
import ktx.ashley.get

class SpriteRenderingSystem(private val batch: SpriteBatch) : SortedIteratingSystem(
    allOf(RenderComponent::class, TransformComponent::class).get(),
    compareBy { it[RenderComponent.mapper]?.z }) {

    override fun update(deltaTime: Float) {
        forceSort()
        super.update(deltaTime)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity[RenderComponent.mapper]?.let { render ->
            render.sprite?.let { sprite ->
                entity[TransformComponent.mapper]?.let { transform ->
                    batch.setColor(render.color.r, render.color.g, render.color.b, render.color.a)
                    batch.draw(
                        sprite,
                        transform.position.x,
                        transform.position.y,
                        transform.origin.x,
                        transform.origin.y,
                        sprite.regionWidth.toFloat(),
                        sprite.regionHeight.toFloat(),
                        1f,
                        1f,
                        transform.rotation
                    )
                }
            }
        }
    }
}