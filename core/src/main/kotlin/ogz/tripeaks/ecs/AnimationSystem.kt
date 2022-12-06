package ogz.tripeaks.ecs

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import ktx.ashley.allOf
import ktx.ashley.get
import ogz.tripeaks.graphics.AnimationSet

class AnimationSystem(var animationSet: AnimationSet) : IteratingSystem(
    allOf(RenderComponent::class, TransformComponent::class, AnimationComponent::class).get()
) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity[RenderComponent.mapper]?.let { render ->
            entity[TransformComponent.mapper]?.let { transform ->
                entity[AnimationComponent.mapper]?.let { animation ->
                    val step = animation.animationType.get(animationSet)
                    if (!step(render, transform, animation, deltaTime)) {
                        engine.removeEntity(entity)
                    } else {
                        animation.timeRemaining -= deltaTime
                    }
                    Gdx.graphics.requestRendering()
                }
            }
        }
    }
}