package ogz.tripeaks

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2

object Const {
    const val ANIMATION_TIME = 0.25f
    const val CELL_HEIGHT = 20f
    const val CELL_WIDTH = 15f
    const val COLUMN_COUNT = 20
    const val CONTENT_HEIGHT = 168f
    const val CONTENT_WIDTH = 300f
    const val FACE_X = 6f
    const val FACE_Y = 4f
    const val PREFERENCES_DARK_THEME = "darkTheme"
    const val PREFERENCES_NAME = "gamePreferences"
    const val PREFERENCES_SHOW_ALL = "showAllCards"
    const val SAVE_CURRENT_CHAIN = "currentChain"
    const val SAVE_DISCARD = "discard"
    const val SAVE_LONGEST_CHAIN = "longestChain"
    const val SAVE_NAME = "save"
    const val SAVE_PEAKS = "peaks"
    const val SAVE_REMOVED_FROM_STACK = "removedFromStack"
    const val SAVE_STACK = "stack"
    const val SAVE_UNDO_COUNT = "undoCount"
    const val SAVE_VALID = "valid"
    const val SEPARATOR = ", "
    const val SIDE_FACE_X = 21f
    const val SPRITE_CARD_BACK = "card_back"
    const val SPRITE_CARD_BACK_SLICE = "card_back_slice"
    const val SPRITE_DARK_PREFIX = "dark_"
    const val SPRITE_HEIGHT = 37f
    const val SPRITE_LIGHT_PREFIX = "light_"
    const val SPRITE_PLATE_DARK = "card_dark"
    const val SPRITE_PLATE_LIGHT = "card_light"
    const val SPRITE_SMALL_PREFIX = "small_"
    const val SPRITE_WIDTH = 27f
    const val VERTICAL_PADDING = 4f
    val STACK_POSITION = Vector2( CONTENT_WIDTH - CELL_WIDTH * 4f + 1f, VERTICAL_PADDING)
    val DISCARD_POSITION = Vector2(CELL_WIDTH * 2f + 1f, VERTICAL_PADDING)
    val DARK_BACKGROUND: Color = Color.valueOf("232433ff")
    val LIGHT_BACKGROUND: Color = Color.valueOf("63a347ff")
}