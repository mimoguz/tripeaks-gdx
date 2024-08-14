package ogz.tripeaks.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import ogz.tripeaks.Constants.TEXT_BUTTON_HORIZONTAL_PADDING
import ogz.tripeaks.Constants.TEXT_BUTTON_VERTICAL_PADDING
import ogz.tripeaks.assets.UiSkin

class LabelButton(skin: UiSkin, text: String?, private val action: () -> Unit = {}) :

    TextButton(text, skin) {

    init {
        pad(
            TEXT_BUTTON_VERTICAL_PADDING + skin.extraLineSpacing,
            TEXT_BUTTON_HORIZONTAL_PADDING,
            TEXT_BUTTON_VERTICAL_PADDING + skin.extraLineSpacing.coerceAtLeast(3f),
            TEXT_BUTTON_HORIZONTAL_PADDING
        )

        addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                action.invoke()
            }
        })
    }

}