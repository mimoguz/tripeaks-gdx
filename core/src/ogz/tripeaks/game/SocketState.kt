package ogz.tripeaks.game

data class SocketState(val card: Int, var isEmpty: Boolean) {
    fun serialize() = "$card ${if (isEmpty) 1 else 0}"

    companion object {
        fun deserialize(text: String): SocketState {
            val parts = text.split(' ')
            require(parts.size == 2)
            val card = Integer.parseInt(parts[0])
            require(card in 0..52)
            val isEmpty = when (parts[1]) {
                "0" -> false
                "1" -> true
                else -> throw IllegalArgumentException("Invalid string")
            }
            return SocketState(card, isEmpty)
        }
    }
}
