package ogz.tripeaks.ui

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import kotlin.math.truncate
import ogz.tripeaks.graphics.Icon
import ogz.tripeaks.graphics.SpriteSet
import ogz.tripeaks.screens.GameUi

class TableButton(
    private val icon: Icon,
    private val anchor: Anchor,
    val action: () -> Unit
) {

    private val bounds = Rectangle(0f, 0f, 0f, 0f)
    private val iconPosition = Vector2(0f, 0f)

    var ui: GameUi? = null
        set(value) {
            field = value
            reposition()
        }

    val x get() = bounds.x
    val y get() = bounds.y
    val height get() = bounds.height
    val width get() = bounds.width

    fun setSize(w: Float, h: Float, sprites: SpriteSet) {
        bounds.setSize(w, h)
        val tex = icon.get(sprites)
        iconPosition.set(
            truncate((w - tex.regionWidth) / 2f),
            truncate((h - tex.regionHeight) / 2f) + 1
        )
        reposition()
    }

    var pressed: Boolean = false
    var enabled: Boolean = true

    fun reposition() {
        ui?.let { gui ->
            when (anchor) {
                is Anchor.TopRight -> bounds.setPosition(
                    truncate(gui.width / 2f - anchor.point.x - bounds.width),
                    truncate(gui.height / 2f - anchor.point.y - bounds.height)
                )
                is Anchor.TopLeft -> bounds.setPosition(
                    truncate(gui.width / -2f + anchor.point.x),
                    truncate(gui.height / 2f - anchor.point.y - bounds.height)
                )
                is Anchor.BottomRight -> bounds.setPosition(
                    truncate(gui.width / 2f - anchor.point.x - bounds.width),
                    truncate(gui.height / -2f + anchor.point.y)
                )
                is Anchor.BottomLeft -> bounds.setPosition(
                    truncate(gui.width / -2f + anchor.point.x),
                    truncate(gui.height / -2f + anchor.point.y)
                )
            }
        }
    }

    fun render(batch: SpriteBatch, sprites: SpriteSet) {
        val tex = icon.get(sprites)
        when {
            !enabled -> {
                sprites.buttonDisabled.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height)
                batch.draw(tex, bounds.x + iconPosition.x, bounds.y + iconPosition.y)
            }
            pressed -> {
                sprites.buttonDown.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height)
                batch.draw(tex, bounds.x + iconPosition.x, bounds.y + iconPosition.y - 1f)
            }
            else -> {
                sprites.buttonUp.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height)
                batch.draw(tex, bounds.x + iconPosition.x, bounds.y + iconPosition.y)
            }
        }
    }

    fun contains(point: Vector2): Boolean = bounds.contains(point)
    fun contains(x: Float, y: Float): Boolean = bounds.contains(x, y)

}