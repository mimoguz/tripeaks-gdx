package ogz.tripeaks.ecs

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class AnimationComponent : Component, Pool.Poolable {
    var time = 0f
    var step: (RenderComponent, TransformComponent) -> Boolean = { _, _ -> true }

    override fun reset() {
        time = 0f
        step = { _, _ -> true }
    }

    companion object {
        val mapper = mapperFor<AnimationComponent>()
    }
}