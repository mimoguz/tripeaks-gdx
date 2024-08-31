package ogz.tripeaks.assets

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
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
import ogz.tripeaks.models.ThemeMode

class UiSkin private constructor(
    atlas: TextureAtlas,
    uiFont: BitmapFont,
    latinFont: BitmapFont,
    textColor: Color,
    emphasisColor: Color,
    val resourcePrefix: String,
    val isDark: Boolean,
    val cjk: Boolean = false,
    val extraLineSpacing: Float = 0f
) : Skin(atlas) {

    val iconLink: Drawable = findIcon(atlas, "link")
    val iconOptions: Drawable = findIcon(atlas, "options")
    val iconAbout: Drawable = findIcon(atlas, "about")
    val iconStatistics: Drawable = findIcon(atlas, "stats")
    val iconWin: Drawable = findIcon(atlas, "win")
    val iconLose: Drawable = findIcon(atlas, "lose")
    val panelBg = find9Patch(atlas, "panel")
    val line = find9Patch(atlas, "line")

    constructor(
        assets: AssetManager,
        cjk: Boolean,
        theme: ThemeMode,
        darkSystem: Boolean
    ) : this(
        assets[TextureAtlasAssets.Images],
        if (cjk) assets[FontAssets.UnifontCJK] else assets[FontAssets.GamePixels],
        assets[FontAssets.GamePixels],
        theme.select(
            darkSystem,
            Constants.LIGHT_UI_TEXT,
            Constants.DARK_UI_TEXT,
            Constants.BLACK_UI_TEXT
        ),
        theme.select(
            darkSystem,
            Constants.LIGHT_UI_EMPHASIS,
            Constants.DARK_UI_EMPHASIS,
            Constants.BLACK_UI_EMPHASIS
        ),
        theme.resource(darkSystem),
        theme.isDark(darkSystem),
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

        label(LATIN_LABEL_STYLE, extend = defaultStyle) {
            font = latinFont
        }

        label(TITLE_LABEL_STYLE_LATIN, extend = defaultStyle) {
            font = latinFont
            fontColor = skin[EMPHASIS_COLOR]
        }

        button {
            up = skin["${resourcePrefix}_button_up"]
            down = skin["${resourcePrefix}_button_down"]
            disabled = skin["${resourcePrefix}_button_disabled"]
            pressedOffsetY = if (resourcePrefix == "black") -2f else -1f
        }

        textButton {
            up = skin["${resourcePrefix}_button_up"]
            down = skin["${resourcePrefix}_button_down"]
            disabled = skin["${resourcePrefix}_button_disabled"]
            font = uiFont
            fontColor = skin[TEXT_COLOR]
            pressedOffsetY = if (resourcePrefix == "black") -2f else -1f
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

        addStyle(MENU_POP_STYLE, PopTableStyle().apply {
            background = skin["${resourcePrefix}_menu"]
        })

    }

    fun getThemedDrawable(name: String): Drawable = getDrawable("${resourcePrefix}_${name}")

    fun getThemedDrawable(name: String, index: Int): Drawable =
        TextureRegionDrawable(atlas.findRegion("${resourcePrefix}_${name}", index))

    private fun findIcon(atlas: TextureAtlas, name: String): Drawable {
        return TextureRegionDrawable(atlas.findRegion("${resourcePrefix}_icon_$name"))
    }

    private fun find9Patch(atlas: TextureAtlas, name: String): NinePatchDrawable {
        return NinePatchDrawable(NinePatch(atlas.createPatch("${resourcePrefix}_${name}")))
    }

    companion object {

        private const val TEXT_COLOR = "textColor"
        private const val EMPHASIS_COLOR = "emphasisColor"
        const val TITLE_LABEL_STYLE = "title"
        const val TITLE_LABEL_STYLE_LATIN = "title_latin"
        const val LATIN_LABEL_STYLE = "label_latin"
        const val MENU_ITEM_BUTTON_STYLE = "menuItem"
        const val RADIO_BUTTON_STYLE = "radioButton"
        const val MENU_POP_STYLE = "menu"
    }

}