package ogz.tripeaks.ecs

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class CardRenderComponent: Component, Pool.Poolable {
    var cardIndex = -1
    var socketIndex = -1

    override fun reset() {
        cardIndex = -1
        socketIndex = -1
    }

    companion object {
        val mapper = mapperFor<CardRenderComponent>()
    }
}