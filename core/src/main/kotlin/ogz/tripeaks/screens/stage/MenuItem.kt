package ogz.tripeaks.screens.stage

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.ray3k.stripe.PopTable
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.screens.Constants

class MenuItem(text: String, skin: UiSkin) : TextButton(text, skin, UiSkin.MENU_ITEM_BUTTON_STYLE) {
    init {
        pad(4f)
    }

    fun onClick(action: () -> Unit) {
        addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                action.invoke()
                val menu = parent
                if (menu is PopTable) {
                    menu.hide()
                }
            }
        })
    }
}