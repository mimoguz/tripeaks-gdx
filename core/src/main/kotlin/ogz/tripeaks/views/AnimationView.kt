package ogz.tripeaks.views

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool.Poolable
import ogz.tripeaks.Constants
import kotlin.math.truncate
import ogz.tripeaks.graphics.SpriteSet
import ogz.tripeaks.models.Card

class AnimationView : Poolable {

    private var card: Card = -1
    private var time = 0f
    private val position = Vector2(0f, 0f)
    private val scale = Vector2(1f, 1f)
    private val vertexColor = Color(1f, 1f, 1f, 1f)

    fun set(card: Card, startX: Float, startY: Float) {
        this.card = card
        position.set(startX, startY)
        time = 0f
    }

    /**
     * Update, then return true if the animation's completed.
     */
    fun update(dt: Float, strategy: AnimationStrategy): Boolean {
        time += dt
        return strategy.cardRemoved(dt, time, vertexColor, position, scale)
    }

    fun draw(batch: SpriteBatch, sprites: SpriteSet) {
        batch.color = vertexColor
        val x = truncate(position.x)
        val y = truncate(position.y)
        batch.draw(
            sprites.card,
            x,
            y,
            truncate(scale.x * Constants.CARD_WIDTH),
            truncate(scale.y * Constants.CARD_HEIGHT)
        )
        batch.draw(
            sprites.face[card],
            x + Constants.FACE_X,
            y + Constants.FACE_Y,
            truncate(scale.x * Constants.FACE_WIDTH),
            truncate(scale.y * Constants.FACE_HEIGHT)
        )
    }

    override fun reset() {
        card = -1
        time = 0f
        position.set(0f, 0f)
        scale.set(1f, 1f)
        vertexColor.set(1f, 1f, 1f, 1f)
    }

}