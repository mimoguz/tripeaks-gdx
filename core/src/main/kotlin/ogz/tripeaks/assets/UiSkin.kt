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

class UiSkin(atlas: TextureAtlas, uiFont: BitmapFont) : Skin(atlas) {

    init {
        val skin = this

        color("lightText", 242, 204, 143)
        color("darkText", 76, 56, 77)
        color("lightEmphasis", 184, 55, 68)
        color("darkEmphasis", 224, 122, 95)

        label {
            font = uiFont
            fontColor = skin["darkText"]
        }

        label("light", extend = defaultStyle)

        label("dark", extend = defaultStyle) {
            fontColor = skin["lightText"]
        }

        label("title_light", extend = defaultStyle) {
            fontColor = skin["lightEmphasis"]
        }

        label("title_dark", extend = defaultStyle) {
            fontColor = skin["darkEmphasis"]
        }

        button {
            up = skin["buttonUp"]
            down = skin["buttonDown"]
            disabled = skin["buttonDisabled"]
            pressedOffsetY = -1f
        }

        button("light", extend = defaultStyle)

        button("dark", extend = defaultStyle) {
            up = skin["buttonUp_dark"]
            down = skin["buttonDown_dark"]
            disabled = skin["buttonDisabled_dark"]
        }

        textButton {
            up = skin["buttonUp"]
            down = skin["buttonDown"]
            disabled = skin["buttonDisabled"]
            font = uiFont
            fontColor = skin["darkText"]
            pressedOffsetY = -1f
        }

        textButton("light", extend = defaultStyle)

        textButton("dark", extend = defaultStyle) {
            up = skin["buttonUp_dark"]
            down = skin["buttonDown_dark"]
            disabled = skin["buttonDisabled_dark"]
            fontColor = skin["lightText"]
        }

        textButton("menuItem_light") {
            up = skin["menuItemUp"]
            down = skin["menuItemDown"]
            disabled = skin["menuItemUp"]
            over = skin["menuItemOver"]
            font = uiFont
            fontColor = skin["darkText"]
        }

        textButton("menuItem_dark") {
            up = skin["menuItemUp_dark"]
            down = skin["menuItemDown_dark"]
            disabled = skin["menuItemUp_dark"]
            over = skin["menuItemOver_dark"]
            font = uiFont
            fontColor = skin["lightText"]
        }

        textButton("redButton_light") {
            up = skin["redButtonUp"]
            down = skin["redButtonDown"]
            disabled = skin["redButtonDisabled"]
            font = uiFont
            fontColor = skin["lightText"]
            pressedOffsetY = -1f
        }

        textButton("redButton_dark", extend = "redButton_light") {
            up = skin["redButtonUp_dark"]
            down = skin["redButtonDown_dark"]
            disabled = skin["redButtonDisabled_dark"]
        }

        window {
            titleFont = uiFont
            titleFontColor = skin["darkText"]
            background = skin["window"]
        }

        window("light", extend = defaultStyle)

        window("dark", extend = defaultStyle) {
            titleFontColor = skin["lightText"]
            background = skin["window_dark"]
        }

        checkBox {
            checkboxOn = skin["checkboxOn"]
            checkboxOff = skin["checkboxOff"]
            font = uiFont
            fontColor = skin["darkText"]
        }

        checkBox("light", extend = defaultStyle)

        checkBox("dark", extend = defaultStyle) {
            checkboxOn = skin["checkboxOn_dark"]
            checkboxOff = skin["checkboxOff_dark"]
            fontColor = skin["lightText"]
        }

        list {
            font = uiFont
            fontColorSelected = skin["darkText"]
            fontColorUnselected = skin["darkText"]
            selection = skin["menuItemDown"]
            over = skin["menuItemOver"]
            background = skin["window"]
        }

        val listStyleLight = list("light", extend = defaultStyle)

        val listStyleDark = list("dark", extend = defaultStyle) {
            fontColorSelected = skin["lightText"]
            fontColorUnselected = skin["lightText"]
            selection = skin["menuItemDown_dark"]
            over = skin["menuItemOver_dark"]
            background = skin["window_dark"]
        }

        scrollPane {
            hScroll = skin["scroll"]
            hScrollKnob = skin["scrollKnob"]
            vScroll = skin["scroll"]
            vScrollKnob = skin["scrollKnob"]
            corner = skin["menuItemUp"]
        }

        val scrollPaneStyleLight = scrollPane("light", extend = defaultStyle)

        val scrollPaneStyleDark = scrollPane("dark", extend = defaultStyle) {
            hScroll = skin["scroll_dark"]
            hScrollKnob = skin["scrollKnob_dark"]
            vScroll = skin["scroll_dark"]
            vScrollKnob = skin["scrollKnob_dark"]
            corner = skin["menuItemUp_dark"]
        }

        selectBox {
            font = uiFont
            fontColor = skin["dark"]
            background = skin["selectBoxUp"]
            backgroundOpen = skin["selectBoxDown"]
            scrollStyle = scrollPaneStyleLight
            listStyle = listStyleLight
        }

        selectBox("light", extend = defaultStyle)

        selectBox("dark", extend = defaultStyle) {
            fontColor = skin["light"]
            background = skin["selectBoxUp_dark"]
            backgroundOpen = skin["selectBoxDown_dark"]
            scrollStyle = scrollPaneStyleDark
            listStyle = listStyleDark
        }
    }

    companion object {
        private fun Skin.color(name: String, r: Int, g: Int, b: Int): Color = color(name, r / 255f, g / 255f, b / 255f)
    }
}