package ogz.tripeaks.screens.stage

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import ogz.tripeaks.Constants
import ogz.tripeaks.assets.BundleAssets
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.assets.get
import ogz.tripeaks.ui.LabelButton
import ogz.tripeaks.ui.PopDialog

class StalledDialog(
    skin: UiSkin,
    assets: AssetManager,
    callback: (StalledDialogResult) -> Unit
) : PopDialog(skin) {

    init {
        val bundle = assets[BundleAssets.Bundle]

        contentTable.defaults().expandX().left()
        contentTable.add(Image(uiSkin.iconLose))
            .center()
            .padBottom(Constants.UI_VERTICAL_SPACING)
            .row()
        contentTable.add(Label(bundle["stalled"], skin))

        buttonTable.defaults()
            .minWidth(100f)
            .uniformX()
            .fillX()
            .padBottom(Constants.UI_VERTICAL_SPACING)

        buttonTable.add(LabelButton(skin, bundle.get("restart")) {
            callback.invoke(StalledDialogResult.RESTART)
            hide()
        })
            .row()

        buttonTable.add(LabelButton(skin, bundle.get("newGame")) {
            callback.invoke(StalledDialogResult.NEW_GAME)
            hide()
        })
            .row()

        buttonTable.add(LabelButton(skin, bundle.get("return")) {
            callback.invoke(StalledDialogResult.RETURN)
            hide()
        })
            .padBottom(0f)
    }

}

enum class StalledDialogResult {
    NEW_GAME, RESTART, RETURN
}