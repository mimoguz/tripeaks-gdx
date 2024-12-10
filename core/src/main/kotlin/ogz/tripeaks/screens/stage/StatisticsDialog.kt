package ogz.tripeaks.screens.stage

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.I18NBundle
import com.ray3k.stripe.PopTable
import ogz.tripeaks.Constants
import ogz.tripeaks.assets.BundleAssets
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.assets.get
import ogz.tripeaks.models.LayoutStatistics
import ogz.tripeaks.models.PlayerStatistics
import ogz.tripeaks.ui.LabelButton
import ogz.tripeaks.ui.Panel

class StatisticsDialog(
    skin: UiSkin,
    assets: AssetManager,
    stats: PlayerStatistics,
) : PopTable(skin) {

    init {
        val bundle = assets[BundleAssets.Bundle]
        val games = stats.layoutStatistics.fold(0) { sum, layout -> sum + layout.played }
        val wins = stats.layoutStatistics.fold(0) { sum, layout -> sum + layout.won }
        val longestChain = stats.layoutStatistics.fold(0) { acc, layout ->
            acc.coerceAtLeast(layout.longestChain)
        }

        val padRight =
            if (games == 0) Constants.UI_PANEL_HORIZONTAL_BORDER
            else Constants.UI_PANEL_HORIZONTAL_BORDER - Constants.UI_SCROLL_FIX

        pad(
            Constants.UI_PANEL_VERTICAL_BORDER - 2f,
            Constants.UI_PANEL_HORIZONTAL_BORDER,
            Constants.UI_PANEL_VERTICAL_BORDER,
            padRight,
        )

        // Title
        add(HorizontalGroup().apply {
            space(4f)
            children.add(Image(skin.iconStatistics))
            children.add(Label(bundle["statistics"], skin, UiSkin.TITLE_LABEL_STYLE))
        })
            .align(Align.left)
            .padLeft(-14f)
            .colspan(2)
            .padBottom(Constants.UI_VERTICAL_SPACING + skin.extraLineSpacing)

        row()

        val table = Table(skin).apply {
            padRight(Constants.UI_SCROLL_PUSH)
            defaults().align(Align.left).padBottom(Constants.UI_VERTICAL_SPACING).expandX().fillX()

            // General stats
            add(statPanel(bundle, skin, LayoutStatistics("statAll", games, wins, longestChain, 0)))

            row()

            // Layout stats
            val lastIndex = stats.layoutStatistics.size - 1
            stats.layoutStatistics
                .sortedBy { -it.played }
                .withIndex()
                .forEach { (index, layout) ->
                    val cell = add(statPanel(bundle, skin, layout))
                    if (index == lastIndex) cell.padBottom(0f)
                    row()
                }
        }

        val scroll = ScrollPane(table, skin).apply {
            fadeScrollBars = false
            setScrollbarsVisible(true)
            setScrollBarTouch(true)
        }

        add(scroll)
            .padBottom(Constants.UI_VERTICAL_SPACING)
            .expandX()
            .fillX()

        row()

        add(LabelButton(skin, bundle["close"]) {
            hide()
        })
            .minWidth(100f)
            .align(Align.center)
            .padRight(Constants.UI_SCROLL_FIX)

        isModal = true
        isHideOnUnfocus = false
    }

    private fun statPanel(
        bundle: I18NBundle,
        uiSkin: UiSkin,
        stats: LayoutStatistics
    ): Table = Panel(bundle[stats.tag], uiSkin, false, 2).apply {
        defaults().padBottom(uiSkin.extraLineSpacing.coerceAtLeast(2f)).left()
        val colSpacing = Constants.UI_HORIZONTAL_SPACING * 4

        add(Label(bundle["statGames"], skin)).padRight(colSpacing)
        add(Label(stats.played.toString(), skin)).right()

        row()

        add(Label(bundle["statWins"], skin)).padRight(colSpacing)
        add(Label(stats.won.toString(), skin)).right()

        row()

        add(Label(bundle["statLongestChain"], skin)).padRight(colSpacing).padBottom(0f)
        add(Label(stats.longestChain.toString(), skin)).right().padBottom(0f)
    }

}
