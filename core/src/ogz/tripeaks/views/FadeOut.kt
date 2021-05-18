package ogz.tripeaks.views

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import ogz.tripeaks.*
import ogz.tripeaks.data.Card

class FadeOut(private val spriteCollection: SpriteCollection) : View, Dynamic {
    private var finished: (FadeOut) -> Unit = {}
    private val position = Vector2()
    private var alpha = 1f
    private var acc = 0f
    private var card: Card? = null
    private val originalPosition = Vector2()

    fun set(card: Card, x: Float, y: Float, finished: (anim: FadeOut) -> Unit): FadeOut {
        alpha = 1f
        acc = 0f
        position.set(x, y)
        this.finished = finished
        this.card = card
        return this
    }

    override fun update(delta: Float) {
        alpha -= 1f / (Const.ANIMATION_TIME * 60f)
        acc += 12f * Const.CELL_HEIGHT * Const.ANIMATION_TIME * delta
        if (acc >= 1.0) {
            position.set(position.x, position.y - 2f)
            acc = 0f
        }
        if (alpha <= 0f) {
            finished(this)
        }
    }

    override fun draw(batch: SpriteBatch) {
        card?.let { card ->
            originalPosition.set(spriteCollection.plate.x, spriteCollection.plate.y)
            spriteCollection.plate.setPosition(position.x, position.y)
            spriteCollection.plate.draw(batch, alpha)
            spriteCollection.plate.setPosition(originalPosition.x, originalPosition.y)

            val face = spriteCollection.faceList[Util.getSpriteIndex(card)]
            originalPosition.set(face.x, face.y)
            face.setPosition(position.x + Const.FACE_X, position.y + Const.FACE_Y)
            face.draw(batch, alpha)
            face.setPosition(originalPosition.x, originalPosition.y)
        }
    }

    override fun equals(other: Any?): Boolean =
        other is FadeOut && (this.card?.equals(other.card) ?: false)

    override fun hashCode(): Int = card?.hashCode() ?: 0
}
