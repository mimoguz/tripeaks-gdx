package ogz.tripeaks.screens.dialogs

import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align
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
        pad(12f, 8f, 12f, 8f)
        val table = Table(skinData.skin).apply {
            defaults().align(Align.left).pad(0f).space(0f, 0f, 0f, 12f)

            add(Label(res.get("statAll"), skinData.skin)).colspan(3)
            row()
            add(Label("${res.get("statGames")}: ${statistics.gamesPlayed}", skinData.skin))
            add(Label("${res.get("statWins")}: ${statistics.gamesWon}", skinData.skin))
            // TODO: Longest chain
            add(Label("${res.get("statLongestChain")}: TODO", skinData.skin))
            row()

            for (layout in statistics.perLayoutStatistics.values()) {
                add(Label("${res.get(layout.tag)}", skinData.skin)).colspan(3).padTop(8f)
                row()
                add(Label("${res.get("statGames")}: ${layout.played}", skinData.skin))
                add(Label("${res.get("statWins")}: ${layout.won}", skinData.skin))
                add(Label("${res.get("statLongestChain")}: ${layout.longestChain}", skinData.skin))
                row()
            }
        }
        val scroll = ScrollPane(table, skinData.skin).apply {
            setScrollbarsVisible(true)
            fadeScrollBars = false
        }
        contentTable.apply {
            pad(0f)
            add(scroll).pad(0f).height(90f).width(280f)
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
