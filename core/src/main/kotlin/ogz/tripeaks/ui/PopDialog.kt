package ogz.tripeaks.ui

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.ray3k.stripe.PopTable
import ogz.tripeaks.Constants
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.pad

open class PopDialog(protected val uiSkin: UiSkin, title: String? = null, icon: Drawable? = null) :
    PopTable(uiSkin) {

    val buttonTable = Table(uiSkin)
    val contentTable = Table(uiSkin)
    val header = Table(uiSkin)

    init {
        this.defaults().expandX().fillX()
        pad(
            Constants.UI_PANEL_VERTICAL_BORDER, Constants.UI_PANEL_HORIZONTAL_BORDER
        )
        icon?.let {
            header.add(Image(icon)).padRight(4f).left()
            header.padLeft(-icon.minWidth - 4f)
        }
        title?.let {
            header.add(Label(it, uiSkin)).expandX().fillX()
        }
        if (header.hasChildren()) {
            this.add(header).padBottom(Constants.UI_VERTICAL_SPACING).row()
        }

        this.add(contentTable).expand().row()
        this.add(buttonTable).padTop(Constants.UI_VERTICAL_SPACING)

        isModal = true
        isHideOnUnfocus = false
    }

}