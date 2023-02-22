package ogz.tripeaks.ui

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import kotlin.math.truncate

class GameUi {
    private val buttons: ArrayList<GameButton> = arrayListOf()
    val size = Vector2(0f, 0f)

    fun add(button: GameButton) {
        buttons.add(button)
        button.ui = this
    }

    fun clear() {
        for (button in buttons) {
            button.ui = null
        }
        buttons.clear()
    }

    fun update(worldWidth: Float, worldHeight: Float) {
        size.set(worldWidth, worldHeight)
        for (button in buttons) {
            button.reposition()
        }
    }

    fun render(batch: SpriteBatch) {
        for (button in buttons) {
            button.render(batch)
        }
    }

    fun handlePressed(x: Float, y: Float): Boolean {
        for (button in buttons) {
            if (button.contains(x, y)) {
                if (!button.disabled) {
                    button.pressed = true
                }
                return true
            }
        }
        return false
    }

    fun handleReleased(x: Float, y: Float): Boolean {
        for (button in buttons) {
            if (button.contains(x, y)) {
                if (!button.disabled && button.pressed) button.action.invoke()
                button.pressed = false
                return true
            }
            button.pressed = false
        }
        return false
    }
}

class GameButton(
    private val skin: Skin,
    private val icon: TextureRegion,
    private val anchor: Anchor,
    val action: () -> Unit
) {
    private val bounds = Rectangle(0f, 0f, 0f, 0f)
    private val iconPosition = Vector2(0f, 0f)
    private val upDrawable = skin[ButtonStyle::class.java].up
    private val downDrawable = skin[ButtonStyle::class.java].down
    private val disabledDrawable = skin[ButtonStyle::class.java].disabled

    var ui: GameUi? = null
        set(value) {
            field = value
            reposition()
        }

    fun setSize(w: Float, h: Float) {
        bounds.setSize(w, h)
        iconPosition.set(
            truncate((w - icon.regionWidth) / 2f),
            truncate((h - icon.regionHeight) / 2f)
        )
        reposition()
    }

    var pressed: Boolean = false
    var disabled: Boolean = false

    fun reposition() {
        ui?.let { ui ->
            when (anchor) {
                is TopRight -> bounds.setPosition(
                    truncate(ui.size.x / 2f - anchor.point.x - bounds.width),
                    truncate(ui.size.y / 2f - anchor.point.y - bounds.height)
                )
                is TopLeft -> bounds.setPosition(
                    truncate(ui.size.x / -2f + anchor.point.x),
                    truncate(ui.size.y / 2f - anchor.point.y - bounds.height)
                )
                is BottomRight -> bounds.setPosition(
                    truncate(ui.size.x / 2f - anchor.point.x - bounds.width),
                    truncate(ui.size.y / -2f + anchor.point.y)
                )
                is BottomLeft -> bounds.setPosition(
                    truncate(ui.size.x / -2f + anchor.point.x),
                    truncate(ui.size.y / -2f + anchor.point.y)
                )
            }
        }
    }

    fun render(batch: SpriteBatch) {
        when {
            disabled -> {
                disabledDrawable.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height)
                batch.draw(icon, bounds.x + iconPosition.x, bounds.y + iconPosition.y)
            }
            pressed -> {
                downDrawable.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height)
                batch.draw(icon, bounds.x + iconPosition.x, bounds.y + iconPosition.y - 1f)
            }
            else -> {
                upDrawable.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height)
                batch.draw(icon, bounds.x + iconPosition.x, bounds.y + iconPosition.y)
            }
        }
    }

    fun contains(point: Vector2): Boolean = bounds.contains(point)
    fun contains(x: Float, y: Float): Boolean = bounds.contains(x, y)
}

sealed interface Anchor {
    val point: Vector2
}

data class TopRight(override val point: Vector2) : Anchor
data class TopLeft(override val point: Vector2) : Anchor
data class BottomRight(override val point: Vector2) : Anchor
data class BottomLeft(override val point: Vector2) : Anchor