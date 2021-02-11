package my.game.logic

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.IntMap
import ktx.assets.invoke
import ktx.assets.pool
import ktx.collections.GdxArray
import ktx.collections.set
import my.game.Constants
import my.game.TextureAtlasAssets
import my.game.get
import my.game.screens.*

class GameState(private val assets: AssetManager) : Drawable, Dynamic {
    private val stack = java.util.Stack<Card>()
    private val discard = java.util.Stack<Card>()
    private val peaks =  IntMap<Card?>()

    private val cardsPool = pool { Card() }
    private val cardViewPool = pool { CardView(assets[TextureAtlasAssets.Cards]) }
    private val outAnimationPool = pool { CardOutAnimation(assets[TextureAtlasAssets.Cards]) }

    private val rows = GdxArray<GdxArray<CardView>>()
    private val animations = GdxArray<CardOutAnimation>()
    private val discardView by lazy { DiscardView(discard, assets[TextureAtlasAssets.Cards]) }
    private val stackView by lazy { StackView(stack, assets[TextureAtlasAssets.Cards]) }

    fun init() {
        rows.clear()
        stack.clear()
        discard.clear()

        for (layer in 0 until 4) {
            rows.add(GdxArray())
        }

        val deck = Util.makeDeck()
        Table.layout.withIndex().forEach { layer ->
            println(layer.index)
            layer.value.withIndex().forEach { cell ->
                when (cell.value) {
                    Table.OPEN_CARD_SYMBOL, Table.CLOSED_CARD_SYMBOL -> {
                        val info = deck.pop()
                        val source = Source.Cell(cell.index, layer.index)
                        val card = cardsPool().set(info.suit, info.rank, source, cell.value == Table.OPEN_CARD_SYMBOL)
                        val view = cardViewPool().set(
                                card,
                                Constants.X0 + cell.index * Constants.CELL_WIDTH,
                                Constants.Y0 - (layer.index + 2) * Constants.CELL_HEIGHT
                        )
                        rows[layer.index].add(view)
                        peaks[Util.getIndex(source.column, source.row)] = card
                    }
                }
            }
        }

        val discarded = deck.pop()
        discard.push(cardsPool().set(discarded.suit, discarded.rank, Source.Stack, true))

        for (i in 0..22) {
            val info = deck.pop()
            stack.push(cardsPool().set(info.suit, info.rank, Source.Stack, false))
        }
    }

    fun deal(): Card? {
        if (stack.isEmpty()) {
            return null
        }

        val card = stack.pop()
        card.isOpen = true
        discard.push(card)

        return card
    }

    fun undo(): Card? {
        if (discard.count() <= 1) {
            return null
        }

        val card = discard.pop()

        when (card.source) {
            is Source.Stack -> {
                card.isOpen = false
                stack.push(card)
            }

            is Source.Cell -> {
                val cell = card.source as Source.Cell
                peaks[Util.getIndex(cell)] = card

                val leftUp = Util.getIndex(cell.column - 1, cell.row - 1)
                if (peaks.containsKey(leftUp) && peaks[leftUp] != null) {
                    peaks[leftUp]?.apply {
                        isOpen = false
                    }
                }

                val rightUp = Util.getIndex(cell.column - 1, cell.row + 1)
                if (peaks.containsKey(rightUp) && peaks[rightUp] != null) {
                    peaks[rightUp]?.apply {
                        isOpen = false
                    }
                }
            }
        }

        return card
    }

    override fun update(delta: Float) {
        for (anim in animations) {
            anim.update(delta)
        }
        discardView.update(delta)
    }

    override fun draw(batch: SpriteBatch) {
        for (row in rows) {
            for (cardView in row) {
                cardView.draw(batch)
            }
        }
        for (anim in animations) {
            anim.draw(batch)
        }
        discardView.draw(batch)
        stackView.draw(batch)
    }

    fun touch(point: Vector2) {
        val cellX = ((point.x - Constants.X0) / Constants.CELL_WIDTH).toInt()
        val cellY = ((Constants.Y0 - point.y) / Constants.CELL_HEIGHT).toInt()
        for (column in (cellX - 1) .. cellX) {
            for (row in (cellY - 1) .. cellY) {
                val cell = Util.getIndex(column, row)
                if (peaks.containsKey(cell) && peaks[cell]!!.isOpen) {
                    val card = peaks.remove(cell)!!
                    val view = rows[row].find { drawable -> (drawable as CardView).card == card }!!
                    rows[row].removeValue(view, true)
                    cardViewPool(view)
                    animations.add(outAnimationPool().set(
                            card,
                            Constants.X0 + column * Constants.CELL_WIDTH,
                            Constants.Y0 - (row + 2) * Constants.CELL_HEIGHT,
                            ::whenOutAnimationFinished
                    ))
                    discard.add(card)
                    return
                }
            }
        }
    }

    private fun whenOutAnimationFinished(anim: CardOutAnimation) {
        animations.removeValue(anim, true)
        outAnimationPool(anim)
    }
}