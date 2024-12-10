package ogz.tripeaks.models.layout

interface Layout {

    val numberOfSockets: Int
    val numberOfColumns: Int
    val numberOfRows: Int
    val tag: String
    operator fun get(index: Int): Socket
    fun lookup(column: Int, row: Int): Socket?

}

abstract class LayoutBase(private val sockets: List<Socket>) : Layout {

    private val cellMap: Map<Int, Socket> =
        sockets.associateBy { socket -> getCellIndex(socket.column, socket.row) }

    private fun getCellIndex(column: Int, row: Int) = row * numberOfColumns + column

    override operator fun get(index: Int): Socket = sockets[index]

    override fun lookup(column: Int, row: Int): Socket? {
        if (column >= numberOfColumns || row >= numberOfRows) return null
        val index = getCellIndex(column, row)
        return if (cellMap.containsKey(index)) cellMap[index] else null
    }

    override val numberOfSockets: Int get() = sockets.size

}