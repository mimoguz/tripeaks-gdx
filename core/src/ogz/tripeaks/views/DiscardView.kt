package ogz.tripeaks.views

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ogz.tripeaks.*
import ogz.tripeaks.data.Card

class DiscardView(private val discard: java.util.Stack<Card>, private val spriteCollection: SpriteCollection)
    : View {

    override fun draw(batch: SpriteBatch) {
        discard.lastOrNull()?.let { top ->
            batch.draw(spriteCollection.plate, Const.DISCARD_POSITION.x, Const.DISCARD_POSITION.y)
            batch.draw(
                    spriteCollection.faceList[Util.getSpriteIndex(top)],
                    Const.DISCARD_POSITION.x + Const.FACE_X,
                    Const.DISCARD_POSITION.y + Const.FACE_Y
            )
        }
    }
}
