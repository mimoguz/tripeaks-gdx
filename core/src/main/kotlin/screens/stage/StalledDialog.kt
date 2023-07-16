package ogz.tripeaks.screens.stage

import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.assets.AssetManager
import com.ray3k.stripe.PopTable
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.assets.BundleAssets
import ogz.tripeaks.assets.get
import ogz.tripeaks.models.GameStatistics
import ogz.tripeaks.screens.Constants
import ogz.tripeaks.screens.Constants.HORIZONTAL_PADDING
import ogz.tripeaks.screens.Constants.VERTICAL_PADDING
import ogz.tripeaks.ui.LabelButton

class StalledDialog(
    skin: UiSkin,
    assets: AssetManager,
    stats: GameStatistics,
    callback: (StalledDialogResult) -> Unit
) : PopTable(skin) {
    init {
        val bundle = assets[BundleAssets.Bundle]
        add(Label(bundle.get("stalled"), skin)).align(Align.left).expandX()
        row().spaceTop(VERTICAL_PADDING)
        add(LabelButton(skin, bundle.get("newGame")) {
            callback.invoke(StalledDialogResult.NEW_GAME)
            hide()
        }).padRight(Constants.HORIZONTAL_PADDING)
        add(LabelButton(skin, bundle.get("return")) {
            callback.invoke(StalledDialogResult.RETURN)
            hide()
        })

        isModal = true
        isHideOnUnfocus = false
    }
}

enum class StalledDialogResult {
    NEW_GAME, RETURN
}