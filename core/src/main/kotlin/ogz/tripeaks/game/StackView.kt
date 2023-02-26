package ogz.tripeaks.game

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import ktx.collections.GdxIntArray
import ktx.collections.gdxIntArrayOf
import ogz.tripeaks.graphics.SpriteSet
import ogz.tripeaks.models.Card
import ogz.tripeaks.screens.Constants
import ogz.tripeaks.screens.Constants.SMALL_FACE_H_PADDING
import ogz.tripeaks.screens.Constants.SMALL_FACE_WIDTH
import kotlin.math.truncate
import ogz.tripeaks.screens.Constants.FACE_X
import ogz.tripeaks.screens.Constants.FACE_Y
import ogz.tripeaks.screens.Constants.HORIZONTAL_PADDING
import ogz.tripeaks.screens.Constants.VERTICAL_PADDING
import ogz.tripeaks.screens.Constants.WORLD_HEIGHT

class StackView {
    private val position = Vector2(0f, 0f)
    private val cardPosition = Vector2(0f, 0f)

    var stack: GdxIntArray = gdxIntArrayOf()

    fun move(worldWidth: Float) {
        position.set(
            truncate(worldWidth / 2f - HORIZONTAL_PADDING),
            truncate(WORLD_HEIGHT / -2f + VERTICAL_PADDING - 1f)
        )
    }

    fun draw(batch: SpriteBatch, sprites: SpriteSet, strategy: StackDrawingStrategy) {
        if (stack.isEmpty) {
            batch.draw(sprites.empty, position.x, position.y)
        } else {
            val last = stack.size - 1
            for (i in 0 until stack.size) {
                cardPosition.set(position.x - X_SHIFT * i, position.y)
                if (i == last) {
                    drawTop(batch, stack[i], sprites, cardPosition)
                } else {
                    strategy.drawBack(batch, stack[i], sprites, cardPosition)
                }
            }
            val card = stack[stack.size - 1]
            batch.draw(sprites.card, position.x, position.y)
            batch.draw(sprites.face[card], position.x + FACE_X, position.y + FACE_Y)
        }
    }

    private fun drawTop(batch: SpriteBatch, card: Card, sprites: SpriteSet, pos: Vector2) {
        batch.draw(sprites.card, pos.x, pos.y)
        batch.draw(sprites.face[card], pos.x, pos.y)
    }

    companion object {
        const val X_SHIFT = SMALL_FACE_WIDTH + 2f * SMALL_FACE_H_PADDING
    }
}