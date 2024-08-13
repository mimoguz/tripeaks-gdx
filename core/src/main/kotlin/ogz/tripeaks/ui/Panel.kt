package ogz.tripeaks.ui

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import ogz.tripeaks.Constants
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.pad
import kotlin.math.nextDown

class Panel(
    title: String,
    skin: UiSkin,
    uiAtlas: TextureAtlas,
    latin: Boolean = false,
    columns: Int = 1
): Table(skin) {

    init {
        val uiSkin = skin as UiSkin
        val divider = uiAtlas.findRegion("${uiSkin.resourcePrefix}_line")
        val bg = NinePatchDrawable(uiAtlas.createPatch("${uiSkin.resourcePrefix}_panel"))
        val divPadding =
            if (latin) 2f
            else (uiSkin.extraLineSpacing / 2f).nextDown().coerceAtLeast(2f)
        val titlePadding = if (latin) 2f else uiSkin.extraLineSpacing.coerceAtLeast(2f)
        val labelStyle = if (latin) UiSkin.TITLE_LABEL_STYLE_LATIN else UiSkin.TITLE_LABEL_STYLE

        pad(Constants.UI_VERTICAL_SPACING, Constants.UI_HORIZONTAL_SPACING + 3)
        setBackground(bg)

        add(Label(title, skin, labelStyle)).colspan(columns).left().padBottom(titlePadding)
        row()
        add(Image(divider))
            .colspan(columns)
            .expandX()
            .fillX()
            .pad(divPadding, -Constants.UI_HORIZONTAL_SPACING)
        row()
    }

}