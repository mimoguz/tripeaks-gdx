package ogz.tripeaks.services

import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.graphics.AnimationSet
import ogz.tripeaks.graphics.SpriteSet
import ogz.tripeaks.models.LayoutStatistics

sealed interface Message {
    companion object {
        class TouchDown(val screenX: Int, val screenY: Int, val pointer: Int, val button: Int) : Message
        class TouchUp(val screenX: Int, val screenY: Int, val pointer: Int, val button: Int) : Message
        class AnimationSetChanged(val animationSet: AnimationSet) : Message
        class SpriteSetChanged(val spriteSet: SpriteSet) : Message
        class ShowAllChanged(val showAll: Boolean) : Message
        class SkinChanged(val skin: UiSkin) : Message
    }
}