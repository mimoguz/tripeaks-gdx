package ogz.tripeaks.ecs

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor
import ogz.tripeaks.graphics.AnimationType
import ogz.tripeaks.graphics.NoAnimation

class AnimationComponent : Component, Pool.Poolable {
    var timeRemaining = 0f
    var animationType: AnimationType = NoAnimation

    override fun reset() {
        timeRemaining = 0f
        animationType = NoAnimation
    }

    companion object {
        val mapper = mapperFor<AnimationComponent>()
    }
}