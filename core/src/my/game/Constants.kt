package my.game

import com.badlogic.gdx.math.Vector2

object Constants {
    const val ANIMATION_TIME = 0.333f
    const val COLUMN_COUNT = 20
    const val CARD_BACK = "cardBack"
    const val CELL_WIDTH = 15f
    const val CELL_HEIGHT = 20f
    const val CONTENT_WIDTH = 300f
    const val CONTENT_HEIGHT = 200f
//    const val X0 = -CONTENT_WIDTH / 2f
//    const val Y0 = CONTENT_HEIGHT / 2f
    const val X0 = -CONTENT_WIDTH / 2f
    const val Y0 = CONTENT_HEIGHT / 2f
    val DISCARD_POSITION = Vector2(X0 + CELL_WIDTH * 2f, -Y0 + CELL_HEIGHT * 2f)
    val STACK_POSITION = Vector2(X0 + CELL_WIDTH * 2f, -Y0)
}