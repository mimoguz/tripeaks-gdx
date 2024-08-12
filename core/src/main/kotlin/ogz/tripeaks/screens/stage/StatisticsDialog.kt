package ogz.tripeaks.screens.stage

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.I18NBundle
import com.ray3k.stripe.PopTable
import ogz.tripeaks.Constants
import ogz.tripeaks.assets.BundleAssets
import ogz.tripeaks.assets.TextureAtlasAssets
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.assets.get
import ogz.tripeaks.models.LayoutStatistics
import ogz.tripeaks.models.PlayerStatistics
import ogz.tripeaks.ui.LabelButton

class StatisticsDialog(
    skin: UiSkin,
    assets: AssetManager,
    stats: PlayerStatistics,
) : PopTable(skin) {

    init {
        val bundle = assets[BundleAssets.Bundle]
        val uiAssets = assets[TextureAtlasAssets.Ui]
        val games = stats.layoutStatistics.fold(0) { sum, layout -> sum + layout.played }
        val wins = stats.layoutStatistics.fold(0) { sum, layout -> sum + layout.won }
        val longestChain = stats.layoutStatistics.fold(0) { acc, layout ->
            if (acc > layout.longestChain) acc else layout.longestChain
        }
        val line = uiAssets.findRegion("${skin.resourcePrefix}_line")

        pad(
            Constants.UI_PANEL_VERTICAL_BORDER - 2f,
            Constants.UI_PANEL_HORIZONTAL_BORDER,
            Constants.UI_PANEL_VERTICAL_BORDER,
            Constants.UI_PANEL_HORIZONTAL_BORDER,
        )

        // Title
        add(HorizontalGroup().apply {
            space(4f)
            children.add(Image(uiAssets.findRegion("${skin.resourcePrefix}_icon_stats")))
            children.add(Label(bundle["statistics"], skin, UiSkin.TITLE_LABEL_STYLE))
        })
            .align(Align.left)
            .padLeft(-14f)
            .colspan(2)
            .padBottom(Constants.UI_VERTICAL_SPACING + skin.extraLineSpacing)

        row()

        val table = Table(skin).apply {
            padRight(Constants.UI_HORIZONTAL_SPACING)
            defaults().align(Align.left).padBottom(Constants.UI_VERTICAL_SPACING).expandX().fillX()

            // General stats
            add(statPanel(bundle, uiAssets, LayoutStatistics("statAll", games, wins, longestChain)))

            row()

            // Layout stats
            val lastIndex = stats.layoutStatistics.size - 1
            stats.layoutStatistics
                .sortedBy { -it.played }
                .withIndex()
                .forEach { (index, layout) ->
                    val cell = add(statPanel(bundle, uiAssets, layout))
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

        isModal = true
        isHideOnUnfocus = false
    }

    private fun statPanel(
        bundle: I18NBundle,
        uiAssets: TextureAtlas,
        stats: LayoutStatistics
    ): Table = Table(skin).apply {
        val uiSkin = skin as UiSkin
        val line = uiAssets.findRegion("${uiSkin.resourcePrefix}_line")
        val divSpacing = MathUtils.floor(uiSkin.extraLineSpacing / 2f).toFloat().coerceAtLeast(2f)
        val bg = NinePatchDrawable(uiAssets.createPatch("${uiSkin.resourcePrefix}_panel"))

        pad(
            Constants.UI_VERTICAL_SPACING,
            Constants.UI_HORIZONTAL_SPACING,
            Constants.UI_VERTICAL_SPACING,
            Constants.UI_HORIZONTAL_SPACING,
        )
        setBackground(bg)
        defaults().padBottom(uiSkin.extraLineSpacing.coerceAtLeast(2f)).left()

        add(Label(bundle[stats.tag], skin, UiSkin.TITLE_LABEL_STYLE))
            .colspan(2)
            .align(Align.left)

        row()

        add(Image(line))
            .colspan(2)
            .expandX()
            .fillX()
            .pad(
                divSpacing,
                -Constants.UI_HORIZONTAL_SPACING,
                divSpacing,
                -Constants.UI_HORIZONTAL_SPACING
            )

        row()

        add(Label(bundle["statGames"], skin))
        add(Label(stats.played.toString(), skin)).right()

        row()

        add(Label(bundle["statWins"], skin))
        add(Label(stats.won.toString(), skin)).right()

        row()

        add(Label(bundle["statLongestChain"], skin))
        add(Label(stats.longestChain.toString(), skin)).right()
    }

}
