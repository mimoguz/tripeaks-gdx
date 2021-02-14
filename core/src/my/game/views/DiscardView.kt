package my.game.views

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import my.game.*
import my.game.data.Card

class DiscardView(private val discard: java.util.Stack<Card>, private val assets: AssetManager) : View, Dynamic {
    private var top = if (discard.empty()) null else discard.peek()
    private var sprite = getSprite()

    override fun update(delta: Float) {
        if (top != discard.peek()) {
            top = discard.peek()
            sprite = getSprite()
        }
    }

    override fun draw(batch: SpriteBatch) {
        batch.draw(sprite, Constants.DISCARD_POSITION.x, Constants.DISCARD_POSITION.y)
    }

    private fun getSprite(): Sprite =
                if (top == null) {
                    assets[TextureAtlasAssets.Cards].createSprite(Constants.CARD_BACK)
                } else {
                    assets[TextureAtlasAssets.Cards].createSprite(top!!.getSpriteName())
                }
}
