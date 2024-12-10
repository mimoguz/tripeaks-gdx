package ogz.tripeaks.views

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.collections.GdxArray
import ktx.collections.gdxIntArrayOf
import ktx.collections.isNotEmpty
import ktx.collections.sortBy
import ogz.tripeaks.models.GameState
import ogz.tripeaks.models.Settings
import ogz.tripeaks.models.layout.Socket

class GameView(game: GameState?, private var worldWidth: Float) {

    private val cards = GdxArray<CardView>(true, 32)
    private val animations = GdxArray<AnimationView>(false, 16)
    private val finishedAnimations = GdxArray<AnimationView>(false, 16)
    private val stack = StackView()
    private val discard = DiscardView()
    private val cardPool = CardViewPool()
    private val animationPool = AnimationViewPool()

    var currentGame: GameState? = game
        set(value) {
            field = value

            if (value != null) {
                stack.stack = value.stack
                discard.discard = value.discard

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
            } else {
                cards.clear()
                stack.stack = gdxIntArrayOf()
                discard.discard = gdxIntArrayOf()
            }

            stack.move(worldWidth)
            discard.move(worldWidth)
        }

    fun update(deltaTime: Float, settings: Settings) {
        animations.forEach { anim ->
            if (anim.update(deltaTime, settings.animationStrategy)) {
                finishedAnimations.add(anim)
            }
        }
    }

    fun draw(batch: SpriteBatch, settings: Settings, worldWidth: Float, worldHeight: Float) {
        discard.draw(batch, settings.spriteSet)
        stack.draw(batch, settings.spriteSet, settings.drawingStrategy)
        cards.forEach { card ->
            card.draw(batch, settings.spriteSet, settings.drawingStrategy)
        }
        if (animations.isNotEmpty()) {
            batch.shader = settings.animationStrategy.shaderProgram
            settings.animationStrategy.resize(worldWidth, worldHeight)
            animations.forEach { anim ->
                anim.draw(batch, settings.spriteSet)
            }
            batch.shader = null
        }
        animations.removeAll(finishedAnimations, true)
        finishedAnimations.clear()
    }

    fun syncSocket(socket: Socket) {
        currentGame?.let { game ->
            val view = updateViewsAround(socket, game)
            if (game.socketState(socket.index).isEmpty) {
                animations.add(animationPool.obtain().apply { set(view.card, view.x, view.y) })
            }
        }
    }

    fun resize(worldWidth: Float) {
        this.worldWidth = worldWidth
        stack.move(worldWidth)
        discard.move(worldWidth)
    }

    private fun updateViewsAround(socket: Socket, game: GameState): CardView {
        val card = game.socketState(socket.index).card
        val view = cards.find { it.card == card }!!
        view.update(game)
        val blocked = game.gameLayout[socket.index].blocks
        for (socketIndex in blocked) {
            val blockedCard = game.socketState(socketIndex).card
            cards.find { it.card == blockedCard }?.update(game)
        }
        return view
    }

}
