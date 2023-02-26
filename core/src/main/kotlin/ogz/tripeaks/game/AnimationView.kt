package ogz.tripeaks.game

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool.Poolable
import ogz.tripeaks.graphics.SpriteSet
import ogz.tripeaks.models.Card
import ogz.tripeaks.screens.Constants.CARD_HEIGHT
import ogz.tripeaks.screens.Constants.CARD_WIDTH
import ogz.tripeaks.screens.Constants.FACE_HEIGHT
import ogz.tripeaks.screens.Constants.FACE_WIDTH
import ogz.tripeaks.screens.Constants.FACE_X

class AnimationView : Poolable {
    private var card: Card = -1
    private var time = 0f
    private var position = Vector2(0f, 0f)
    private var scale = Vector2(0f, 0f)
    private var vertexColor = Color(1f, 1f, 1f, 1f)

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
        return strategy.cardRemoved(time, vertexColor, position, scale)
    }

    fun render(batch: SpriteBatch, sprites: SpriteSet) {
        batch.color.set(vertexColor.r, vertexColor.g, vertexColor.b, vertexColor.a)
        batch.draw(
            sprites.card,
            position.x,
            position.y,
            scale.x * CARD_WIDTH,
            scale.y * CARD_HEIGHT
        )
        batch.draw(
            sprites.face[card],
            position.x + FACE_X,
            position.y + FACE_X,
            scale.x * FACE_WIDTH,
            scale.y * FACE_HEIGHT
        )
        batch.color.set(1f, 1f, 1f, 1f)
    }

    override fun reset() {
        card = -1
        time = 0f
        position.set(0f, 0f)
        scale.set(1f, 1f)
        vertexColor.set(1f, 1f, 1f, 1f)
    }
}