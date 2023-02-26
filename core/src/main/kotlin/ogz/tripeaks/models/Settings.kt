package ogz.tripeaks.models

import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.game.AnimationStrategy
import ogz.tripeaks.game.CardDrawingStrategy
import ogz.tripeaks.game.StackDrawingStrategy
import ogz.tripeaks.graphics.SpriteSet
import ogz.tripeaks.models.layout.BasicLayout
import ogz.tripeaks.models.layout.DiamondsLayout
import ogz.tripeaks.models.layout.Inverted2ndLayout
import ogz.tripeaks.models.layout.Layout

class Settings(
    val backDesign: Int,
    val layout: Layout,
    val animationStrategy: AnimationStrategy,
    val cardDrawingStrategy: CardDrawingStrategy,
    val stackDrawingStrategy: StackDrawingStrategy,
    val spriteSet: SpriteSet,
    val skin: UiSkin,
    val emptyDiscard: Boolean,
) {
    val showAll: Boolean
        get() = cardDrawingStrategy is CardDrawingStrategy.Strategies.BackVisible

    val darkTheme: Boolean
        get() = skin.isDark
}
