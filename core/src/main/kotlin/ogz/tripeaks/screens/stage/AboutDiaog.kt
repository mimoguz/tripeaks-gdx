package ogz.tripeaks.screens.stage

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.utils.Align
import com.ray3k.stripe.PopTable
import ogz.tripeaks.Constants
import ogz.tripeaks.assets.BundleAssets
import ogz.tripeaks.assets.TextureAtlasAssets
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.assets.get
import ogz.tripeaks.ui.LabelButton

class AboutDiaog(skin: UiSkin, assets: AssetManager) : PopTable(skin) {

    init {
        val bundle = assets[BundleAssets.Bundle]
        val uiAssets = assets[TextureAtlasAssets.Ui]
        val line = uiAssets.findRegion("${skin.resourcePrefix}_line")
        val divSpacing = MathUtils.floor(skin.extraLineSpacing / 2f).toFloat().coerceAtLeast(2f)

        pad(
            Constants.UI_PANEL_VERTICAL_BORDER - 2f,
            Constants.UI_PANEL_HORIZONTAL_BORDER,
            Constants.UI_PANEL_VERTICAL_BORDER,
            Constants.UI_PANEL_HORIZONTAL_BORDER,
        )

        // Title
        add(HorizontalGroup().apply {
            space(4f)
            children.add(Image(uiAssets.findRegion("${skin.resourcePrefix}_icon_about")))
            children.add(Label(bundle["about"], skin, UiSkin.TITLE_LABEL_STYLE))
        })
            .align(Align.left)
            .padLeft(-14f)
            .colspan(2)
            .padBottom(Constants.UI_VERTICAL_SPACING + skin.extraLineSpacing)

        row()

        val table = Table(skin).apply {
            padRight(Constants.UI_HORIZONTAL_SPACING)
            defaults().align(Align.left).expandX().fillX()

            add(
                sectionPanel(uiAssets, "Licenses").apply {
                    addLicenseInfo("tripeaks-gdx", "GPL-3.0", "github.com/mimoguz/tripeaks-gdx")
                        .padBottom(Constants.UI_VERTICAL_SPACING + 2f)
                    row()
                    addLicenseInfo("libGDX", "Apache-2.0", "libgdx.com")
                        .padBottom(Constants.UI_VERTICAL_SPACING + 2f)
                    row()
                    addLicenseInfo("KTX", "CC0-1.0", "libktx.github.io")
                        .padBottom(Constants.UI_VERTICAL_SPACING + 2f)
                    row()
                    addLicenseInfo("Stripe", "MIT", "github.com/raeleus/stripe")
                        .padBottom(Constants.UI_VERTICAL_SPACING + 2f)
                    row()
                    addLicenseInfo(
                        "GNU Unifont",
                        "OFL-1.1, or GPL-2+ with\nthe GNU font embedding exception",
                        "unifoundry.com/unifont"
                    )
                }
            )
                .padBottom(Constants.UI_VERTICAL_SPACING)

            row()

            add(
                sectionPanel(uiAssets, "Privacy Policy").apply {
                    add(
                        Label(
                            "This game is open source and free\n" +
                                    "software. It is compiled unchanged\n" +
                                    "from the source codes available at\n" +
                                    "github.com/mimoguz/tripeaks-gdx.\n" +
                                    "It does not collect any user data.",
                            skin,
                            UiSkin.LATIN_LABEL_STYLE
                        )
                    )
                }
            )
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

    private fun Table.addLicenseInfo(
        projectName: String,
        license: String,
        link: String,
    ): Cell<Label> = this.let {
            add(Label("$projectName: $license.", skin, UiSkin.LATIN_LABEL_STYLE)).padBottom(2f)
            row()
            add(Label(link, skin, UiSkin.LATIN_LABEL_STYLE))
        }

    private fun sectionPanel(uiAssets: TextureAtlas, header: String): Table = Table(skin).apply {
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

        add(Label(header, skin, UiSkin.TITLE_LABEL_STYLE))
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
    }
}
