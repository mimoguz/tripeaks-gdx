package ogz.tripeaks.views

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import ogz.tripeaks.Constants
import ogz.tripeaks.View
import ogz.tripeaks.TextureAtlasAssets
import ogz.tripeaks.data.Card
import ogz.tripeaks.get

class CardView(private val assets: AssetManager) : View {
    var card: Card? = null
    private val position: Vector2 = Vector2()
    private var front: Sprite? = null
    private var back: Sprite? = null

    fun set(card: Card, x: Float, y: Float): CardView {
        this.card = card
        front = assets[TextureAtlasAssets.Cards].createSprite(card.getSpriteName())
        back = assets[TextureAtlasAssets.Cards].createSprite(Constants.SPRITE_CARD_BACK_KEY)
        this.position.set(x, y)
        return this
    }

    fun setPosition(x: Float, y: Float): CardView {
        position.set(x, y)
        return this
    }

    inline fun setPosition(pos: Vector2): CardView = setPosition(pos.x, pos.y)

    override fun draw(batch: SpriteBatch) {
        val sprite = if (card?.isOpen == true) front else back
        sprite?.setPosition(position.x, position.y)
        sprite?.draw(batch)
    }
}