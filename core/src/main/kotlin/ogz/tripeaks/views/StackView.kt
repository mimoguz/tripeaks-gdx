package ogz.tripeaks.views

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import ktx.collections.GdxIntArray
import ktx.collections.gdxIntArrayOf
import kotlin.math.truncate
import ogz.tripeaks.graphics.SpriteSet
import ogz.tripeaks.Constants.SMALL_FACE_WIDTH
import ogz.tripeaks.Constants.STACK_RIGHT
import ogz.tripeaks.Constants.VIEWPORT_VERTICAL_PADDING
import ogz.tripeaks.Constants.WORLD_HEIGHT

class StackView {

    private val position = Vector2(0f, 0f)
    private val cardPosition = Vector2(0f, 0f)

    var stack: GdxIntArray = gdxIntArrayOf()

    fun move(worldWidth: Float) {
        position.set(
            truncate(worldWidth / 2f - STACK_RIGHT),
            truncate(WORLD_HEIGHT / -2f + VIEWPORT_VERTICAL_PADDING - 1f)
        )
    }

    fun draw(batch: SpriteBatch, sprites: SpriteSet, strategy: CardDrawingStrategy) {
        if (stack.isEmpty) {
            batch.draw(sprites.empty, position.x, position.y)
        } else {
            val last = stack.size - 1
            for (i in 0 until stack.size) {
                cardPosition.set(position.x - X_SHIFT * i, position.y)
                if (i == last) {
                    strategy.drawBack(batch, stack[i], sprites, cardPosition)
                } else {
                    strategy.drawStacked(batch, stack[i], sprites, cardPosition)
                }
            }
        }
    }

    companion object {
        const val X_SHIFT = SMALL_FACE_WIDTH + 1f
    }

}