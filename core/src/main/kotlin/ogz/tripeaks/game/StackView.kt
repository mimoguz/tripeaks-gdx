package ogz.tripeaks.game

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import ktx.collections.GdxIntArray
import ktx.collections.gdxIntArrayOf
import ogz.tripeaks.graphics.SpriteSet
import ogz.tripeaks.models.Card
import ogz.tripeaks.screens.Constants.SMALL_FACE_H_PADDING
import ogz.tripeaks.screens.Constants.SMALL_FACE_WIDTH
import kotlin.math.truncate
import ogz.tripeaks.screens.Constants.FACE_HEIGHT
import ogz.tripeaks.screens.Constants.FACE_WIDTH
import ogz.tripeaks.screens.Constants.FACE_X
import ogz.tripeaks.screens.Constants.FACE_Y
import ogz.tripeaks.screens.Constants.HORIZONTAL_PADDING
import ogz.tripeaks.screens.Constants.STACK_RIGHT
import ogz.tripeaks.screens.Constants.VERTICAL_PADDING
import ogz.tripeaks.screens.Constants.WORLD_HEIGHT

class StackView {
    private val position = Vector2(0f, 0f)
    private val cardPosition = Vector2(0f, 0f)

    var stack: GdxIntArray = gdxIntArrayOf()

    fun move(worldWidth: Float) {
        position.set(
            truncate(worldWidth / 2f - STACK_RIGHT),
            truncate(WORLD_HEIGHT / -2f + VERTICAL_PADDING - 1f)
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