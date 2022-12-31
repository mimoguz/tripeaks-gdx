package ogz.tripeaks.screens

import com.badlogic.gdx.graphics.Color

object Constants {
    const val MIN_WORLD_WIDTH = 300
    const val MAX_WORLD_WIDTH = 360
    const val WORLD_HEIGHT = 168
    val DARK_UI_TEXT = rgb(242, 204, 143)
    val DARK_UI_EMPHASIS = rgb(184, 55, 68)
    val LIGHT_UI_TEXT = rgb(76, 56, 77)
    val LIGHT_UI_EMPHASIS = rgb(224, 122, 95)
    val DARK_UI_BG: Color = Color.valueOf("232433ff")
    val LIGHT_UI_BG: Color = Color.valueOf("63a347ff")

    private fun rgb(r: Int, g: Int, b: Int): Color = Color(r / 255f, g / 255f, b / 255f, 1f)
}