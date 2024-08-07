package ogz.tripeaks.views

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool.Poolable
import kotlin.math.truncate
import ogz.tripeaks.graphics.SpriteSet
import ogz.tripeaks.models.Card
import ogz.tripeaks.models.GameState
import ogz.tripeaks.models.layout.Layout
import ogz.tripeaks.models.layout.Socket
import ogz.tripeaks.Constants.CARD_HEIGHT
import ogz.tripeaks.Constants.CELL_HEIGHT
import ogz.tripeaks.Constants.CELL_PADDING_LEFT
import ogz.tripeaks.Constants.CELL_PADDING_TOP
import ogz.tripeaks.Constants.CELL_WIDTH
import ogz.tripeaks.Constants.VIEWPORT_VERTICAL_PADDING
import ogz.tripeaks.Constants.WORLD_HEIGHT

class CardView : Poolable {

    private val position: Vector2 = Vector2(0f, 0f)
    private var hidden: Boolean = true
    private var open: Boolean = false

    var card: Card = -1
        private set

    var socket: Socket? = null
        private set

    val x: Float
        get() = position.x

    val y: Float
        get() = position.y

    fun put(card: Card, socket: Socket, layout: Layout) {
        this.card = card
        this.socket = socket
        this.open = layout[socket.index].blockedBy.isEmpty()
        this.hidden = false
        updatePosition(layout)
    }

    fun update(game: GameState) {
        socket?.let { socket ->
            hidden = game.isEmpty(socket.index)
            open = game.isOpen(socket.index)
        }
    }

    fun draw(
        batch: SpriteBatch,
        sprites: SpriteSet,
        strategy: CardDrawingStrategy
    ) {
        when {
            socket == null -> {}
            hidden -> {}
            open -> strategy.drawFront(batch, card, sprites, position)
            else -> strategy.drawBack(batch, card, sprites, position)
        }
    }

    private fun updatePosition(layout: Layout) {
        socket?.let { socket ->
            val minX = truncate(layout.numberOfColumns * CELL_WIDTH / -2f)
            position.set(
                minX + socket.column * CELL_WIDTH + CELL_PADDING_LEFT,
                MAX_Y - socket.row * CELL_HEIGHT - CELL_PADDING_TOP
            )
        }
    }

    override fun reset() {
        card = -1
        hidden = true
        open = false
        socket = null
    }

    companion object {
        val MAX_Y: Float = truncate(WORLD_HEIGHT / 2f) - VIEWPORT_VERTICAL_PADDING - CARD_HEIGHT + 1f
    }

}

