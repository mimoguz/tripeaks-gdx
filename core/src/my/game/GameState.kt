package my.game

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.IntMap
import ktx.assets.invoke
import ktx.assets.pool
import ktx.collections.GdxArray
import ktx.collections.set
import my.game.data.Card
import my.game.data.Source
import my.game.views.CardView
import my.game.views.DiscardView
import my.game.views.FadeOut
import my.game.views.StackView

class GameState(private val assets: AssetManager) : Drawable, Dynamic {
    private val stack = java.util.Stack<Card>()
    private val discard = java.util.Stack<Card>()
    private val peaks =  IntMap<Card?>()

    private val cardsPool = pool { Card() }
    private val cardViewPool = pool { CardView(assets) }
    private val outAnimationPool = pool { FadeOut(assets) }

    private val rows = GdxArray<GdxArray<CardView>>()
    private val animations = GdxArray<FadeOut>()
    private val discardView by lazy { DiscardView(discard, assets) }
    private val stackView by lazy { StackView(stack, assets) }

    fun init() {
        rows.clear()
        stack.clear()
        discard.clear()

        for (layer in 0 until 4) {
            rows.add(GdxArray())
        }

        val deck = Util.makeDeck()
        Table.layout.withIndex().forEach { layer ->
            layer.value.withIndex().forEach { cell ->
                when (cell.value) {
                    Table.OPEN_CARD_SYMBOL, Table.CLOSED_CARD_SYMBOL -> {
                        val info = deck.pop()
                        val source = Source.Cell(cell.index, layer.index)
                        val card = cardsPool().set(info.suit, info.rank, source, cell.value == Table.OPEN_CARD_SYMBOL)
                        val view = cardViewPool().set(
                                card,
                                cell.index * Constants.CELL_WIDTH,
                                Constants.CONTENT_HEIGHT - (layer.index + 2) * Constants.CELL_HEIGHT
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
                peaks[leftUp, null]?.let { it.isOpen = false }

                val rightUp = Util.getIndex(cell.column - 1, cell.row + 1)
                peaks[rightUp, null]?.let { it.isOpen = false }
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
        val cellX = (point.x/ Constants.CELL_WIDTH).toInt()
        val cellY = ((Constants.CONTENT_HEIGHT -  point.y) / Constants.CELL_HEIGHT).toInt()
        for (column in (cellX - 1) .. cellX) {
            for (row in (cellY - 1) .. cellY) {
                val cell = Util.getIndex(column, row)
                if (peaks[cell, null]?.isOpen == true) {
                    println(peaks[cell, null])
                    if (peaks[cell]?.areConsecutive(discard.peek()) != true) {
                        return
                    }
                    val card = peaks.remove(cell)!!
                    val view = rows[row].find { drawable -> (drawable as CardView).card == card }!!
                    rows[row].removeValue(view, true)
                    cardViewPool(view)
                    animations.add(outAnimationPool().set(
                            card,
                            column * Constants.CELL_WIDTH,
                            (row + 2) * Constants.CELL_HEIGHT,
                            ::whenOutAnimationFinished
                    ))
                    discard.push(card)

                    // Flip upper neighbors if they are clear.
                    if (isClear(column - 1, row - 1)) {
                        peaks[Util.getIndex(column - 1, row - 1), null]?.let { it.isOpen = true}
                    }
                    if (isClear(column + 1, row - 1)) {
                        peaks[Util.getIndex(column + 1, row - 1), null]?.let { it.isOpen = true}
                    }

                    if (peaks.isEmpty) {
                        println("Won!")
                    }

                    return
                }
            }
        }
    }

    private fun whenOutAnimationFinished(anim: FadeOut) {
        animations.removeValue(anim, true)
        outAnimationPool(anim)
    }

    private fun isClear(column: Int, row: Int): Boolean {
        val leftDown = Util.getIndex(column - 1, row + 1)
        val rightDown = Util.getIndex(column + 1, row + 1)
        return  !peaks.containsKey(leftDown) && !peaks.containsKey(rightDown)
    }
}