package ogz.tripeaks.models

import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.game.AnimationStrategy
import ogz.tripeaks.game.CardDrawingStrategy
import ogz.tripeaks.graphics.SpriteSet
import ogz.tripeaks.models.layout.Layout

class Settings(
    val backDesign: Int,
    val layout: Layout,
    val animationStrategy: AnimationStrategy,
    val cardDrawingStrategy: CardDrawingStrategy,
    val spriteSet: SpriteSet,
    val skin: UiSkin,
    val emptyDiscard: Boolean,
) {
    val showAll: Boolean
        get() = cardDrawingStrategy is CardDrawingStrategy.Strategies.BackVisible

    val darkTheme: Boolean
        get() = skin.isDark
}
