package ogz.tripeaks.views

import com.badlogic.gdx.Preferences
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.IntMap
import ktx.assets.invoke
import ktx.assets.pool
import ktx.collections.GdxArray
import ktx.collections.GdxSet
import ktx.collections.set
import ogz.tripeaks.*
import ogz.tripeaks.data.Card
import ogz.tripeaks.data.Source
import ogz.tripeaks.data.StatKeeper
import java.lang.IllegalStateException
import java.util.*
import kotlin.collections.HashSet

class GameState(
    assets: AssetManager,
    private var useDarkTheme: Boolean,
    private var showAllCards: Boolean
) : View, Dynamic {

    private val stack = GdxArray<Card>()
    private val discard = GdxArray<Card>()
    private val peaks = IntMap<Card>()
    private val spriteCollection = SpriteCollection(assets, useDarkTheme)
    val statKeeper = StatKeeper()

    private val cardsPool = pool { Card() }
    private val cardViewPool = pool { CardView(spriteCollection) }
    private val outAnimationPool = pool { FadeOut(spriteCollection) }

    private val rows: GdxArray<GdxArray<CardView>> = GdxArray.with(
        GdxArray(),
        GdxArray(),
        GdxArray(),
        GdxArray()
    )
    private val animations = GdxSet<FadeOut>()
    private val discardView by lazy { DiscardView(discard, spriteCollection) }
    private val stackView by lazy { StackView(stack, spriteCollection, showAllCards) }

    val canDeal: Boolean get() = !stack.isEmpty
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
                        val cell = Source.Cell(column.index, row.index)
                        val card = cardsPool().set(
                            info.suit,
                            info.rank,
                            cell,
                            column.value == Table.OPEN_CARD_SYMBOL
                        )
                        val view = cardViewPool().set(
                            card,
                            Util.getCellX(column.index),
                            Util.getCellY(row.index),
                            showAllCards
                        )
                        rows[row.index].add(view)
                        peaks.put(cell, card)
                    }
                }
            }
        }

        val discarded = deck.pop()
        discard.add(cardsPool().set(discarded.suit, discarded.rank, Source.Stack, true))

        for (i in 0..22) {
            val info = deck.pop()
            stack.add(cardsPool().set(info.suit, info.rank, Source.Stack, false))
        }
    }

    fun deal(): Card? {
        if (stack.isEmpty) {
            return null
        }

        val card = stack.pop()
        card.isOpen = true
        discard.add(card)
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
                stack.add(card)
                statKeeper.backToStack()
            }

            is Source.Cell -> {
                peaks.put(source, card)
                val view = cardViewPool().set(
                    card,
                    Util.getCellX(source.column),
                    Util.getCellY(source.row),
                    showAllCards
                )
                rows[source.row].add(view)

                peaks.at(source.column - 1, source.row - 1)?.let { it.isOpen = false }
                peaks.at(source.column + 1, source.row - 1)?.let { it.isOpen = false }

                statKeeper.backToPeaks()
            }
        }

        return card
    }

    fun touch(x: Float, y: Float) {
        val cellX = (x / Const.CELL_WIDTH).toInt()
        val cellY =
            ((Const.CONTENT_HEIGHT - Const.VERTICAL_PADDING - y) / Const.CELL_HEIGHT).toInt()
        for (column in (cellX - 1)..cellX) {
            for (row in (cellY - 1)..cellY) {
                val cell = Util.getIndex(column, row)
                if (peaks.at(cell)?.isOpen == true) {
                    if (peaks.at(cell)?.areConsecutive(discard.peek()) != true) {
                        return
                    }
                    val card = peaks.remove(cell)!!
                    val view = rows[row].find { it.card?.equals(card) ?: false }!!
                    rows[row].removeValue(view, false)
                    cardViewPool(view)
                    animations.add(
                        outAnimationPool().set(
                            card,
                            Util.getCellX(column),
                            Util.getCellY(row),
                            ::whenOutAnimationFinished
                        )
                    )
                    discard.add(card)

                    // Flip upper neighbors if they are clear.
                    if (isClear(column - 1, row - 1)) {
                        peaks.at(column - 1, row - 1)?.let { it.isOpen = true }
                    }
                    if (isClear(column + 1, row - 1)) {
                        peaks.at(column + 1, row - 1)?.let { it.isOpen = true }
                    }

                    statKeeper.takeFromPeaks()
                    return
                }
            }
        }
    }

    override fun update(delta: Float) {
        animations.forEach { it.update(delta) }
    }

    override fun draw(batch: SpriteBatch) {
        rows.forEach { row ->
            row.forEach { it.draw(batch) }
        }
        animations.forEach { it.draw(batch) }
        discardView.draw(batch)
        stackView.draw(batch)
    }

    fun setTheme(useDarkTheme: Boolean) {
        if (this.useDarkTheme != useDarkTheme) {
            this.useDarkTheme = useDarkTheme
            spriteCollection.set(this.useDarkTheme)
        }
    }

    fun setShowAllCards(show: Boolean) {
        if (showAllCards != show) {
            showAllCards = show
            stackView.setShowAllCards(show)
            rows.forEach { row ->
                row.forEach { it.setAlwaysShow(show) }
            }
        }
    }

    fun save(save: Preferences) {
        save.putString(Const.SAVE_STACK, collectionString(stack))
        save.putString(Const.SAVE_DISCARD, collectionString(discard))
        save.putString(
            Const.SAVE_PEAKS,
            collectionString(IntMap.Entries<Card>(peaks).map { it.value })
        )
        save.flush()
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
                peaks.put(cell, card)
                val view = cardViewPool().set(
                    card,
                    Util.getCellX(cell.column),
                    Util.getCellY(cell.row),
                    showAllCards
                )
                rows[cell.row].add(view)
            }
        }

        statKeeper.load(save)
    }

    private fun readStack(text: String, collection: GdxArray<Card>) {
        val savedStack = text.split(Const.SEPARATOR)
        for (str in savedStack) {
            collection.add(cardsPool().read(str))
        }
    }

    private fun whenOutAnimationFinished(anim: FadeOut) {
        animations.remove(anim)
        outAnimationPool(anim)
    }

    private fun isClear(column: Int, row: Int): Boolean {
        val leftDown = Util.getIndex(column - 1, row + 1)
        val rightDown = Util.getIndex(column + 1, row + 1)
        return !peaks.containsKey(leftDown) && !peaks.containsKey(rightDown)
    }

    private fun resetCollections() {
        rows.forEach { row -> row.forEach { cardViewPool(it) } }
        peaks.forEach { cardsPool(it.value) }
        stack.forEach { cardsPool(it) }
        discard.forEach { cardsPool(it) }
        animations.forEach { outAnimationPool(it) }

        rows.forEach { it.clear() }
        stack.clear()
        discard.clear()
        animations.clear()
    }

    @Suppress("NOTHING_TO_INLINE", "unused")
    companion object {
        fun collectionString(collection: Iterable<Card?>): String {
            val joiner = StringJoiner(Const.SEPARATOR)
            for (card in collection) {
                card?.let { joiner.add(it.write()) }
            }
            return joiner.toString()
        }

        inline fun IntMap<Card>.at(column: Int, row: Int): Card? =
            this[Util.getIndex(column, row), null]

        inline fun IntMap<Card>.at(cell: Source.Cell): Card? = this[Util.getIndex(cell), null]

        inline fun IntMap<Card>.at(key: Int): Card? = this[key, null]

        inline fun IntMap<Card>.put(column: Int, row: Int, card: Card) {
            this[Util.getIndex(column, row)] = card
        }

        inline fun IntMap<Card>.put(cell: Source.Cell, card: Card) {
            this[Util.getIndex(cell)] = card
        }
    }
}