package ogz.tripeaks.data

sealed class Source {
    object Stack: Source()
    data class Cell(val column: Int, val row: Int): Source()
}
