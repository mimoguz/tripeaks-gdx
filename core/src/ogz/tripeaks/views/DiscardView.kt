package ogz.tripeaks.views

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ogz.tripeaks.Const
import ogz.tripeaks.Dynamic
import ogz.tripeaks.Util
import ogz.tripeaks.View
import ogz.tripeaks.data.Card

class DiscardView(private val discard: java.util.Stack<Card>, private val assets: AssetManager, private var useDarkTheme: Boolean)
    : View, Dynamic {

    private var top = if (discard.empty()) null else discard.peek()
    private var face = top?.let { Util.setFaceSprite(assets, Sprite(), it, useDarkTheme) } ?: Sprite()
    private var plate = Util.setPlateSprite(assets, Sprite(), useDarkTheme)

    override fun update(delta: Float) {
        if (top != discard.peek()) {
            top = discard.peek()
            top?.let { Util.setFaceSprite(assets, face, it, useDarkTheme) }
        }
    }

    override fun draw(batch: SpriteBatch) {
        batch.draw(plate, Const.DISCARD_POSITION.x, Const.DISCARD_POSITION.y)
        batch.draw(face, Const.DISCARD_POSITION.x + Const.FACE_X, Const.DISCARD_POSITION.y + Const.FACE_Y)
    }

    override fun setTheme(dark: Boolean) {
        this.useDarkTheme = dark
        Util.setPlateSprite(assets, plate, dark)
        top?.let { Util.setFaceSprite(assets, face, it, dark) }
    }
}
