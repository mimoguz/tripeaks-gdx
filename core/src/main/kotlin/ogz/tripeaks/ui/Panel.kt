package ogz.tripeaks.ui

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import ogz.tripeaks.Constants
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.pad
import kotlin.math.nextDown

class Panel(
    title: String,
    uiSkin: UiSkin,
    latin: Boolean = false,
    columns: Int = 1
): Table(uiSkin) {

    init {
        val divPadding =
            if (latin) 2f
            else (uiSkin.extraLineSpacing / 2f).nextDown().coerceAtLeast(2f)
        val titlePadding = if (latin) 2f else uiSkin.extraLineSpacing.coerceAtLeast(2f)
        val labelStyle = if (latin) UiSkin.TITLE_LABEL_STYLE_LATIN else UiSkin.TITLE_LABEL_STYLE

        pad(Constants.UI_VERTICAL_SPACING, Constants.UI_HORIZONTAL_SPACING + 3)
        setBackground(uiSkin.panelBg)

        add(Label(title, uiSkin, labelStyle)).colspan(columns).left().padBottom(titlePadding)
        row()
        add(Image(uiSkin.line))
            .colspan(columns)
            .expandX()
            .fillX()
            .pad(divPadding, -Constants.UI_HORIZONTAL_SPACING)
        row()
    }

}