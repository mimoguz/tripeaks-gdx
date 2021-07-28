package ogz.tripeaks.ecs

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class CardAnimationComponent : Component, Pool.Poolable {
    var cardIndex = -1
    var socketIndex = -1
    var time = 0.0f

    override fun reset() {
        cardIndex = -1
        socketIndex = -1
        time = 0.0f
    }

    companion object {
        val mapper = mapperFor<CardAnimationComponent>()
    }
}