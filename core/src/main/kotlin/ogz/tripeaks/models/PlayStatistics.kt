package ogz.tripeaks.models

import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import ktx.collections.GdxIntArray
import ktx.collections.gdxIntArrayOf
import ogz.tripeaks.services.Deal
import ogz.tripeaks.services.PooledMessageBox
import ogz.tripeaks.services.Receiver
import ogz.tripeaks.services.Take
import ogz.tripeaks.services.Undo

class PlayStatistics(private var tag: String) : Json.Serializable {
    constructor() : this("")

    private val dealReceiver: Receiver<Deal> = Receiver { onDeal() }
    private val takeReceiver: Receiver<Take> = Receiver<Take> { onTake() }
    private val undoReceiver: Receiver<Undo> = Receiver { onUndo(it) }

    private var chains: GdxIntArray = gdxIntArrayOf()
    private var undoCounter: Int = 0
    private var messageBox: PooledMessageBox? = null

    val longestChain
        get() = max(chains, 0)

    val undoCount
        get() = undoCounter

    val layoutTag: String
        get() = tag

    fun register(messageBox: PooledMessageBox) {
        this.messageBox = messageBox
        messageBox.register(dealReceiver)
        messageBox.register(takeReceiver)
        messageBox.register(undoReceiver)
    }

    fun unregister() {
        messageBox?.unregister(dealReceiver)
        messageBox?.unregister(takeReceiver)
        messageBox?.unregister(undoReceiver)
    }

    private fun onDeal() {
        chains.add(0)
    }

    private fun onTake() {
        if (chains.isEmpty) {
            chains.add(0)
        }
        chains[chains.size - 1] += 1
    }

    private fun onUndo(msg: Undo) {
        when {
            msg.targetIsTableau -> {
                // You can't undo a take if there wasn't any, so this must be safe:
                var currentChain = chains[chains.size - 1]
                currentChain -= 1
                if (currentChain == 0) {
                    chains.pop()
                } else {
                    chains[chains.size - 1] = currentChain
                }
                undoCounter += 1
            }
            msg.targetIsDeck -> {
                undoCounter += 1
            }
            msg.targetIsInvalid -> {
                // Ignore
            }
        }
    }

    override fun write(json: Json) {
        val serializable = SerializablePlayStatistics(tag, chains, undoCounter)
        json.writeValue(PlayStatistics::class.java.simpleName, serializable)
    }

    override fun read(json: Json, jsonData: JsonValue) {
        val serializable = json.readValue(PlayStatistics::class.java.simpleName, SerializablePlayStatistics::class.java, jsonData)
        this.chains = serializable.chains!!
        this.tag = serializable.tag!!
        this.undoCounter = serializable.undoCounter
    }

    companion object {
        private fun max(array: GdxIntArray, initialValue: Int = Int.MIN_VALUE): Int {
            var max = initialValue
            for (i in 0 until array.size) {
                max = max.coerceAtLeast(array[i])
            }
            return max
        }

        private class SerializablePlayStatistics(var tag: String?, var chains: GdxIntArray?, var undoCounter: Int) {
            constructor(): this(null, null, 0)
        }
    }
}