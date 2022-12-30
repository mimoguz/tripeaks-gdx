package ogz.tripeaks.models

import com.badlogic.gdx.utils.Disposable
import ogz.tripeaks.graphics.Animations
import ogz.tripeaks.models.layout.BasicLayout

class Settings(
    var darkTheme: Boolean,
    var backDesign: Int,
    var layout: LayoutType,
    var animation: AnimationType,
    var showAll: Boolean,
    var emptyDiscard: Boolean,
) {
    constructor() : this(false, 0, LayoutType.BasicLo, AnimationType.BlinkAnim, false, false)

    fun clone(
        darkTheme: Boolean = this.darkTheme,
        backDesign: Int = this.backDesign,
        layout: LayoutType = this.layout,
        animation: AnimationType = this.animation,
        showAll: Boolean = this.showAll,
        emptyDiscard: Boolean = this.emptyDiscard,
    ) = Settings(
        darkTheme,
        backDesign,
        layout,
        animation,
        showAll,
        emptyDiscard,
    )
}

enum class LayoutType(val tag: String) {
    BasicLo(BasicLayout.TAG)
}

enum class AnimationType(val tag: String) {
    BlinkAnim(Animations.BLINK.name),
    DissolveAnim(Animations.DISSOLVE.name)
}
