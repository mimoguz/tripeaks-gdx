package ogz.tripeaks.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import com.ray3k.stripe.PopTable
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.Constants.TEXT_BUTTON_HORIZONTAL_PADDING
import ogz.tripeaks.Constants.TEXT_BUTTON_VERTICAL_PADDING

class MenuItem(text: String, skin: UiSkin, private val action: () -> Unit) :
    TextButton(text, skin, UiSkin.MENU_ITEM_BUTTON_STYLE) {

    init {
        pad(
            TEXT_BUTTON_VERTICAL_PADDING + skin.extraLineSpacing,
            TEXT_BUTTON_HORIZONTAL_PADDING,
            TEXT_BUTTON_VERTICAL_PADDING + skin.extraLineSpacing,
            TEXT_BUTTON_HORIZONTAL_PADDING
        )
        this.label.setAlignment(Align.left)

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