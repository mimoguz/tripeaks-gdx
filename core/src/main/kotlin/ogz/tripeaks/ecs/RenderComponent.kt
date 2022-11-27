package ogz.tripeaks.ecs

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class RenderComponent: Component, Pool.Poolable {
    var color: Color = Color.WHITE
    var sprite: TextureRegion? = null
    var z: Int = 0

    override fun reset() {
        color = Color.WHITE
        sprite = null
        z = 0
    }

    companion object {
        val mapper = mapperFor<RenderComponent>()
    }
}