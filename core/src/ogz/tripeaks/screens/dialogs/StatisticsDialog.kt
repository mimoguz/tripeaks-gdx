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

        pad(4f, 16f, 12f, 16f)

        val table = Table(skinData.skin).apply {
            // debug()
            val vspace = if (res.get("skinKey") == "cjk") 8f else 0f
            defaults().align(Align.left).pad(0f).space(vspace, 0f, vspace, 12f)
            width = 220f
            align(Align.center)
            padLeft(16f)

            val games = statistics.perLayoutStatistics.values().sumOf { it.played }
            val wins = statistics.perLayoutStatistics.values().sumOf { it.won }
            val longestChain = statistics.perLayoutStatistics.values().fold(0) { acc, layout ->
                if (acc > layout.longestChain) acc else layout.longestChain
            }

            add(Label(res.get("statAll"), skinData.skin, titleStyle)).colspan(2).align(Align.center)
            row()
            add(Label(res.get("statGames"), skinData.skin, theme))
            add(Label(games.toString(), skinData.skin, theme)).align(Align.right)
            row()
            add(Label(res.get("statWins"), skinData.skin, theme))
            add(Label(wins.toString(), skinData.skin, theme)).align(Align.right)
            row()
            add(Label(res.get("statLongestChain"), skinData.skin, theme))
            add(Label(longestChain.toString(), skinData.skin, theme)).align(Align.right)

            for (layout in statistics.perLayoutStatistics.values().filter { it.played > 0 }
                .sortedByDescending { it.played }) {
                row()
                //TODO: Find a better way to handle the bottom padding
                add(Image(ui.findRegion("line_$theme"))).width(32f)
                    .colspan(2)
                    .align(Align.center)
                    .pad(6f, 0f, 6f, 0f)
                row()
                add(Label(res.get(layout.tag), skinData.skin, titleStyle)).colspan(2)
                    .align(Align.center)
                row()
                add(Label(res.get("statGames"), skinData.skin, theme))
                add(Label(layout.played.toString(), skinData.skin, theme)).align(Align.right)
                row()
                add(Label(res.get("statWins"), skinData.skin, theme))
                add(Label(layout.won.toString(), skinData.skin, theme)).align(Align.right)
                row()
                add(Label(res.get("statLongestChain"), skinData.skin, theme))
                add(Label(layout.longestChain.toString(), skinData.skin, theme)).align(Align.right)
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
            add(scroll).pad(0f).height(96f).width(table.width)
        }

        buttonTable.apply {
            // More padding on the left to compensate uneven window padding:
            pad(4f, 4f, 0f, 4f)
            defaults().width(158f).space(4f).height(Const.BUTTON_HEIGHT).pad(0f)
            add(MyTextButton(res.get("return"), skinData, theme).apply {
                setAction { hide() }
            })
        }
    }
}
