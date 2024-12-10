package ogz.tripeaks

import com.badlogic.gdx.graphics.Color

object Constants {

    const val CARD_HEIGHT = 37f
    const val CARD_WIDTH = 25f
    const val CELL_HEIGHT = 19f
    const val CELL_PADDING_LEFT = 1f
    const val CELL_PADDING_TOP = 1f
    const val CELL_WIDTH = 14f
    const val FACE_HEIGHT = 28f
    const val FACE_WIDTH = 15f
    const val FACE_X = (CARD_WIDTH - FACE_WIDTH) / 2
    const val FACE_Y = (CARD_HEIGHT - FACE_HEIGHT - 1) / 2 + 1
    const val HORIZONTAL_PADDING = 12f
    const val MAX_WORLD_WIDTH = 360f
    const val MIN_WORLD_WIDTH = 304f
    const val SMALL_FACE_HEIGHT = 10f
    const val SMALL_10_HEIGHT = 18f
    const val SMALL_FACE_WIDTH = 6f
    const val VIEWPORT_VERTICAL_PADDING = 4f
    const val WORLD_HEIGHT = 168f
    const val DISCARD_LEFT =  HORIZONTAL_PADDING + CELL_WIDTH * 2f + CELL_PADDING_LEFT
    const val STACK_RIGHT = HORIZONTAL_PADDING + CELL_WIDTH * 4f - CELL_PADDING_LEFT * 2f
    const val DISSOLVE_TIME = 0.5f
    const val BLUR_TIME = 0.667f

    const val TEXT_BUTTON_VERTICAL_PADDING = 4f
    const val TEXT_BUTTON_HORIZONTAL_PADDING = 8f
    const val UI_VERTICAL_SPACING = 6f
    const val UI_HORIZONTAL_SPACING = 6f
    const val UI_PANEL_VERTICAL_BORDER = 9f
    const val UI_PANEL_HORIZONTAL_BORDER = 22f
    const val UI_CJK_LINE_SPACING = 4f
    const val UI_SCROLL_FIX = 14f
    const val UI_SCROLL_PUSH = 4f

    val DARK_UI_TEXT = rgb(249, 219, 169)
    val LIGHT_UI_TEXT = rgb(76, 56, 77)
    val BLACK_UI_TEXT = Color.valueOf("98c379ff")
    val DARK_UI_EMPHASIS = rgb(224, 122, 95)
    val LIGHT_UI_EMPHASIS = rgb(184, 55, 68)
    val BLACK_UI_EMPHASIS = Color.valueOf("e06c75ff")
    val DARK_BG: Color = Color.valueOf("232433ff")
    val LIGHT_BG: Color = Color.valueOf("63a347ff")
    val BLACK_BG: Color = Color.BLACK

    private fun rgb(r: Int, g: Int, b: Int): Color = Color(r / 255f, g / 255f, b / 255f, 1f)

}