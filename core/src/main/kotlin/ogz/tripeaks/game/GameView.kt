package ogz.tripeaks.game

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.collections.GdxArray
import ktx.collections.sortBy
import ogz.tripeaks.models.GameState
import ogz.tripeaks.models.Settings
import ogz.tripeaks.models.layout.Socket

class GameView(game: GameState, private var worldWidth: Float) {
    private val cards = GdxArray<CardView>(true, 32)
    private val animations = GdxArray<AnimationView>(false, 16)
    private val finishedAnimations = GdxArray<AnimationView>(false, 16)
    private val stack = StackView()
    private val discard = DiscardView()
    private val cardPool = CardViewPool()
    private val animationPool = AnimationViewPool()

    var currentGame: GameState = game
        set(value) {
            field = value

            stack.stack = value.stack
            stack.move(worldWidth)

            discard.discard = value.discard
            discard.move(worldWidth)

            cards.clear()
            val layout = value.gameLayout
            for (socketIndex in 0 until layout.numberOfSockets) {
                val view = cardPool.obtain().apply {
                    this.put(value.socketState(socketIndex).card, layout[socketIndex], layout)
                    update(value)
                }
                cards.add(view)
            }
            cards.sortBy { it.socket?.z ?: 0 }
        }

    fun update(deltaTime: Float, settings: Settings) {
        animations.forEach { anim ->
            if (anim.update(deltaTime, settings.animationStrategy)) {
                finishedAnimations.add(anim)
            }
        }
    }

    fun draw(batch: SpriteBatch, settings: Settings) {
        discard.draw(batch, settings.spriteSet)
        stack.draw(batch, settings.spriteSet, settings.drawingStrategy)
        cards.forEach { card ->
            card.draw(batch, settings.spriteSet, settings.drawingStrategy)
        }
        animations.forEach { anim ->
            anim.draw(batch, settings.spriteSet)
        }
        animations.removeAll(finishedAnimations, true)
        finishedAnimations.clear()
    }

    private fun syncSocket(socket: Socket) {
        val view = updateViewsAround(socket)
        if (currentGame.socketState(socket.index).isEmpty) {
            animations.add(animationPool.obtain().apply {
                set(view.card, view.x, view.y)
            })
        }
    }

    fun resize(worldWidth: Float) {
        this.worldWidth = worldWidth
        stack.move(worldWidth)
        discard.move(worldWidth)
    }

    private fun updateViewsAround(socket: Socket): CardView {
        val card = currentGame.socketState(socket.index).card
        val view = cards.find { it.card == card }!!
        view.update(currentGame)
        val blocked = currentGame.gameLayout[socket.index].blocks
        for (socketIndex in blocked) {
            val blockedCard = currentGame.socketState(socketIndex).card
            cards.find { it.card == blockedCard }?.update(currentGame)
        }
        return view
    }
}