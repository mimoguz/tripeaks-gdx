package ogz.tripeaks.views

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import ogz.tripeaks.Const
import ogz.tripeaks.SpriteCollection
import ogz.tripeaks.Util
import ogz.tripeaks.View
import ogz.tripeaks.data.Card

@Suppress("MemberVisibilityCanBePrivate", "unused")
class CardView(private val spriteCollection: SpriteCollection) : View {
    var card: Card? = null
    private val position: Vector2 = Vector2()
    private var alwaysShow = false

    fun set(card: Card, x: Float, y: Float, alwaysShow: Boolean): CardView {
        this.card = card
        this.alwaysShow = alwaysShow
        position.set(x, y)
        return this
    }

    fun setPosition(x: Float, y: Float): CardView {
        position.set(x, y)
        return this
    }

    fun setPosition(pos: Vector2): CardView = setPosition(pos.x, pos.y)

    fun setAlwaysShow(show: Boolean) {
        alwaysShow = show
    }

    override fun draw(batch: SpriteBatch) {
        if (card == null) {
            return
        }
        batch.draw(spriteCollection.plate, position.x, position.y)
        if (card!!.isOpen || alwaysShow) {
            batch.draw(
                spriteCollection.faceList[Util.getSpriteIndex(card!!)],
                position.x + Const.FACE_X,
                position.y + Const.FACE_Y
            )
        } else {
            batch.draw(spriteCollection.back, position.x, position.y)
        }
    }

    override fun equals(other: Any?): Boolean =
        other is CardView && (this.card?.equals(other.card) ?: false)

    override fun hashCode(): Int = card?.hashCode() ?: 0
}
