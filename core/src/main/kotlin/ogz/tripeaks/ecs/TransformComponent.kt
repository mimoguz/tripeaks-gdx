package ogz.tripeaks.ecs

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g3d.utils.BaseAnimationController.Transform
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class TransformComponent : Component, Pool.Poolable {
    var position = Vector2(0f, 0f)
    var origin = Vector2(0f, 0f)
    var rotation = 0f

    override fun reset() {
        position = Vector2(0f, 0f)
        origin = Vector2(0f, 0f)
        rotation = 0f
    }

    companion object {
        val mapper = mapperFor<TransformComponent>()
    }
}