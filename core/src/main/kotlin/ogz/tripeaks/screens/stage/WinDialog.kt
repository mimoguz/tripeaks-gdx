package ogz.tripeaks.screens.stage

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import ogz.tripeaks.Constants
import ogz.tripeaks.assets.BundleAssets
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.assets.get
import ogz.tripeaks.models.GameStatistics
import ogz.tripeaks.ui.LabelButton
import ogz.tripeaks.ui.PopDialog

class WinDialog(
    skin: UiSkin,
    assets: AssetManager,
    stats: GameStatistics,
    callback: (WinDialogResult) -> Unit
) : PopDialog(skin) {

    init {
        val bundle = assets[BundleAssets.Bundle]

        contentTable
            .defaults()
            .padBottom(skin.extraLineSpacing.coerceAtLeast(2f))
            .left()
            .expandX()

        contentTable.add(Image(uiSkin.iconWin)).center().padBottom(Constants.UI_VERTICAL_SPACING)
            .row()
        contentTable.add(Label(bundle["won"], skin, UiSkin.TITLE_LABEL_STYLE)).row()
        contentTable.add(Label(bundle.format("longestChain", stats.longestChain), skin)).row()
        contentTable.add(Label(bundle.format("usedUndo", stats.undoCount), skin))
            .padBottom(0f)

        buttonTable.defaults()
            .minWidth(100f)
            .uniformX()
            .fillX()

        buttonTable.add(LabelButton(skin, bundle.get("newGame")) {
            callback.invoke(WinDialogResult.NEW_GAME)
            hide()
        })
            .padRight(MathUtils.floor(Constants.UI_HORIZONTAL_SPACING / 2f).toFloat())

        buttonTable.add(LabelButton(skin, bundle.get("return")) {
            callback.invoke(WinDialogResult.RETURN)
            hide()
        })
            .padLeft(MathUtils.floor(Constants.UI_HORIZONTAL_SPACING / 2f).toFloat())
    }

}


enum class WinDialogResult {
    NEW_GAME, RETURN
}