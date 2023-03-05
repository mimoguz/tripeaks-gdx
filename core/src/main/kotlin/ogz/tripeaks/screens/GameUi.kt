package ogz.tripeaks.screens

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import ogz.tripeaks.graphics.Icon
import ogz.tripeaks.graphics.SpriteSet
import ogz.tripeaks.screens.Constants.CARD_HEIGHT
import ogz.tripeaks.screens.Constants.CARD_WIDTH
import ogz.tripeaks.screens.Constants.HORIZONTAL_PADDING
import ogz.tripeaks.screens.Constants.VERTICAL_PADDING
import ogz.tripeaks.ui.Anchor
import ogz.tripeaks.ui.TableButton

class GameUi(
    sprites: SpriteSet,
    dealAction: () -> Unit,
    undoAction: () -> Unit,
    menuAction: () -> Unit
) {
    val dealButton = TableButton(
        Icon.Deal,
        Anchor.BottomRight(Vector2(HORIZONTAL_PADDING, VERTICAL_PADDING)),
        dealAction
    ).apply {
        setSize(CARD_WIDTH, CARD_HEIGHT, sprites)
        ui = this@GameUi
    }

    val menuButton = TableButton(
        Icon.Menu,
        Anchor.TopRight(Vector2(HORIZONTAL_PADDING, VERTICAL_PADDING)),
        menuAction
    ).apply {
        setSize(CARD_WIDTH, CARD_WIDTH, sprites)
        ui = this@GameUi
    }

    val undoButton = TableButton(
        Icon.Menu,
        Anchor.BottomLeft(Vector2(HORIZONTAL_PADDING, VERTICAL_PADDING)),
        undoAction
    ).apply {
        setSize(CARD_WIDTH, CARD_HEIGHT, sprites)
        ui = this@GameUi
    }

    private val buttons = listOf(dealButton, undoButton, menuButton)


    val size = Vector2(0f, 0f)


    fun resize(worldWidth: Float, worldHeight: Float) {
        size.set(worldWidth, worldHeight)
        dealButton.reposition()
        menuButton.reposition()
        undoButton.reposition()
    }

    fun draw(batch: SpriteBatch, sprites: SpriteSet) {
        dealButton.render(batch, sprites)
        menuButton.render(batch, sprites)
        undoButton.render(batch, sprites)
    }

    fun handlePressed(x: Float, y: Float): Boolean {
        for (button in buttons) {
            if (button.contains(x, y)) {
                if (button.enabled) {
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
                if (button.enabled && button.pressed) button.action.invoke()
                button.pressed = false
                return true
            }
            button.pressed = false
        }
        return false
    }
}

