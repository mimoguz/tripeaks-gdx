package ogz.tripeaks.screens.stage

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.ray3k.stripe.PopTable
import ogz.tripeaks.Constants
import ogz.tripeaks.assets.BundleAssets
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.assets.get
import ogz.tripeaks.models.GameStatistics
import ogz.tripeaks.ui.LabelButton

class WinDialog(
    skin: UiSkin,
    assets: AssetManager,
    stats: GameStatistics,
    callback: (WinDialogResult) -> Unit
) : PopTable(skin) {

    init {
        val bundle = assets[BundleAssets.Bundle]

        pad(
            Constants.UI_PANEL_VERTICAL_BORDER,
            Constants.UI_PANEL_HORIZONTAL_BORDER,
            Constants.UI_PANEL_VERTICAL_BORDER,
            Constants.UI_PANEL_HORIZONTAL_BORDER,
        )

        defaults()
            .padBottom(skin.extraLineSpacing.coerceAtLeast(2f))
            .left()

        add(Image(skin.iconWin)).colspan(2).padBottom(Constants.UI_VERTICAL_SPACING).center()

        row()

        add(Label(bundle["won"], skin, UiSkin.TITLE_LABEL_STYLE)).colspan(2)

        row()

        add(Label(bundle.format("longestChain", stats.longestChain), skin)).colspan(2)

        row()

        add(Label(bundle.format("usedUndo", stats.undoCount), skin))
            .colspan(2)
            .padBottom(Constants.UI_VERTICAL_SPACING)

        row()

        add(LabelButton(skin, bundle.get("newGame")) {
            callback.invoke(WinDialogResult.NEW_GAME)
            hide()
        })
            .padRight(MathUtils.floor(Constants.UI_HORIZONTAL_SPACING / 2f).toFloat())
            .padBottom(0f)
            .uniformX()
            .center()
            .fillX()

        add(LabelButton(skin, bundle.get("return")) {
            callback.invoke(WinDialogResult.RETURN)
            hide()
        })
            .padLeft(MathUtils.floor(Constants.UI_HORIZONTAL_SPACING / 2f).toFloat())
            .padBottom(0f)
            .uniformX()
            .center()
            .fillX()

        isModal = true
        isHideOnUnfocus = false
    }

}

enum class WinDialogResult {
    NEW_GAME, RETURN
}