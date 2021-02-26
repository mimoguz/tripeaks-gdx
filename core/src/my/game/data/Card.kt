package my.game.data

import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import my.game.Util
import java.util.*
import kotlin.math.abs

class Card : Json.Serializable {
    private var suit: Suit = Suit.Club
    private var rank: Rank = Rank.Ace
    var isOpen: Boolean = false
    private var src: Source = Source.Stack

    val source get() = src

    fun set(suit: Suit, rank: Rank, source: Source, open: Boolean = false): Card {
        this.suit = suit
        this.rank = rank
        this.isOpen = open
        src = source
        return this
    }

    fun getSpriteName() =
            suit.toString().toLowerCase(Locale.US) +
                    (rank.ordinal + 1).toString().padStart(2, '0')

    fun areConsecutive(other: Card): Boolean {
        val distance = abs(rank.ordinal - other.rank.ordinal)
        return distance == 1 || distance == 12
    }

    override fun toString(): String = "$rank of ${suit}s"

    override fun write(json: Json) {
        json.writeValue("suit", suit.name)
        json.writeValue("rank", rank.name)
        json.writeValue("isOpen", isOpen)
        when (src) {
            is Source.Stack -> json.writeValue("source", -1)
            is Source.Cell -> json.writeValue("source", Util.getIndex(source as Source.Cell))
        }
    }

    override fun read(json: Json, jsonData: JsonValue) {
        suit = Suit.valueOf(json.readValue("suit", String::class.java, jsonData))
        rank = Rank.valueOf(json.readValue("rank", String::class.java, jsonData))
        isOpen = json.readValue("isOpen", Boolean::class.java, jsonData)
        val sourceIndex = json.readValue("source", Int::class.java, jsonData)
        src = if (sourceIndex == -1) {
            Source.Stack
        } else {
            Source.Cell(Util.getColumn(sourceIndex), Util.getRow(sourceIndex))
        }
    }

    override fun equals(other: Any?): Boolean = other !is Card || (other.rank == rank && other.suit == suit)
}

