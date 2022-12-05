package ogz.tripeaks.ui

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener

class IconButton(skin: Skin, icon: TextureRegion?) : Button(skin) {

    init {
        setIcon(icon)
    }

    fun onClick(action: () -> Unit) {
        addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                action.invoke()
            }
        })
    }

    fun setIcon(icon: TextureRegion?) {
        children.clear()
        if (icon != null) {
            val image = Image(icon).apply {
                val btn = this@IconButton
                setPosition(
                    MathUtils.floor((btn.width - width) / 2f).toFloat(),
                    MathUtils.floor((btn.width - width) / 2f).toFloat()
                )
                touchable = Touchable.disabled
            }
            children.add(image)
        }
    }

    override fun setSize(w: Float, h: Float) {
        super.setSize(w, h)
        children.forEach {
            it.setPosition(
                MathUtils.floor((w - it.width) / 2f).toFloat(),
                MathUtils.floor((h - it.height) / 2f).toFloat()
            )
        }
    }
}