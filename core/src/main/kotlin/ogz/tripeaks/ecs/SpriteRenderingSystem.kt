package ogz.tripeaks.ecs

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.assets.AssetManager
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
                    batch.run {
                        color = render.color
                        draw(
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
}