package my.game.screens

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import my.game.Constants
import my.game.logic.Card

interface Drawable {
    fun draw(batch: SpriteBatch)
}

interface Dynamic {
    fun update(delta: Float)
}

class CardView(private val textureAtlas: TextureAtlas) : Drawable {
    var card: Card? = null
    private val position: Vector2 = Vector2()
    private var front: Sprite? = null
    private var back: Sprite? = null

    fun set(card: Card, x: Float, y: Float): CardView {
        this.card = card
        front = textureAtlas.createSprite(card.getSpriteName())
        back = textureAtlas.createSprite(Constants.CARD_BACK)
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

class CardOutAnimation(private val textureAtlas: TextureAtlas) : Drawable, Dynamic {
    private var sprite: Sprite? = null
    private val position: Vector2 = Vector2()
    private var alpha: Float = 1f
    private var acc: Float = 0f
    private var finished = { _: CardOutAnimation -> }

    override fun update(delta: Float) {
        alpha -= 1 / (Constants.ANIMATION_TIME * 60f)
        acc += 4f * Constants.CELL_HEIGHT * (delta / Constants.ANIMATION_TIME)
        if (acc >= 1.0) {
            position.y -= 1f
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

    fun set(card: Card, x: Float, y: Float, finished: (anim: CardOutAnimation) -> Unit): CardOutAnimation {
        sprite = textureAtlas.createSprite(card.getSpriteName())
        alpha = 1f
        acc = 0f
        position.set(x, y)
        this.finished = finished
        return this
    }
}

class StackView(private val stack: java.util.Stack<Card>, textureAtlas: TextureAtlas) : Drawable {
    private val sprite = textureAtlas.createSprite(Constants.CARD_BACK)

    override fun draw(batch: SpriteBatch) {
        for (shift in 0 until stack.size) {
            batch.draw(sprite, Constants.STACK_POSITION.x + shift * 8f, Constants.STACK_POSITION.y)
        }
    }
}

class DiscardView(private val discard: java.util.Stack<Card>, private val textureAtlas: TextureAtlas) : Drawable, Dynamic {
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
                    textureAtlas.createSprite(Constants.CARD_BACK)
                } else {
                    textureAtlas.createSprite(top!!.getSpriteName())
                }
}
