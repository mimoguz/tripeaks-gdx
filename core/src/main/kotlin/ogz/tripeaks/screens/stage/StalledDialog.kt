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
import ogz.tripeaks.ui.LabelButton

class StalledDialog(
    skin: UiSkin,
    assets: AssetManager,
    callback: (StalledDialogResult) -> Unit
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
            .padBottom(Constants.UI_VERTICAL_SPACING)
            .left()

        add(Image(skin.iconLose)).center().row()
        add(Label(bundle["stalled"], skin)).row()

        add(LabelButton(skin, bundle.get("restart")) {
            callback.invoke(StalledDialogResult.RESTART)
            hide()
        })
            .minWidth(100f)
            .fillX()
            .row()

        add(LabelButton(skin, bundle.get("newGame")) {
            callback.invoke(StalledDialogResult.NEW_GAME)
            hide()
        })
            .minWidth(100f)
            .fillX()
            .row()

        add(LabelButton(skin, bundle.get("return")) {
            callback.invoke(StalledDialogResult.RETURN)
            hide()
        })
            .minWidth(100f)
            .padBottom(0f)
            .fillX()

        isModal = true
        isHideOnUnfocus = false
    }

}

enum class StalledDialogResult {
    NEW_GAME, RESTART, RETURN
}