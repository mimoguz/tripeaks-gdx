package ogz.tripeaks.screens.dialogs

import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.I18NBundle
import ogz.tripeaks.Const
import ogz.tripeaks.game.Statistics
import ogz.tripeaks.screens.controls.MyTextButton
import ogz.tripeaks.util.SkinData

// TODO: Finish dialog
class StatisticsDialog(
    skinData: SkinData,
    theme: String,
    statistics: Statistics,
    val res: I18NBundle,
) : Dialog("", skinData.skin, theme) {

    private val returnButton = MyTextButton(res.get("return"), skinData, theme)

    init {
        pad(16f, 24f, 16f, 24f)
        contentTable.apply {
        }
        buttonTable.apply {
            pad(4f, 4f, 0f, 4f)
            defaults().width(108f).space(4f).height(Const.BUTTON_HEIGHT).pad(0f)
            add(returnButton)
        }
    }
}
