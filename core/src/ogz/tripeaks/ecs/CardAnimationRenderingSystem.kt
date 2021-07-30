package ogz.tripeaks.ecs

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.remove
import ogz.tripeaks.Const
import ogz.tripeaks.game.GameState
import ogz.tripeaks.util.SpriteCollection
import kotlin.math.roundToInt

class CardAnimationRenderingSystem(
    private val batch: Batch,
    private val sprites: SpriteCollection,
    private val state: GameState
) :
    SortedIteratingSystem(allOf(CardAnimationComponent::class).get(), compareBy {
        it[CardAnimationComponent.mapper]?.let { component -> state.layout[component.socketIndex].z }
    }) {

    override fun update(deltaTime: Float) {
        forceSort()
        super.update(deltaTime)
    }

    private val backupPosition = Vector2(0f, 0f)

    private val startX = ((Const.CONTENT_WIDTH - Const.CELL_WIDTH * state.layout.numberOfColumns) / 2f + 0.5f)
        .roundToInt()
        .toFloat()


    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity[CardAnimationComponent.mapper]?.let { component ->
            component.time += deltaTime
            if (component.time >= ANIMATION_TIME) entity.remove<CardAnimationComponent>()
            else {
                val socket = state.layout[component.socketIndex]
                val t = component.time / ANIMATION_TIME
                val x = socket.column * Const.CELL_WIDTH + startX + Const.SPRITE_X
                val srcY = Const.CONTENT_HEIGHT -
                        socket.row * Const.CELL_HEIGHT -
                        2f * Const.CELL_HEIGHT -
                        Const.VERTICAL_PADDING -
                        Const.SPRITE_Y
                val y = MathUtils.round(srcY - t * DELTA_Y).toFloat()
                val alpha = 1f - t

                backupPosition.set(sprites.plate.x, sprites.plate.y)
                sprites.plate.setPosition(MathUtils.round(x).toFloat(), MathUtils.round(y).toFloat())
                sprites.plate.draw(batch, alpha)
                sprites.plate.setPosition(backupPosition.x, backupPosition.y)

                val face = sprites.faces[component.cardIndex]
                backupPosition.set(face.x, face.y)
                face.setPosition(x + Const.FACE_X, y + Const.FACE_Y)
                face.draw(batch, alpha)
                face.setPosition(backupPosition.x, backupPosition.y)
            }
        }
    }

    companion object {
        const val ANIMATION_TIME = 0.5f
        const val DELTA_Y = 50f
    }
}