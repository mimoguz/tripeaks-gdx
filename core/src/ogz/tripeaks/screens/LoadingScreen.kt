package ogz.tripeaks.screens

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.app.KtxScreen
import ktx.collections.GdxArray
import ktx.scene2d.Scene2DSkin
import ktx.style.*
import ogz.tripeaks.*
import ogz.tripeaks.game.layout.Layout
import ogz.tripeaks.util.GamePreferences
import ogz.tripeaks.util.SkinData

class LoadingScreen(
        private val game: Game,
        private val batch: Batch,
        private val viewport: Viewport,
        private val assets: AssetManager,
        private val preferences: GamePreferences,
        private val layouts: List<Layout>
) :
        KtxScreen {

    override fun show() {
        for (asset in TextureAtlasAssets.values()) assets.load(asset)
        for (asset in TextureAssets.values()) assets.load(asset)
        for (asset in FontAssets.values()) assets.load(asset)
        for (asset in BundleAssets.values()) {
            assets.load(asset)
        }
    }

    override fun render(delta: Float) {
        assets.update()
        viewport.apply()

        if (assets.isFinished) {
            val skinData = buildSkin(assets[BundleAssets.Bundle].get("skinKey"))
            Scene2DSkin.defaultSkin = skinData.skin
            game.context.bindSingleton(skinData)
            game.addScreen(StartScreen(game, assets, viewport, batch, skinData, preferences, layouts))
            game.setScreen<StartScreen>()
            game.removeScreen<LoadingScreen>()
            dispose()
        }
    }

    private fun buildSkin(key: String): SkinData {
        if (key == "cjk") {
            val skin = makeCommonSkin(assets[FontAssets.UnifontCjk16])
            return SkinData(skin, 2f, 2f, 15f, 4f)
        } else {
            val skin = makeCommonSkin(assets[FontAssets.GameFont])
            return SkinData(skin, 3f, 4f, 19f, 2f)
        }
    }

    private fun makeCommonSkin(skinFont: BitmapFont): Skin =
            skin(assets[TextureAtlasAssets.Ui]) { skin ->

                color("light", 242f / 255f, 204f / 255f, 143f / 255f, 1f)
                color("dark", 76f / 255f, 56f / 255f, 77f / 255f, 1f)
                color("emphasisLight", 184f / 255f, 55f / 255f, 68f / 255f, 1f)
                color("emphasisDark", 224f / 255f, 122f / 255f, 95f / 255f, 1f)

                label {
                    font = skinFont
                    fontColor = skin["dark"]
                }
                label("light", extend = defaultStyle)
                label("dark", extend = defaultStyle) {
                    fontColor = skin["light"]
                }
                label("title_light", extend = defaultStyle) {
                    fontColor = skin["emphasisLight"]
                }
                label("title_dark", extend = defaultStyle) {
                    fontColor = skin["emphasisDark"]
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
                    font = skinFont
                    fontColor = skin["dark"]
                    pressedOffsetY = -1f
                }
                textButton("light", extend = defaultStyle)
                textButton("dark", extend = defaultStyle) {
                    up = skin["buttonUp_dark"]
                    down = skin["buttonDown_dark"]
                    disabled = skin["buttonDisabled_dark"]
                    fontColor = skin["light"]
                }
                textButton("menuItem_light") {
                    up = skin["menuItemUp"]
                    down = skin["menuItemDown"]
                    disabled = skin["menuItemUp"]
                    over = skin["menuItemOver"]
                    font = skinFont
                    fontColor = skin["dark"]
                }
                textButton("menuItem_dark") {
                    up = skin["menuItemUp_dark"]
                    down = skin["menuItemDown_dark"]
                    disabled = skin["menuItemUp_dark"]
                    over = skin["menuItemOver_dark"]
                    font = skinFont
                    fontColor = skin["light"]
                }
                textButton("redButton_light") {
                    up = skin["redButtonUp"]
                    down = skin["redButtonDown"]
                    disabled = skin["redButtonDisabled"]
                    font = skinFont
                    fontColor = skin["light"]
                    pressedOffsetY = -1f
                }
                textButton("redButton_dark", extend = "redButton_light") {
                    up = skin["redButtonUp_dark"]
                    down = skin["redButtonDown_dark"]
                    disabled = skin["redButtonDisabled_dark"]
                }

                window {
                    titleFont = skinFont
                    titleFontColor = skin["dark"]
                    background = skin["window"]
                }
                window("light", extend = defaultStyle)
                window("dark", extend = defaultStyle) {
                    titleFontColor = skin["light"]
                    background = skin["window_dark"]
                }
                checkBox {
                    checkboxOn = skin["checkboxOn"]
                    checkboxOff = skin["checkboxOff"]
                    font = skinFont
                    fontColor = skin["dark"]
                }

                checkBox("light", extend = defaultStyle)
                checkBox("dark", extend = defaultStyle) {
                    checkboxOn = skin["checkboxOn_dark"]
                    checkboxOff = skin["checkboxOff_dark"]
                    fontColor = skin["light"]
                }

                val listStyleDefault =  list {
                    font = skinFont
                    fontColorSelected = skin["dark"]
                    fontColorUnselected = skin["dark"]
                    selection = skin["menuItemDown"]
                    over = skin["menuItemOver"]
                    background = skin["window"]
                }
                list("light", extend = defaultStyle)
                val listStyleDark = list("dark", extend = defaultStyle) {
                    fontColorSelected = skin["light"]
                    fontColorUnselected = skin["light"]
                    selection = skin["menuItemDown_dark"]
                    over = skin["menuItemOver_dark"]
                    background = skin["window_dark"]
                }

                val scrollPaneStyleDefault =  scrollPane {
                    hScroll = skin["scroll"]
                    hScrollKnob = skin["scrollKnob"]
                    vScroll = skin["scroll"]
                    vScrollKnob = skin["scrollKnob"]
                    corner = skin["menuItemUp"]
                }
                scrollPane("light", extend = defaultStyle)
                val scrollPaneStyleDark = scrollPane("dark", extend = defaultStyle) {
                    hScroll = skin["scroll_dark"]
                    hScrollKnob = skin["scrollKnob_dark"]
                    vScroll = skin["scroll_dark"]
                    vScrollKnob = skin["scrollKnob_dark"]
                    corner = skin["menuItemUp_dark"]
                }

                selectBox {
                    font = skinFont
                    fontColor = skin["dark"]
                    background = skin["selectBoxUp"]
                    backgroundOpen = skin["selectBoxDown"]
                    scrollStyle = scrollPaneStyleDefault
                    listStyle = listStyleDefault
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

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }
}
