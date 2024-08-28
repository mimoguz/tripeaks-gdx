package ogz.tripeaks.models

import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.graphics.SpriteSet
import ogz.tripeaks.models.layout.Layout
import ogz.tripeaks.views.AnimationStrategy
import ogz.tripeaks.views.CardDrawingStrategy

class Settings(
    val themeMode: ThemeMode,
    val backDesign: Int,
    val layout: Layout,
    val animationStrategy: AnimationStrategy,
    val drawingStrategy: CardDrawingStrategy,
    val spriteSet: SpriteSet,
    val skin: UiSkin,
    val emptyDiscard: Boolean,
) {

    val showAll: Boolean
        get() = drawingStrategy is CardDrawingStrategy.Strategies.BackVisible

    val darkTheme: Boolean
        get() = skin.isDark

}

enum class ThemeMode {
    System, Light, Dark, Black;

    fun <R> select(darkSystem: Boolean, light: R, dark: R, black: R): R = when (this) {
        Light -> light
        Dark -> dark
        Black -> black
        System -> if (darkSystem) dark else light
    }

    fun isDark(darkSystem: Boolean): Boolean =
        this == Dark || this == Black || (this == System && darkSystem)

    fun resource(darkSystem: Boolean): String = select(darkSystem, "light", "dark", "black")
}

