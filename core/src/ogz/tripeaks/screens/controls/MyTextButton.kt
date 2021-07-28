package ogz.tripeaks.screens.controls

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import ogz.tripeaks.util.SkinData

class MyTextButton(text: String, skinData: SkinData, theme: String) : TextButton(text, skinData.skin, theme) {
    init {
        pad(skinData.buttonPadTop, 8f, skinData.buttonPadBottom, 8f)
    }

    fun setAction(action: () -> Unit) {
        addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                action.invoke()
            }
        })
    }
}