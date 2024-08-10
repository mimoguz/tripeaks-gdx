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
import ogz.tripeaks.Constants

class UiSkin private constructor(
    atlas: TextureAtlas,
    uiFont: BitmapFont,
    textColor: Color,
    emphasisColor: Color,
    val resourcePrefix: String,
    val isDark: Boolean,
    val cjk: Boolean = false,
    val extraLineSpacing: Float = 0f
) : Skin(atlas) {

    constructor(assets: AssetManager, cjk: Boolean, dark: Boolean) : this(
        assets[TextureAtlasAssets.Ui],
        if (cjk) assets[FontAssets.UnifontCJK] else assets[FontAssets.GamePixels],
        if (dark) Constants.DARK_UI_TEXT else Constants.LIGHT_UI_TEXT,
        if (dark) Constants.DARK_UI_EMPHASIS else Constants.LIGHT_UI_EMPHASIS,
        if (dark) "dark" else "light",
        dark,
        cjk,
        if (cjk) Constants.UI_CJK_LINE_SPACING else 0f,
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
            up = skin["${resourcePrefix}_button_up"]
            down = skin["${resourcePrefix}_button_down"]
            disabled = skin["${resourcePrefix}_button_disabled"]
            pressedOffsetY = -1f
        }

        textButton {
            up = skin["${resourcePrefix}_button_up"]
            down = skin["${resourcePrefix}_button_down"]
            disabled = skin["${resourcePrefix}_button_disabled"]
            font = uiFont
            fontColor = skin[TEXT_COLOR]
            pressedOffsetY = -1f
        }

        textButton(MENU_ITEM_BUTTON_STYLE) {
            up = skin["${resourcePrefix}_menu_item_up"]
            down = skin["${resourcePrefix}_menu_item_down"]
            disabled = skin["${resourcePrefix}_menu_item_up"]
            over = skin["${resourcePrefix}_menu_item_over"]
            font = uiFont
            fontColor = skin[TEXT_COLOR]
        }

        window {
            titleFont = uiFont
            titleFontColor = skin[TEXT_COLOR]
            background = skin["${resourcePrefix}_menu"] // window
        }

        checkBox {
            checkboxOn = skin["${resourcePrefix}_checkbox_on"]
            checkboxOff = skin["${resourcePrefix}_checkbox_off"]
            font = uiFont
            fontColor = skin[TEXT_COLOR]
        }

        checkBox(RADIO_BUTTON_STYLE) {
            checkboxOn = skin["${resourcePrefix}_radio_on"]
            checkboxOnDisabled = skin["${resourcePrefix}_radio_on_disabled"]
            checkboxOff = skin["${resourcePrefix}_radio_off"]
            checkboxOffDisabled = skin["${resourcePrefix}_radio_off_disabled"]
            font = uiFont
            fontColor = skin[TEXT_COLOR]
        }

        val skinListStyle = list {
            font = uiFont
            fontColorSelected = skin[TEXT_COLOR]
            fontColorUnselected = skin[TEXT_COLOR]
            selection = skin["${resourcePrefix}_menu_item_down"]
            over = skin["${resourcePrefix}_menu_item_over"]
            background = skin["${resourcePrefix}_menu"]
        }

        val skinScrollPaneStyle = scrollPane {
            hScroll = skin["${resourcePrefix}_scroll"]
            hScrollKnob = skin["${resourcePrefix}_scroll_knob"]
            vScroll = skin["${resourcePrefix}_scroll"]
            vScrollKnob = skin["${resourcePrefix}_scroll_knob"]
            corner = skin["${resourcePrefix}_menu_item_down"] // TODO
        }

        selectBox {
            font = uiFont
            fontColor = skin[TEXT_COLOR]
            background = skin["${resourcePrefix}_selectbox_up"]
            backgroundOpen = skin["${resourcePrefix}_selectbox_down"]
            scrollStyle = skinScrollPaneStyle
            listStyle = skinListStyle
        }

        addStyle("default", PopTableStyle().apply {
            background = skin["${resourcePrefix}_menu"] // window
            // stageBackground = skin["overlay_$resourcePostfix"]
        })

        addStyle("menu", PopTableStyle().apply {
            background = skin["${resourcePrefix}_menu"]
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