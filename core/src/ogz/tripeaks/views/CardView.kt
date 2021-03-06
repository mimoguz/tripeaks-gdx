package ogz.tripeaks.views

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import ogz.tripeaks.*
import ogz.tripeaks.data.Card

class CardView(private val assets: AssetManager) : View {
    var card: Card? = null
    private val position: Vector2 = Vector2()
    private var face: Sprite = Sprite()
    private var back: Sprite = Sprite()
    private var plate: Sprite = Sprite()

    fun set(card: Card, x: Float, y: Float, dark: Boolean): CardView {
        this.card = card
        Util.setRegion(assets, back, Const.SPRITE_CARD_BACK)
        setTheme(dark)
        this.position.set(x, y)
        return this
    }

    fun setPosition(x: Float, y: Float): CardView {
        position.set(x, y)
        return this
    }

    fun setPosition(pos: Vector2): CardView = setPosition(pos.x, pos.y)

    override fun draw(batch: SpriteBatch) {
        batch.draw(plate, position.x, position.y)
        if (card?.isOpen == true) {
            batch.draw(face, position.x + Const.FACE_X, position.y + Const.FACE_Y)
        } else {
            batch.draw(back, position.x, position.y)
        }
    }

    override fun setTheme(dark: Boolean) {
        Util.setPlateSprite(assets, plate, dark)
        if (card != null) {
            Util.setFaceSprite(assets, face, card!!, dark)
        }
    }
}