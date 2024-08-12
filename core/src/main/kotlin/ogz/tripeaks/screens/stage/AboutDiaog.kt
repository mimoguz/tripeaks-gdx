package ogz.tripeaks.screens.stage

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.ray3k.stripe.PopTable
import ogz.tripeaks.Constants
import ogz.tripeaks.assets.BundleAssets
import ogz.tripeaks.assets.TextureAtlasAssets
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.assets.get
import ogz.tripeaks.ui.LabelButton

class AboutDiaog(
    skin: UiSkin,
    assets: AssetManager,
) : PopTable(skin) {

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
            defaults()
                .align(Align.left)
                .padBottom(skin.extraLineSpacing)
                .padRight(Constants.UI_HORIZONTAL_SPACING)
                .expandX()
                .fillX()

            add(Label("Licenses", skin, UiSkin.TITLE_LABEL_STYLE_LATIN))
            row()
            add(Image(line)).pad(divSpacing, 0f, divSpacing, Constants.UI_HORIZONTAL_SPACING)
            row()
            addLicenseInfo(this, "tripeaks-gdx", "GPL-3.0", "github.com/mimoguz/tripeaks-gdx")
            addLicenseInfo(this, "libGDX", "Apache-2.0", "libgdx.com")
            addLicenseInfo(this, "KTX", "CC0-1.0", "libktx.github.io")
            addLicenseInfo(this, "Stripe", "MIT", "github.com/raeleus/stripe")
            row()
            addLicenseInfo(
                this,
                "GNU Unifont",
                "OFL-1.1, or GPL-2+ with\nthe GNU font embedding exception",
                "unifoundry.com/unifont"
            )

            add(Label("Privacy Policy", skin, UiSkin.TITLE_LABEL_STYLE_LATIN))
            row()
            add(Image(line)).pad(divSpacing, 0f, divSpacing, Constants.UI_HORIZONTAL_SPACING)
            row()
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
                .padLeft(Constants.UI_HORIZONTAL_SPACING * 2f)
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

    private fun addLicenseInfo(
        table: Table,
        projectName: String,
        license: String,
        link: String,
    ) {
        val indent = Constants.UI_HORIZONTAL_SPACING * 2f
        val uiSkin = skin as UiSkin

        table.apply {
            add(Label("$projectName: $license.", skin, UiSkin.LATIN_LABEL_STYLE)).padLeft(indent)
                .padBottom(2f)
            row()
            add(Label(link, skin, UiSkin.LATIN_LABEL_STYLE))
                .padBottom(Constants.UI_HORIZONTAL_SPACING + 2f)
                .colspan(2)
                .padLeft(indent)
            row()
        }
    }
}
