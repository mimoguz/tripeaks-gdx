package ogz.tripeaks.screens.dialogs

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.utils.Align
import ogz.tripeaks.Const
import ogz.tripeaks.screens.controls.MyTextButton
import ogz.tripeaks.util.SkinData

class GameMenu(skinData: SkinData, theme: String, attached: Actor) : Window("", skinData.skin, theme) {

    val btn1 = MyTextButton("Button 1", skinData, theme)
    val btn2 = MyTextButton("Button 2", skinData, theme)

    init {
        isModal = false
        isVisible = false
        val layout = HorizontalGroup().apply {
            add(btn1).width(100f).height(24f).spaceBottom(4f)
            row()
            add(btn2).width(100f).height(24f)
            pad(0f)
        }
        add(layout)
        pad(6f)
        width = 112f
        height = 64f
        setPosition(
            attached.x + attached.width,
            attached.y + 1f,
            Align.topRight
        )
    }
}