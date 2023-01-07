package ogz.tripeaks.ecs

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor
import ktx.collections.GdxArray
import ktx.collections.gdxArrayOf
import ogz.tripeaks.graphics.SpriteType

class MultiSpriteComponent: Component, Pool.Poolable {
    var color: Color = Color(1f, 1f, 1f, 1f)
    val layers: GdxArray<SpriteLayer> = gdxArrayOf()
    var z: Int = 0

    override fun reset() {
        color.set(1f, 1f, 1f, 1f)
        layers.clear()
        z = 0
    }

    companion object {
        val mapper = mapperFor<MultiSpriteComponent>()
    }
}

class SpriteLayer: Pool.Poolable {
    var spriteType: SpriteType? = null
    val localPosition: Vector2 = Vector2()

    override fun reset() {
        spriteType = null
        localPosition.set(0f, 0f)
    }
}

class SpriteLayerPool: Pool<SpriteLayer>() {
    override fun newObject(): SpriteLayer = SpriteLayer()
}