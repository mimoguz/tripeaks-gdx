package ogz.tripeaks

import com.badlogic.gdx.math.Vector2

object Constants {
    const val ANIMATION_TIME = 0.25f
    const val VERTICAL_PADDING = 4f
    const val COLUMN_COUNT = 20
    const val SPRITE_CARD_BACK_KEY = "cardBack"
    const val CELL_WIDTH = 15f
    const val CELL_HEIGHT = 20f
    const val CONTENT_WIDTH = 300f
    const val CONTENT_HEIGHT = 168f
    const val SPRITE_WIDTH = 27f
    const val SPRITE_HEIGHT = 37f
    const val PREFERENCES_VALID_KEY = "valid"
    const val PREFERENCES_SAVE_KEY = "save"
    const val PREFERENCES_STACK_KEY = "stack"
    const val PREFERENCES_DISCARD_KEY = "discard"
    const val PREFERENCES_PEAKS_KEY = "peaks"
    const val PREFERENCES_SEPARATOR = ", "
    val STACK_POSITION = Vector2( CONTENT_WIDTH - CELL_WIDTH * 4f, VERTICAL_PADDING)
    val DISCARD_POSITION = Vector2(CELL_WIDTH * 2f + 1f, VERTICAL_PADDING)
}