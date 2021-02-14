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
    const val SPRITE_WIDTH = 27f
    const val SPRITE_HEIGHT = 37f
    val DISCARD_POSITION = Vector2(CELL_WIDTH * 2f + 1f, CELL_HEIGHT * 2f)
    val STACK_POSITION = Vector2(CELL_WIDTH * 2f + 1f, 0f)
}