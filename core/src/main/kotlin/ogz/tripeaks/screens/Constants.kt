package ogz.tripeaks.screens

import com.badlogic.gdx.graphics.Color

object Constants {
    const val CARD_HEIGHT = 37
    const val CARD_WIDTH = 25
    const val CELL_HEIGHT = 19
    const val CELL_PADDING_LEFT = 1
    const val CELL_PADDING_TOP = 1
    const val CELL_WIDTH = 14
    const val FACE_HEIGHT = 30
    const val FACE_WIDTH = 15
    const val HORIZONTAL_PADDING = 12
    const val MAX_WORLD_WIDTH = 360
    const val MIN_WORLD_WIDTH = 300
    const val SMALL_FACE_HEIGHT = 5
    const val SMALL_10_HEIGHT = 11
    const val SMALL_FACE_WIDTH = 3
    const val SMALL_FACE_V_PADDING = 2f
    const val SMALL_FACE_H_PADDING = 2f
    const val VERTICAL_PADDING = 4
    const val WORLD_HEIGHT = 168
    const val DISCARD_LEFT =  HORIZONTAL_PADDING + CELL_WIDTH * 2 + CELL_PADDING_LEFT
    const val STACK_RIGHT = HORIZONTAL_PADDING + CELL_WIDTH * 4 - CELL_PADDING_LEFT * 2

    val DARK_UI_TEXT = rgb(242, 204, 143)
    val DARK_UI_EMPHASIS = rgb(184, 55, 68)
    val LIGHT_UI_TEXT = rgb(76, 56, 77)
    val LIGHT_UI_EMPHASIS = rgb(224, 122, 95)
    val DARK_UI_BG: Color = Color.valueOf("232433ff")
    val LIGHT_UI_BG: Color = Color.valueOf("63a347ff")

    private fun rgb(r: Int, g: Int, b: Int): Color = Color(r / 255f, g / 255f, b / 255f, 1f)
}