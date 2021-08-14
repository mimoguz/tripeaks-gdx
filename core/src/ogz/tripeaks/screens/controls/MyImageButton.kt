package ogz.tripeaks.screens.controls

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import ogz.tripeaks.util.SkinData

class MyImageButton(graphic: Sprite, skinData: SkinData, theme: String) :
    Button(skinData.skin, theme) {

    init {
        setGraphic(graphic)
    }

    fun setGraphic(graphic: Sprite) {
        children.clear()
        val image = Image(graphic).apply {
            setPosition(
                (this@MyImageButton.width - width) / 2f,
                (this@MyImageButton.height - height) / 2f
            )
            touchable = Touchable.disabled
        }
        children.add(image)
    }

    fun setAction(action: () -> Unit) {
        addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                action.invoke()
            }
        })
    }

    override fun setSize(w: Float, h: Float) {
        super.setSize(w, h)
        children.firstOrNull()?.apply {
            setPosition(
                (w - width) / 2f,
                (h - height) / 2f
            )
        }
    }
}
