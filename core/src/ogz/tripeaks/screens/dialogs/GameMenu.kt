package ogz.tripeaks.screens.dialogs

import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.utils.Align
import ogz.tripeaks.Const
import ogz.tripeaks.screens.controls.MyTextButton
import ogz.tripeaks.util.SkinData

class GameMenu(skinData: SkinData, theme: String) : Window("", skinData.skin, theme) {

    val btn1 = MyTextButton("Button 1", skinData, theme)
    val btn2 = MyTextButton("Button 2", skinData, theme)

    init {
        isModal = true
        setPosition(Const.STACK_POSITION.x + 2f * Const.CELL_WIDTH, 30f, Align.topRight)
        isVisible = false
        val layout = HorizontalGroup().apply {
            add(btn1)
            add(btn2)
        }
        add(layout)
    }
}