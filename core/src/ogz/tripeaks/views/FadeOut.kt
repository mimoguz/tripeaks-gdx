package ogz.tripeaks.views

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import ogz.tripeaks.*
import ogz.tripeaks.data.Card

class FadeOut(private val assets: AssetManager, useDarkTheme: Boolean) : View, Dynamic {
    private var plate = Util.setPlateSprite(assets, Sprite(), useDarkTheme)
    private var face = Sprite()
    private var finished: (FadeOut) -> Unit = {}
    private val position = Vector2()
    private var alpha = 1f
    private var acc = 0f
    private var card: Card? = null

    fun set(card: Card, x: Float, y: Float, finished: (anim: FadeOut) -> Unit, dark: Boolean): FadeOut {
        alpha = 1f
        acc = 0f
        position.set(x, y)
        this.finished = finished
        this.card = card
        setTheme(dark)
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
        plate.setPosition(position.x, position.y)
        plate.draw(batch, alpha)
        face.setPosition(position.x + Const.FACE_X, position.y + Const.FACE_Y)
        face.draw(batch, alpha)
    }

    override fun setTheme(dark: Boolean) {
        Util.setPlateSprite(assets, plate,  dark)
        card?.let { Util.setFaceSprite(assets, face, it, dark) }
    }
}