package ogz.tripeaks.models

import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.views.AnimationStrategy
import ogz.tripeaks.views.CardDrawingStrategy
import ogz.tripeaks.graphics.SpriteSet
import ogz.tripeaks.models.layout.Layout

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
    SYSTEM, LIGHT, DARK
}

