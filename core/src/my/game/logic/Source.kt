package my.game.logic

sealed class Source {
    object Stack: Source()
    data class Cell(val column: Int, val row: Int): Source()
}
