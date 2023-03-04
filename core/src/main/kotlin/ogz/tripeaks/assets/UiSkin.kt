package ogz.tripeaks.assets

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.ray3k.stripe.PopTable.PopTableStyle
import ktx.style.addStyle
import ktx.style.button
import ktx.style.checkBox
import ktx.style.color
import ktx.style.defaultStyle
import ktx.style.get
import ktx.style.label
import ktx.style.list
import ktx.style.scrollPane
import ktx.style.selectBox
import ktx.style.textButton
import ktx.style.window
import ogz.tripeaks.screens.Constants

class UiSkin(
    atlas: TextureAtlas,
    uiFont: BitmapFont,
    textColor: Color,
    emphasisColor: Color,
    val resourcePrefix: String,
    val isDark: Boolean
) : Skin(atlas) {
    constructor(assets: AssetManager, cjk: Boolean, dark: Boolean) : this(
        assets[TextureAtlasAssets.Ui],
        if (cjk) assets[FontAssets.GamePixels] else assets[FontAssets.GamePixels], // TODO
        if (dark) Constants.DARK_UI_TEXT else Constants.LIGHT_UI_TEXT,
        if (dark) Constants.DARK_UI_EMPHASIS else Constants.LIGHT_UI_EMPHASIS,
        if (dark) "dark" else "light",
        dark
    )

    init {
        val skin = this

        color(TEXT_COLOR, textColor.r, textColor.g, textColor.b, textColor.a)
        color(EMPHASIS_COLOR, emphasisColor.r, emphasisColor.g, emphasisColor.b, emphasisColor.a)

        label {
            font = uiFont
            fontColor = skin[TEXT_COLOR]
        }

        label(TITLE_LABEL_STYLE, extend = defaultStyle) {
            fontColor = skin[EMPHASIS_COLOR]
        }

        button {
            up = skin["${resourcePrefix}_buttonUp"]
            down = skin["${resourcePrefix}_buttonDown"]
            disabled = skin["${resourcePrefix}_buttonDisabled"]
            pressedOffsetY = -1f
        }

        textButton {
            up = skin["${resourcePrefix}_buttonUp"]
            down = skin["${resourcePrefix}_buttonDown"]
            disabled = skin["${resourcePrefix}_buttonDisabled"]
            font = uiFont
            fontColor = skin[TEXT_COLOR]
            pressedOffsetY = -1f
        }

        textButton(MENU_ITEM_BUTTON_STYLE) {
            up = skin["${resourcePrefix}_menuItemUp"]
            down = skin["${resourcePrefix}_menuItemDown"]
            disabled = skin["${resourcePrefix}_menuItemUp"]
            over = skin["${resourcePrefix}_menuItemOver"]
            font = uiFont
            fontColor = skin[TEXT_COLOR]
        }

        window {
            titleFont = uiFont
            titleFontColor = skin[TEXT_COLOR]
            background = skin["${resourcePrefix}_window"]
        }

        checkBox {
            checkboxOn = skin["${resourcePrefix}_checkboxOn_alt"]
            checkboxOff = skin["${resourcePrefix}_checkboxOff_alt"]
            font = uiFont
            fontColor = skin[TEXT_COLOR]
        }

        checkBox(RADIO_BUTTON_STYLE) {
            checkboxOn = skin["${resourcePrefix}_radioButtonOn"]
            checkboxOnDisabled = skin["${resourcePrefix}_radioButtonOnDisabled"]
            checkboxOff = skin["${resourcePrefix}_radioButtonOff"]
            checkboxOffDisabled = skin["${resourcePrefix}_radioButtonOffDisabled"]
            font = uiFont
            fontColor = skin[TEXT_COLOR]
        }

        val skinListStyle = list {
            font = uiFont
            fontColorSelected = skin[TEXT_COLOR]
            fontColorUnselected = skin[TEXT_COLOR]
            selection = skin["${resourcePrefix}_menuItemDown"]
            over = skin["${resourcePrefix}_menuItemOver"]
            background = skin["${resourcePrefix}_window"]
        }

        val skinScrollPaneStyle = scrollPane {
            hScroll = skin["${resourcePrefix}_scroll"]
            hScrollKnob = skin["${resourcePrefix}_scrollKnob"]
            vScroll = skin["${resourcePrefix}_scroll"]
            vScrollKnob = skin["${resourcePrefix}_scrollKnob"]
            corner = skin["${resourcePrefix}_menuItemUp"]
        }

        selectBox {
            font = uiFont
            fontColor = skin[TEXT_COLOR]
            background = skin["${resourcePrefix}_selectBoxUp"]
            backgroundOpen = skin["${resourcePrefix}_selectBoxDown"]
            scrollStyle = skinScrollPaneStyle
            listStyle = skinListStyle
        }

        addStyle("default", PopTableStyle().apply {
            background = skin["${resourcePrefix}_window"]
            // stageBackground = skin["overlay_$resourcePostfix"]
        })

        addStyle("menu", PopTableStyle().apply {
            background = skin["${resourcePrefix}_window"]
        })
    }

    companion object {
        private const val TEXT_COLOR = "textColor"
        private const val EMPHASIS_COLOR = "emphasisColor"
        const val TITLE_LABEL_STYLE = "title"
        const val MENU_ITEM_BUTTON_STYLE = "menuItem"
        const val RADIO_BUTTON_STYLE = "radioButton"
    }
}