package ogz.tripeaks.views

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import ktx.collections.GdxIntArray
import ktx.collections.gdxIntArrayOf
import ogz.tripeaks.graphics.SpriteSet
import ogz.tripeaks.Constants.FACE_X
import ogz.tripeaks.Constants.FACE_Y
import ogz.tripeaks.Constants.VIEWPORT_VERTICAL_PADDING
import ogz.tripeaks.Constants.WORLD_HEIGHT
import kotlin.math.truncate
import ogz.tripeaks.Constants.DISCARD_LEFT

class DiscardView {

    private val position = Vector2(0f, 0f)

    var discard: GdxIntArray = gdxIntArrayOf()

    fun move(worldWidth: Float) {
        position.set(
            truncate(worldWidth / -2f + DISCARD_LEFT),
            truncate(WORLD_HEIGHT / -2f + VIEWPORT_VERTICAL_PADDING - 1f)
        )
    }

    fun draw(batch: SpriteBatch, sprites: SpriteSet) {
        if (discard.isEmpty) {
            batch.draw(sprites.empty, position.x, position.y)
        } else {
            val card = discard[discard.size - 1]
            batch.draw(sprites.card, position.x, position.y)
            batch.draw(sprites.face[card], position.x + FACE_X, position.y + FACE_Y)
        }
    }

}