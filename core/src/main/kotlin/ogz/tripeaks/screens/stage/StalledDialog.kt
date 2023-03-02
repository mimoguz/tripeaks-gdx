package ogz.tripeaks.screens.stage

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.ray3k.stripe.PopTable
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.models.GameStatistics
import ogz.tripeaks.screens.Constants.VERTICAL_PADDING
import ogz.tripeaks.ui.LabelButton

class StalledDialog(
    skin: UiSkin,
    stats: GameStatistics,
    callback: (StalledDialogResult) -> Unit
) : PopTable(skin) {
    init {
        add(Label("LongestChain", skin)).align(Align.left).expandX()
        add(Label(stats.longestChain.toString(), skin)).align(Align.right)

        row().pad(VERTICAL_PADDING, 0f, 0f, 0f)

        add(LabelButton(skin, "New Game") {
            callback.invoke(StalledDialogResult.NEW_GAME)
            hide()
        }).align(Align.right).expandX()

        add(LabelButton(skin, "Exit") {
            callback.invoke(StalledDialogResult.RETURN)
            hide()
        }).align(Align.right)

        isModal = true
        isHideOnUnfocus = false
    }
}

enum class StalledDialogResult {
    NEW_GAME, RETURN
}