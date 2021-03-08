package ogz.tripeaks.views

import com.badlogic.gdx.Preferences
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.IntMap
import ktx.assets.invoke
import ktx.assets.pool
import ktx.collections.GdxArray
import ktx.collections.set
import ogz.tripeaks.*
import ogz.tripeaks.data.Card
import ogz.tripeaks.data.Source
import ogz.tripeaks.data.StatKeeper
import java.util.*

class GameState(private val assets: AssetManager, private var dark: Boolean) : View, Dynamic {
    private val stack = Stack<Card>()
    private val discard = Stack<Card>()
    private val peaks = IntMap<Card>()
    val statKeeper = StatKeeper()

    private val cardsPool = pool { Card() }
    private val cardViewPool = pool { CardView(assets) }
    private val outAnimationPool = pool { FadeOut(assets, dark) }

    private val rows = GdxArray<GdxArray<CardView>>()
    private val animations = GdxArray<FadeOut>()
    private val discardView by lazy { DiscardView(discard, assets, dark) }
    private val stackView by lazy { StackView(stack, assets, dark) }

    val canDeal: Boolean get() = stack.isNotEmpty()
    val canUndo: Boolean get() = discard.size > 1
    val won: Boolean get() = peaks.isEmpty

    fun init() {
        resetCollections()
        statKeeper.set(0, 0, 0, 0)

        val deck = Util.makeDeck()
        Table.layout.withIndex().forEach { row ->
            row.value.withIndex().forEach { column ->
                when (column.value) {
                    Table.OPEN_CARD_SYMBOL, Table.CLOSED_CARD_SYMBOL -> {
                        val info = deck.pop()
                        val source = Source.Cell(column.index, row.index)
                        val card = cardsPool().set(info.suit, info.rank, source, column.value == Table.OPEN_CARD_SYMBOL)
                        val view = cardViewPool().set(card, Util.getCellX(column.index), Util.getCellY(row.index), dark)
                        rows[row.index].add(view)
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
        statKeeper.takeFromStack()

        return card
    }

    fun undo(): Card? {
        if (discard.count() <= 1) {
            return null
        }

        val card = discard.pop()

        when (val source = card.source) {
            is Source.Stack -> {
                card.isOpen = false
                stack.push(card)
                statKeeper.backToStack()
            }

            is Source.Cell -> {
                peaks[Util.getIndex(source)] = card
                val view = cardViewPool().set(card, Util.getCellX(source.column), Util.getCellY(source.row), dark)
                rows[source.row].add(view)

                val leftUp = Util.getIndex(source.column - 1, source.row - 1)
                peaks[leftUp, null]?.let { it.isOpen = false }

                val rightUp = Util.getIndex(source.column + 1, source.row - 1)
                peaks[rightUp, null]?.let { it.isOpen = false }

                statKeeper.backToPeaks()
            }
        }

        return card
    }

    fun touch(point: Vector2) {
        val cellX = (point.x / Const.CELL_WIDTH).toInt()
        val cellY = ((Const.CONTENT_HEIGHT -  Const.VERTICAL_PADDING - point.y) / Const.CELL_HEIGHT).toInt()
        for (column in (cellX - 1)..cellX) {
            for (row in (cellY - 1)..cellY) {
                val cell = Util.getIndex(column, row)
                if (peaks[cell, null]?.isOpen == true) {
                    if (peaks[cell]?.areConsecutive(discard.peek()) != true) {
                        return
                    }
                    val card = peaks.remove(cell)!!
                    val view = rows[row].find { drawable ->
                        drawable?.card?.equals(card) ?: false
                    }!!
                    rows[row].removeValue(view, true)
                    cardViewPool(view)
                    animations.add(outAnimationPool().set(card, Util.getCellX(column), Util.getCellY(row), ::whenOutAnimationFinished, dark))
                    discard.push(card)

                    // Flip upper neighbors if they are clear.
                    if (isClear(column - 1, row - 1)) {
                        peaks[Util.getIndex(column - 1, row - 1), null]?.let { it.isOpen = true }
                    }
                    if (isClear(column + 1, row - 1)) {
                        peaks[Util.getIndex(column + 1, row - 1), null]?.let { it.isOpen = true }
                    }

                    statKeeper.takeFromPeaks()
                    return
                }
            }
        }
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

    override fun setTheme(dark: Boolean) {
        this.dark = dark
        discardView.setTheme(dark)
        stackView.setTheme(dark)
        rows.forEach { row -> row.forEach { it.setTheme(dark) } }
    }

    fun save(save: Preferences) {
        save.putString(Const.SAVE_STACK, collectionString(stack))
        save.putString(Const.SAVE_DISCARD, collectionString(discard))
        save.putString(Const.SAVE_PEAKS, collectionString(peaks.values()))
        statKeeper.save(save)
    }

    fun load(save: Preferences) {
        resetCollections()

        readStack(save.getString(Const.SAVE_STACK), stack)
        readStack(save.getString(Const.SAVE_DISCARD), discard)
        val savedPeaks = save.getString(Const.SAVE_PEAKS).split(Const.SEPARATOR)
        for (str in savedPeaks) {
            val card = cardsPool().read(str)
            (card.source as? Source.Cell)?.let { cell ->
                peaks[Util.getIndex(cell)] = card
                val view = cardViewPool().set(card, Util.getCellX(cell.column), Util.getCellY(cell.row), dark)
                rows[cell.row].add(view)
            }
        }

        statKeeper.load(save)
    }

    private fun readStack(text: String, collection: Stack<Card>) {
        val savedStack = text.split(Const.SEPARATOR)
        for (str in savedStack) {
            collection.push(cardsPool().read(str))
        }
    }

    private fun whenOutAnimationFinished(anim: FadeOut) {
        animations.removeValue(anim, true)
        outAnimationPool(anim)
    }

    private fun isClear(column: Int, row: Int): Boolean {
        val leftDown = Util.getIndex(column - 1, row + 1)
        val rightDown = Util.getIndex(column + 1, row + 1)
        return !peaks.containsKey(leftDown) && !peaks.containsKey(rightDown)
    }

    private fun resetCollections() {
        rows.forEach { row -> row.forEach { it?.let { cardViewPool(it) } } }
        peaks.values().forEach { cardsPool(it) }
        stack.forEach { cardsPool(it) }
        discard.forEach { cardsPool(it) }
        animations.forEach { outAnimationPool(it) }

        rows.clear()
        stack.clear()
        discard.clear()
        animations.clear()

        for (layer in 0 until 4) {
            rows.add(GdxArray())
        }
    }

    companion object {
        fun collectionString(collection: Iterable<Card?>): String {
            val joiner = StringJoiner(Const.SEPARATOR)
            for (card in collection) {
                card?.let { joiner.add(it.write()) }
            }
            return joiner.toString()
        }
    }
}