package ogz.tripeaks

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2

object Const {
    const val CELL_HEIGHT = 20f
    const val CELL_WIDTH = 15f
    const val CONTENT_HEIGHT = 168f
    const val CONTENT_WIDTH = 300f
    const val FACE_X = 6f
    const val FACE_Y = 4f
    const val SPRITE_CARD_BACK = "card_back"
    const val SPRITE_DARK_PREFIX = "dark"
    const val SPRITE_HEIGHT = 37f
    const val SPRITE_LIGHT_PREFIX = "light"
    const val SPRITE_PLATE_DARK = "card_dark"
    const val SPRITE_PLATE_LIGHT = "card_light"
    const val SPRITE_SMALL_PREFIX = "small"
    const val SPRITE_WIDTH = 27f
    const val SPRITE_X = 1f
    const val SPRITE_Y = 1f
    const val VERTICAL_PADDING = 4f
    const val BUTTON_HEIGHT = 25f
    val HORIZONTAL_PADDING = (CONTENT_WIDTH - CELL_WIDTH * 20 ) * 0.5f
    val STACK_POSITION = Vector2(CONTENT_WIDTH - HORIZONTAL_PADDING - 4 * CELL_WIDTH + SPRITE_X , VERTICAL_PADDING)
    val DISCARD_POSITION = Vector2(CELL_WIDTH * 2f + HORIZONTAL_PADDING + SPRITE_X, VERTICAL_PADDING)
    val DARK_BACKGROUND: Color = Color.valueOf("232433ff")
    val LIGHT_BACKGROUND: Color = Color.valueOf("63a347ff")
}