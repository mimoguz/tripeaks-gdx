package ogz.tripeaks

import com.badlogic.gdx.graphics.g2d.SpriteBatch

interface View {
    fun draw(batch: SpriteBatch)
    fun setTheme(dark: Boolean)
}