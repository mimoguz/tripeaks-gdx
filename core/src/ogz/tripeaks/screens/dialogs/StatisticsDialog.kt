package ogz.tripeaks.screens.dialogs

import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.I18NBundle
import ogz.tripeaks.Const
import ogz.tripeaks.game.Statistics
import ogz.tripeaks.screens.controls.MyTextButton
import ogz.tripeaks.util.SkinData

@Suppress("GDXKotlinUnsafeIterator")
class StatisticsDialog(
    skinData: SkinData,
    theme: String,
    statistics: Statistics,
    val res: I18NBundle,
) : Dialog("", skinData.skin, theme) {
    init {
        val titleStyle = "title_$theme"

        pad(12f, 24f, 12f, 11f)

        val table = Table(skinData.skin).apply {
            // debug()
            align(Align.left)
            defaults().align(Align.left).pad(0f).space(0f, 0f, 0f, 8f)
            width = 240f

            val games = statistics.perLayoutStatistics.values().sumOf { it.played }
            val wins = statistics.perLayoutStatistics.values().sumOf { it.won }
            val longestChain = statistics.perLayoutStatistics.values().fold(0) { acc, layout ->
                if (acc > layout.longestChain) acc else layout.longestChain
            }

            add(Label(res.get("statAll"), skinData.skin, titleStyle)).colspan(3)
            row()
            add(Label("${res.get("statGames")}: $games", skinData.skin, theme))
            add(Label("${res.get("statWins")}: $wins", skinData.skin, theme))
            add(
                Label(
                    "${res.get("statLongestChain")}: $longestChain",
                    skinData.skin,
                    theme
                )
            ).spaceRight(0f)
            row()

            for (layout in statistics.perLayoutStatistics.values().filter { it.played > 0 }
                .sortedByDescending { it.played }) {
                add(Label(res.get(layout.tag), skinData.skin, titleStyle)).colspan(3).padTop(8f)
                row()
                add(Label("${res.get("statGames")}: ${layout.played}", skinData.skin, theme))
                add(Label("${res.get("statWins")}: ${layout.won}", skinData.skin, theme))
                add(
                    Label(
                        "${res.get("statLongestChain")}: ${layout.longestChain}",
                        skinData.skin,
                        theme
                    )
                ).spaceRight(0f)
                row()
            }
        }

        width = table.width + padLeft + padRight

        val scroll = ScrollPane(table, skinData.skin, theme).apply {
            setScrollbarsVisible(true)
            fadeScrollBars = false
        }

        contentTable.apply {
            pad(0f)
            add(scroll).pad(0f).height(100f).width(table.width)
        }

        buttonTable.apply {
            // More padding on the left to compensate uneven window padding:
            pad(4f, 4f, 0f, 17f)
            defaults().width(108f).space(4f).height(Const.BUTTON_HEIGHT).pad(0f)
            add(MyTextButton(res.get("return"), skinData, theme).apply {
                setAction { hide() }
            })
        }
    }
}
