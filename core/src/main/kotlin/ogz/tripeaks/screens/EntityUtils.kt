package ogz.tripeaks.screens

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.utils.ImmutableArray

interface EntityUtils {
    val cardEntities: ImmutableArray<Entity>
    val stackEntity: Entity
    val discardEntity: Entity
    fun initDiscard(worldWidth: Float)
    fun updateDiscard(worldWidth: Float)
    fun moveDiscard(worldWidth: Float)
    fun initStack(worldWidth: Float)
    fun updateStack(worldWidth: Float)
    fun moveStack(worldWidth: Float)
    fun initSocket(index: Int)
    fun updateSocket(index: Int)
    fun addRemovalAnimation(socketIndex: Int)
}