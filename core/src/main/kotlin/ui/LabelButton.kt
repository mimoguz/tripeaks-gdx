package ogz.tripeaks.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import ogz.tripeaks.screens.Constants.TEXT_BUTTON_HORIZONTAL_PADDING
import ogz.tripeaks.screens.Constants.TEXT_BUTTON_VERTICAL_PADDING

class LabelButton(skin: Skin, text: String?, private var action: () -> Unit = {}) :
    TextButton(text, skin) {

    init {
        pad(
            TEXT_BUTTON_VERTICAL_PADDING,
            TEXT_BUTTON_HORIZONTAL_PADDING,
            TEXT_BUTTON_VERTICAL_PADDING,
            TEXT_BUTTON_HORIZONTAL_PADDING
        )

        addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                action.invoke()
            }
        })
    }

    fun onClick(action: () -> Unit) {
        this.action = action
    }
}