package ogz.tripeaks.util

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Disposable

data class SkinData(
    val skin: Skin,
    val buttonPadTop: Float,
    val buttonPadBottom: Float,
    val exitButtonTopMargin: Float,
    val checkBoxTopMargin: Float
) : Disposable {
    override fun dispose() {
        skin.dispose()
    }
}