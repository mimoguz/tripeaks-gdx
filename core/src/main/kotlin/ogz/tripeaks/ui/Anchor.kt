package ogz.tripeaks.ui

import com.badlogic.gdx.math.Vector2

sealed interface Anchor {

    val point: Vector2

    data class TopRight(override val point: Vector2) : Anchor
    data class TopLeft(override val point: Vector2) : Anchor
    data class BottomRight(override val point: Vector2) : Anchor
    data class BottomLeft(override val point: Vector2) : Anchor

}