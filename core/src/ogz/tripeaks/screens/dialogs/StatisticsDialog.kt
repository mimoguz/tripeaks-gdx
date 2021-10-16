package ogz.tripeaks.screens.dialogs

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.*
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
    private val res: I18NBundle,
    private val ui: TextureAtlas
) : Dialog("", skinData.skin, theme) {
    init {
        val titleStyle = "title_$theme"

        pad(4f, 5f, 12f, 5f)


        val table = Table(skinData.skin).apply {
            // debug()
            align(Align.left)
            defaults().align(Align.left).pad(0f).space(0f, 0f, 0f, 8f)
            width = 260f

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
                add(Image(ui.findRegion("line_$theme"))).width(this.width / 2f - 6f).colspan(3)
                    .align(Align.left).padTop(5f).padBottom(-2f)
                row()
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
            add(Image(ui.findRegion("stats_$theme"))).pad(0f).space(0f)
            row()
            add(scroll).pad(0f).padLeft(11f).height(96f).width(table.width)
        }

        buttonTable.apply {
            // More padding on the left to compensate uneven window padding:
            pad(4f, 4f, 0f, 4f)
            defaults().width(108f).space(4f).height(Const.BUTTON_HEIGHT).pad(0f)
            add(MyTextButton(res.get("return"), skinData, theme).apply {
                setAction { hide() }
            })
        }
    }
}
