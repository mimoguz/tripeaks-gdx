package ogz.tripeaks.models

import ogz.tripeaks.graphics.AnimationSet
import ogz.tripeaks.graphics.Animations
import ogz.tripeaks.models.layout.BasicLayout
import ogz.tripeaks.models.layout.DiamondsLayout
import ogz.tripeaks.models.layout.Inverted2ndLayout
import ogz.tripeaks.models.layout.Layout

class Settings(
    var darkTheme: Boolean,
    var backDesign: Int,
    var layout: LayoutType,
    var animation: AnimationType,
    var showAll: Boolean,
    var emptyDiscard: Boolean,
) {
    constructor() : this(false, 0, LayoutType.Diamonds, AnimationType.BlinkAnim, false, false)

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
    Basic(BasicLayout.TAG),
    Diamonds(DiamondsLayout.TAG),
    Inverted2nd(Inverted2ndLayout.TAG)
}

fun LayoutType.create(): Layout {
    return when (this) {
         LayoutType.Basic -> BasicLayout()
         LayoutType.Diamonds -> DiamondsLayout()
         LayoutType.Inverted2nd -> Inverted2ndLayout()
    }
}

enum class AnimationType(val tag: String) {
    BlinkAnim(Animations.BLINK.name),
    DissolveAnim(Animations.DISSOLVE.name)
}

fun AnimationType.get(): AnimationSet {
    return when (this) {
        AnimationType.BlinkAnim -> Animations.BLINK
        AnimationType.DissolveAnim -> Animations.DISSOLVE
    }
}
