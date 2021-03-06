package ogz.tripeaks

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2

object Const {
    const val ANIMATION_TIME = 0.25f
    const val VERTICAL_PADDING = 4f
    const val COLUMN_COUNT = 20
    const val SPRITE_CARD_BACK = "card_back"
    const val SPRITE_PLATE_LIGHT = "card_light"
    const val SPRITE_PLATE_DARK = "card_dark"
    const val SPRITE_DARK_PREFIX = "dark_"
    const val SPRITE_LIGHT_PREFIX = "light_"
    const val CELL_WIDTH = 15f
    const val CELL_HEIGHT = 20f
    const val CONTENT_WIDTH = 300f
    const val CONTENT_HEIGHT = 168f
    const val SPRITE_WIDTH = 27f
    const val SPRITE_HEIGHT = 37f
    const val PREFERENCES_VALID = "valid"
    const val PREFERENCES_SAVE = "save"
    const val PREFERENCES_GAME_PREFS = "gamePreferences"
    const val PREFERENCES_DARK_THEME = "darkTheme"
    const val PREFERENCES_STACK = "stack"
    const val PREFERENCES_DISCARD = "discard"
    const val PREFERENCES_PEAKS = "peaks"
    const val PREFERENCES_SEPARATOR = ", "
    const val FACE_X = 6f
    const val FACE_Y = 4f
    val STACK_POSITION = Vector2( CONTENT_WIDTH - CELL_WIDTH * 4f, VERTICAL_PADDING)
    val DISCARD_POSITION = Vector2(CELL_WIDTH * 2f + 1f, VERTICAL_PADDING)
    val DARK_BACKGROUND: Color = Color.valueOf("3d405bff")
    val LIGHT_BACKGROUND: Color = Color.valueOf("63a347ff")
}