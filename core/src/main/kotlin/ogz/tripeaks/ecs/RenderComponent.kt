package ogz.tripeaks.ecs

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor
import ogz.tripeaks.graphics.SpriteType

class RenderComponent: Component, Pool.Poolable {
    var color: Color = Color(1f, 1f, 1f, 1f)
    var spriteType: SpriteType? = null
    var z: Int = 0

    override fun reset() {
        color.set(1f, 1f, 1f, 1f)
        spriteType = null
        z = 0
    }

    companion object {
        val mapper = mapperFor<RenderComponent>()
    }
}