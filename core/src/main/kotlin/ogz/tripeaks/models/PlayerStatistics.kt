package ogz.tripeaks.models

import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import ktx.collections.GdxArray
import ktx.collections.gdxArrayOf
import ogz.tripeaks.services.Messages
import ogz.tripeaks.services.MessageBox
import ogz.tripeaks.services.Receiver

class PlayerStatistics(
    var played: Int,
    var won: Int,
    var layoutStatistics: GdxArray<LayoutStatistics>,
) : Json.Serializable {
    constructor() : this(played = 0, won = 0, layoutStatistics = gdxArrayOf())

    private val winReceiver: Receiver<Messages.Win> = Receiver { addWin(it.gameStatistics) }
    private val firstMoveReceiver: Receiver<Messages.FirstMove> = Receiver { updatePlayed() }
    private var messageBox: MessageBox? = null

    fun register(messageBox: MessageBox) {
        this.messageBox = messageBox
        messageBox.register(winReceiver)
        messageBox.register(firstMoveReceiver)
    }

    fun unregister() {
        messageBox?.unregister(winReceiver)
        messageBox?.unregister(firstMoveReceiver)
    }

    private fun updatePlayed() {
        played += 1
    }

    private fun addWin(gameStatistics: GameStatistics) {
        won += 1
        var stats = layoutStatistics.find { it.tag == gameStatistics.layoutTag }
        if (stats == null) {
            stats = LayoutStatistics(gameStatistics.layoutTag, 0, 0, 0)
            layoutStatistics.add(stats)
        }
        stats.played += 1
        stats.won += 1
        stats.longestChain = gameStatistics.longestChain

        layoutStatistics.sort { a, b -> b.won.compareTo(a.won) }
    }

    override fun write(json: Json) {
        val serializable = SerializablePlayerStatistics(played, won, layoutStatistics)
        json.writeValue(PlayerStatistics::class.java.simpleName, serializable)
    }

    override fun read(json: Json, jsonData: JsonValue) {
        val serializable = json.readValue(
            PlayerStatistics::class.java.simpleName,
            SerializablePlayerStatistics::class.java, jsonData
        )
        this.won = serializable.won
        this.played = serializable.played
        this.layoutStatistics = serializable.layoutStatistics
    }

    companion object {

        private class SerializablePlayerStatistics(
            var played: Int,
            var won: Int,
            var layoutStatistics: GdxArray<LayoutStatistics>
        ) {
            constructor() : this(played = 0, won = 0, layoutStatistics = GdxArray.of(LayoutStatistics::class.java))
        }
    }
}