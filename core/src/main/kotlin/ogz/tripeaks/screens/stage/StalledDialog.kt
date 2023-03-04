package ogz.tripeaks.screens.stage

import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.ray3k.stripe.PopTable
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.models.GameStatistics
import ogz.tripeaks.screens.Constants.HORIZONTAL_PADDING
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

        row().spaceTop(VERTICAL_PADDING)

        val radio1 = CheckBox("Radio 1", skin, UiSkin.RADIO_BUTTON_STYLE).apply {
            isChecked = true
            labelCell.spaceLeft(4f)
            isDisabled = true
        }
        val radio2 = CheckBox("Radio 2", skin).apply {
            labelCell.spaceLeft(4f)
        }
        val buttonGroup = ButtonGroup(radio1, radio2).apply {
            setMaxCheckCount(1)
        }
        add(radio1).align(Align.left)
        add(radio2).align(Align.left)

        row().spaceTop(VERTICAL_PADDING)

        add(LabelButton(skin, "New Game") {
            callback.invoke(StalledDialogResult.NEW_GAME)
            hide()
        }).align(Align.right).expandX()
        add(LabelButton(skin, "Return") {
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