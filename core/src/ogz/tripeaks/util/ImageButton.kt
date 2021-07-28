package ogz.tripeaks.util

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener

class ImageButton(skin: Skin, style: String, w: Float, h: Float, graphic: Sprite, action: () -> Unit) :
    Button(skin, style) {

    init {
        setSize(w, h)
        setGraphic(graphic)
        addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                action()
            }
        })
    }

    fun setGraphic(graphic: Sprite) {
        children.clear()
        val image = Image(graphic).apply {
            setPosition( (this@ImageButton.width - width) / 2f, (this@ImageButton.height - height) / 2f)
            touchable = Touchable.disabled
        }
        children.add(image)
    }
}