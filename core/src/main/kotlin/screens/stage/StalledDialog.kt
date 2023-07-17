package ogz.tripeaks.screens.stage

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.ray3k.stripe.PopTable
import kotlin.math.floor
import ogz.tripeaks.assets.BundleAssets
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.assets.get
import ogz.tripeaks.models.GameStatistics
import ogz.tripeaks.screens.Constants
import ogz.tripeaks.ui.LabelButton

class StalledDialog(
    skin: UiSkin,
    assets: AssetManager,
    stats: GameStatistics,
    callback: (StalledDialogResult) -> Unit
) : PopTable(skin) {
    init {
        val bundle = assets[BundleAssets.Bundle]
        add(Label(bundle.get("stalled"), skin)).align(Align.left).expandX().colspan(2).pad(
            Constants.VERTICAL_PADDING,
            Constants.HORIZONTAL_PADDING,
            0f,
            Constants.HORIZONTAL_PADDING
        )

        row()

        add(LabelButton(skin, bundle.get("newGame")) {
            callback.invoke(StalledDialogResult.NEW_GAME)
            hide()
        })
            .pad(
                Constants.VERTICAL_PADDING,
                Constants.HORIZONTAL_PADDING,
                Constants.VERTICAL_PADDING,
                floor(Constants.HORIZONTAL_PADDING / 2)
            )
            .uniformX()
            .align(Align.center)
            .fillX()

        add(LabelButton(skin, bundle.get("return")) {
            callback.invoke(StalledDialogResult.RETURN)
            hide()
        })
            .pad(
                Constants.VERTICAL_PADDING,
                floor(Constants.HORIZONTAL_PADDING / 2),
                Constants.VERTICAL_PADDING,
                Constants.HORIZONTAL_PADDING
            )
            .uniformX()
            .align(Align.center)
            .fillX()


        debug = true
        isModal = true
        isHideOnUnfocus = false
    }
}

enum class StalledDialogResult {
    NEW_GAME, RETURN
}