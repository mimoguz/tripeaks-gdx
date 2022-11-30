package ogz.tripeaks.ecs

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class AnimationComponent : Component, Pool.Poolable {
    var timeRemaining = 0f
    var step: (RenderComponent, TransformComponent, Float) -> Boolean = { _, _, _ -> true }

    override fun reset() {
        timeRemaining = 0f
        step = { _, _, _ -> true }
    }

    companion object {
        val mapper = mapperFor<AnimationComponent>()
    }
}