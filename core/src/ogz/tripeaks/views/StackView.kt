package ogz.tripeaks.views

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ogz.tripeaks.*
import ogz.tripeaks.data.Card
import java.util.*

class StackView(
        private val stack: Stack<Card>,
        private val spriteCollection: SpriteCollection,
        private var showAllCards: Boolean = true
) : View {

    override fun draw(batch: SpriteBatch) {
        if (showAllCards) {
            drawOpen(batch)
        } else {
            drawClosed(batch)
        }
    }

    fun setShowAllCards(show: Boolean) {
        showAllCards = show
    }

    private fun drawClosed(batch: SpriteBatch) {
        for (index in 0 until stack.size - 1) {
            val x = Const.STACK_POSITION.x - index * 6f
            batch.draw(spriteCollection.plate, x, Const.STACK_POSITION.y)
            batch.draw(spriteCollection.back, x, Const.STACK_POSITION.y)
        }
    }

    private fun drawOpen(batch: SpriteBatch) {
        stack.withIndex().forEach {(index, card) ->
            val x = Const.STACK_POSITION.x - index * 6f
            batch.draw(spriteCollection.plate, x, Const.STACK_POSITION.y)
            batch.draw(
                    spriteCollection.sideFaceList[Util.getSpriteIndex(card)],
                    x + Const.SIDE_FACE_OFFSET,
                    Const.STACK_POSITION.y
            )
        }
        stack.lastOrNull()?.let { card ->
            val x =  Const.STACK_POSITION.x - (stack.size - 1) * 6f
            batch.draw(spriteCollection.plate, x, Const.STACK_POSITION.y)
            batch.draw(
                    spriteCollection.faceList[Util.getSpriteIndex(card)],
                    x + Const.FACE_X,
                    Const.STACK_POSITION.y + Const.FACE_Y
            )
        }
    }
}
