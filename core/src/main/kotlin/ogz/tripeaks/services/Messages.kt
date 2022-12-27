package ogz.tripeaks.services

import ogz.tripeaks.models.GameStatistics

object Messages {
    object FirstMove : Message

    class TouchDown(val screenX: Int, val screenY: Int, val pointer: Int, val button: Int) : Message

    class Win(val gameStatistics: GameStatistics) : Message
}