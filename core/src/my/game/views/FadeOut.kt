package my.game.views

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import my.game.*
import my.game.data.Card

class FadeOut(private val assets: AssetManager) : View, Dynamic {
    private var sprite: Sprite? = null
    private var finished: (FadeOut) -> Unit = {}
    private val position = Vector2()
    private var alpha = 1f
    private var acc = 0f

    fun set(card: Card, x: Float, y: Float, finished: (anim: FadeOut) -> Unit): FadeOut {
        sprite = assets[TextureAtlasAssets.Cards].createSprite(card.getSpriteName())
        alpha = 1f
        acc = 0f
        position.set(x, y)
        this.finished = finished
        return this
    }

    override fun update(delta: Float) {
        alpha -= 1f / (Constants.ANIMATION_TIME * 60f)
        acc += 12f * Constants.CELL_HEIGHT * Constants.ANIMATION_TIME * delta
        if (acc >= 1.0) {
            position.set(position.x, position.y - 2f)
            acc = 0f
        }
        if (alpha <= 0f) {
            finished(this)
        }
    }

    override fun draw(batch: SpriteBatch) {
        sprite?.setPosition(position.x, position.y)
        sprite?.draw(batch, alpha)
    }
}