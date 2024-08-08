package ogz.tripeaks.screens.stage

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.I18NBundle
import com.ray3k.stripe.PopTable
import ogz.tripeaks.assets.BundleAssets
import ogz.tripeaks.assets.TextureAtlasAssets
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.assets.get
import ogz.tripeaks.models.PlayerStatistics
import ogz.tripeaks.Constants
import ogz.tripeaks.ui.LabelButton

class StatisticsDialog(
    skin: UiSkin,
    assets: AssetManager,
    stats: PlayerStatistics,
) : PopTable(skin) {

    init {
        val bundle = assets[BundleAssets.Bundle]
        val uiAssets = assets[TextureAtlasAssets.Ui]
        val games = stats.layoutStatistics.fold(0) { sum, layout ->  sum + layout.played }
        val wins = stats.layoutStatistics.fold(0) { sum, layout ->  sum + layout.won }
        val longestChain = stats.layoutStatistics.fold(0) { acc, layout ->
            if (acc > layout.longestChain) acc else layout.longestChain
        }
        val line = uiAssets.findRegion("${skin.resourcePrefix}_line")

        pad(
            Constants.UI_PANEL_VERTICAL_BORDER,
            Constants.UI_PANEL_HORIZONTAL_BORDER,
            Constants.UI_PANEL_VERTICAL_BORDER,
            Constants.UI_PANEL_HORIZONTAL_BORDER,
        )

        // Title
        add(HorizontalGroup().apply {
            space(Constants.UI_HORIZONTAL_SPACING)
            children.add(Image(uiAssets.findRegion("${skin.resourcePrefix}_icon_stats")))
            children.add(Label(bundle["statistics"], skin, UiSkin.TITLE_LABEL_STYLE))
        })
            .align(Align.left)
            .colspan(2)
            .padBottom(Constants.UI_VERTICAL_SPACING + skin.extraLineSpacing)

        row()

        val table = Table(skin).apply {
            defaults().align(Align.left).padBottom(skin.extraLineSpacing)

            padRight(Constants.UI_HORIZONTAL_SPACING)

            // General stats
            addStat(
                this,
                bundle,
                line,
                bundle["statAll"],
                games,
                wins,
                longestChain
            )

            row()

            // Layout stats
            stats.layoutStatistics.sortedBy { it.played }.reversed().forEach { layout ->
                addStat(
                    this,
                    bundle,
                    line,
                    bundle[layout.tag],
                    layout.played,
                    layout.won,
                    layout.longestChain
                )

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
            .width(100f)
            .align(Align.center)

        debug = true
        isModal = true
        isHideOnUnfocus = false
    }

    private fun addStat(
        table: Table,
        bundle: I18NBundle,
        line: TextureAtlas.AtlasRegion,
        title: String,
        played: Int,
        won: Int,
        longestChain: Int,
    ) {
        val uiSkin = skin as UiSkin
        val divSpacing = MathUtils.floor(uiSkin.extraLineSpacing / 2f).toFloat().coerceAtLeast(1f)
        val columnSpacing = Constants.UI_HORIZONTAL_SPACING * 4

        table.add(Label(title, skin, UiSkin.TITLE_LABEL_STYLE))
            .colspan(2)
            .align(Align.center)
            .padBottom(uiSkin.extraLineSpacing)

        table.row()

        table.add(Image(line))
            .colspan(2)
            .expandX()
            .fillX()
            .pad(divSpacing, 0f, divSpacing, 0f)

        table.row()

        table.add(Label(bundle["statGames"], skin)).padRight(columnSpacing)
        table.add(Label(played.toString(), skin)).align(Align.right)

        table.row()

        table.add(Label(bundle["statWins"], skin)).padRight(columnSpacing)
        table.add(Label(won.toString(), skin)).align(Align.right)

        table.row()

        table.add(Label(bundle["statLongestChain"], skin))
            .padRight(columnSpacing)
            .padBottom(Constants.UI_VERTICAL_SPACING)
        table
            .add(Label(longestChain.toString(), skin))
            .align(Align.right)
            .padBottom(Constants.UI_VERTICAL_SPACING)
    }

}
