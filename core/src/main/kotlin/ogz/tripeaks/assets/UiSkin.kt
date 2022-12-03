package ogz.tripeaks.assets

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Skin
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

class UiSkin(
    atlas: TextureAtlas,
    uiFont: BitmapFont,
    textColor: Color,
    emphasisColor: Color,
    resourcePostfix: String
) : Skin(atlas) {

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
            up = skin["buttonUp_$resourcePostfix"]
            down = skin["buttonDown_$resourcePostfix"]
            disabled = skin["buttonDisabled_$resourcePostfix"]
            pressedOffsetY = -1f
        }

        textButton {
            up = skin["buttonUp_$resourcePostfix"]
            down = skin["buttonDown_$resourcePostfix"]
            disabled = skin["buttonDisabled_$resourcePostfix"]
            font = uiFont
            fontColor = skin[TEXT_COLOR]
            pressedOffsetY = -1f
        }

        textButton(MENU_ITEM_BUTTON_STYLE) {
            up = skin["menuItemUp_$resourcePostfix"]
            down = skin["menuItemDown_$resourcePostfix"]
            disabled = skin["menuItemUp_$resourcePostfix"]
            over = skin["menuItemOver_$resourcePostfix"]
            font = uiFont
            fontColor = skin[TEXT_COLOR]
        }

        window {
            titleFont = uiFont
            titleFontColor = skin[TEXT_COLOR]
            background = skin["window_$resourcePostfix"]
        }

        checkBox {
            checkboxOn = skin["checkboxOn_$resourcePostfix"]
            checkboxOff = skin["checkboxOff_$resourcePostfix"]
            font = uiFont
            fontColor = skin[TEXT_COLOR]
        }

        val skinListStyle = list {
            font = uiFont
            fontColorSelected = skin[TEXT_COLOR]
            fontColorUnselected = skin[TEXT_COLOR]
            selection = skin["menuItemDown_$resourcePostfix"]
            over = skin["menuItemOver_$resourcePostfix"]
            background = skin["window_$resourcePostfix"]
        }

        val skinScrollPaneStyle = scrollPane {
            hScroll = skin["scroll_$resourcePostfix"]
            hScrollKnob = skin["scrollKnob_$resourcePostfix"]
            vScroll = skin["scroll_$resourcePostfix"]
            vScrollKnob = skin["scrollKnob_$resourcePostfix"]
            corner = skin["menuItemUp_$resourcePostfix"]
        }

        selectBox {
            font = uiFont
            fontColor = skin[TEXT_COLOR]
            background = skin["selectBoxUp_$resourcePostfix"]
            backgroundOpen = skin["selectBoxDown_$resourcePostfix"]
            scrollStyle = skinScrollPaneStyle
            listStyle = skinListStyle
        }
    }

    companion object {
        private const val TEXT_COLOR = "textColor"
        private const val EMPHASIS_COLOR = "emphasisColor"
        const val TITLE_LABEL_STYLE = "title"
        const val MENU_ITEM_BUTTON_STYLE = "menuItem"
    }
}