package ogz.tripeaks.screens.dialogs

import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.I18NBundle
import ogz.tripeaks.Const
import ogz.tripeaks.screens.controls.MyTextButton
import ogz.tripeaks.util.SkinData

class StalledDialog(
    skinData: SkinData,
    theme: String,
    val res: I18NBundle,
) : Dialog("", skinData.skin, theme) {

    val newGameButton = MyTextButton(res.get("newGame"), skinData, theme)
    val exitButton = MyTextButton(res.get("exit"), skinData, theme, true)
    val undoButton = MyTextButton(res.get("undoLast"), skinData, theme)
    val returnButton = MyTextButton(res.get("return"), skinData, theme)

    init {
        pad(8f, 24f, 12f, 24f)
        contentTable.add(Label(res.get("stalled"), skinData.skin, theme)).height(16f)
        buttonTable.apply {
            pad(0f, 4f, 0f, 4f)
            defaults().width(180f).height(Const.BUTTON_HEIGHT).pad(0f).space(4f)
            add(undoButton)
            row()
            add(newGameButton)
            row()
            add(returnButton)
            row()
            add(exitButton).spaceTop(skinData.exitButtonTopMargin)
        }
    }
}
