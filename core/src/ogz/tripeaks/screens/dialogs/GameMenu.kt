package ogz.tripeaks.screens.dialogs

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.I18NBundle
import ogz.tripeaks.screens.controls.MyMenuItem
import ogz.tripeaks.util.SkinData

class GameMenu(skinData: SkinData, theme: String, res: I18NBundle, attached: Actor) :
    Window("", skinData.skin, theme) {

    val newGameButton = MyMenuItem(res.get("newGameShort") , skinData, theme)
    val exitButton = MyMenuItem(res.get("exit"), skinData, theme)

    init {
        isModal = false
        isVisible = false
        val layout = HorizontalGroup().apply {
            add(newGameButton).width(100f).height(24f).spaceBottom(4f)
            row()
            add(exitButton).width(100f).height(24f)
            pad(0f)
        }
        add(layout)
        pad(4f, 4f, 6f, 4f)
        width = 108f
        height = 62f
        setPosition(
            attached.x + attached.width,
            attached.y + 3f,
            Align.topRight
        )
    }
}