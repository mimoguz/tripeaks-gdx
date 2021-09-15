package ogz.tripeaks.screens.dialogs

import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.I18NBundle
import ogz.tripeaks.Const
import ogz.tripeaks.game.Statistics
import ogz.tripeaks.screens.controls.MyTextButton
import ogz.tripeaks.util.SkinData

// TODO: Finish dialog
class StatisticsDialog(
    skinData: SkinData,
    theme: String,
    statistics: Statistics,
    val res: I18NBundle,
) : Dialog("", skinData.skin, theme) {
    init {
        pad(16f, 24f, 16f, 24f)
        contentTable.apply {
            add(
                Label(
                    "${statistics.gamesPlayed} ${res.format("statGame", statistics.gamesPlayed)}",
                    skinData.skin
                )
            )
            add(
                Label(
                    "${statistics.gamesWon} ${res.format("statWin", statistics.gamesWon)}",
                    skinData.skin
                )
            )
            row()

            add(Label("", skinData.skin))
            add(Label(res.get("statGames"), skinData.skin))
            add(Label(res.get("statWins"), skinData.skin))
            add(Label(res.get("statLongestChain"), skinData.skin))
            row()

            for (layout in statistics.perLayoutStatistics.values()) {
                add(Label("${layout.tag}", skinData.skin))
                add(Label("${layout.played}", skinData.skin))
                add(Label("${layout.won}", skinData.skin))
                add(Label("${layout.longestChain}", skinData.skin))
                row()
            }
        }
        buttonTable.apply {
            pad(4f, 4f, 0f, 4f)
            defaults().width(108f).space(4f).height(Const.BUTTON_HEIGHT).pad(0f)
            add(MyTextButton(res.get("return"), skinData, theme).apply {
                setAction { hide() }
            })
        }
    }
}
