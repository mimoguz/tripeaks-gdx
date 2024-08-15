package ogz.tripeaks.screens.stage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.ray3k.stripe.PopTable
import ogz.tripeaks.Constants
import ogz.tripeaks.assets.BundleAssets
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.assets.get
import ogz.tripeaks.ui.LabelButton
import ogz.tripeaks.ui.Panel

class AboutDiaog(skin: UiSkin, assets: AssetManager) : PopTable(skin) {

    init {
        val bundle = assets[BundleAssets.Bundle]

        pad(
            Constants.UI_PANEL_VERTICAL_BORDER - 2f,
            Constants.UI_PANEL_HORIZONTAL_BORDER,
            Constants.UI_PANEL_VERTICAL_BORDER,
            Constants.UI_PANEL_HORIZONTAL_BORDER - Constants.UI_SCROLL_FIX,
        )

        // Title
        add(HorizontalGroup().apply {
            space(4f)
            children.add(Image(skin.iconAbout))
            children.add(Label(bundle["about"], skin, UiSkin.TITLE_LABEL_STYLE))
        })
            .align(Align.left)
            .padLeft(-14f)
            .colspan(2)
            .padBottom(Constants.UI_VERTICAL_SPACING + skin.extraLineSpacing)

        row()

        val table = Table(skin).apply {
            padRight(Constants.UI_SCROLL_PUSH)
            defaults().align(Align.left).expandX().fillX()

            add(
                Panel("Licenses", skin, true).apply {
                    defaults().padBottom(Constants.UI_VERTICAL_SPACING + 2f).left()
                    addLicenseInfo("tripeaks-gdx", "GPL-3.0", "github.com/mimoguz/tripeaks-gdx")
                    row()
                    addLicenseInfo("libGDX", "Apache-2.0", "libgdx.com")
                    row()
                    addLicenseInfo("KTX", "CC0-1.0", "libktx.github.io")
                    row()
                    addLicenseInfo("Stripe", "MIT", "github.com/raeleus/stripe")
                    row()
                    addLicenseInfo(
                        "GNU Unifont",
                        "OFL-1.1, or GPL-2+ with\nthe GNU font embedding exception",
                        "unifoundry.com/unifont"
                    )
                        .padBottom(0f)
                }
            )
                .padBottom(Constants.UI_VERTICAL_SPACING)

            row()

            add(
                Panel("Privacy Policy", skin, true).apply {
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
                    ).left()
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
            .padRight(Constants.UI_SCROLL_FIX)

        isModal = true
        isHideOnUnfocus = false
    }

    private fun Table.addLicenseInfo(
        projectName: String,
        license: String,
        link: String,
    ): Cell<LabelButton> = this.let {
        val uiSkin = skin as UiSkin
            add(Label("$projectName: $license.", skin, UiSkin.LATIN_LABEL_STYLE)).padBottom(2f)
            row()
            add((LabelButton(uiSkin, link) { Gdx.net.openURI("https://$link")}).apply {
                add(Image(uiSkin.iconLink)).padLeft(Constants.UI_HORIZONTAL_SPACING)
            })
        }

}
