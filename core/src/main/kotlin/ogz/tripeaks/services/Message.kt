package ogz.tripeaks.services

import ogz.tripeaks.models.GameStatistics
import ogz.tripeaks.models.LayoutStatistics

sealed interface Message {
    companion object {
        object FirstMove : Message
        object PlayerStatisticsQuery : Message
        class PlayerStatistics(val played: Int, val won: Int, val layoutStatistics: List<LayoutStatistics>) : Message
        object SettingsQuery : Message
        class Settings(val v: Int) : Message
        class TouchDown(val screenX: Int, val screenY: Int, val pointer: Int, val button: Int) : Message
        class Win(val gameStatistics: GameStatistics) : Message
    }
}