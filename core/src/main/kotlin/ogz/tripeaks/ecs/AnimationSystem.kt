package ogz.tripeaks.ecs

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import ktx.ashley.allOf
import ktx.ashley.get

class AnimationSystem : IteratingSystem(
    allOf(RenderComponent::class, TransformComponent::class, AnimationComponent::class).get()
) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity[RenderComponent.mapper]?.let { render ->
            entity[TransformComponent.mapper]?.let { transform ->
                entity[AnimationComponent.mapper]?.let { animation ->
                    if (!animation.step(render, transform, animation)) {
                        engine.removeEntity(entity)
                    } else {
                        animation.timeRemaining -= deltaTime
                    }
                    // Gdx.graphics.requestRendering()
                }
            }
        }
    }
}