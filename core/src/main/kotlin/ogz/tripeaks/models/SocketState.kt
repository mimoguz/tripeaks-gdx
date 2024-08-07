package ogz.tripeaks.models

class SocketState(var card: Int, var isEmpty: Boolean) {

    constructor() : this(Int.MIN_VALUE, true)

}