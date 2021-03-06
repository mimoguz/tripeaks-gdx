package ogz.tripeaks.screens

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.ObjectMap
import ktx.app.KtxScreen
import ktx.graphics.use
import ktx.scene2d.Scene2DSkin
import ktx.style.*
import ogz.tripeaks.*
import ogz.tripeaks.data.SkinData

class LoadingScreen(val game: Game) : KtxScreen {
    private val camera = OrthographicCamera()
    private val viewport = IntegerScalingViewport(320, 200, camera)

    override fun show() {
        for (asset in TextureAtlasAssets.values()) game.assets.load(asset)
        for (asset in TextureAssets.values()) game.assets.load(asset)
        for (asset in FontAssets.values()) game.assets.load(asset)
        for (asset in BundleAssets.values()) {
            game.assets.load(asset)
        }
    }

    override fun render(delta: Float) {
        game.assets.update()
        camera.update()

        game.batch.use(camera) {
            game.font.draw(it, "...", -5f, 0f)
        }

        if (game.assets.isFinished) {
            val skins = buildSkins()
            Scene2DSkin.defaultSkin = skins["default"].skin
            game.addScreen(StartScreen(game, skins))
            game.setScreen<StartScreen>()
            game.removeScreen<LoadingScreen>()
            dispose()
        }
    }

    private fun buildSkins(): ObjectMap<String, SkinData> {
        val default = makeCommonSkin(game.assets[FontAssets.GameFont])
        val cjk = makeCommonSkin(game.assets[FontAssets.UnifontCjk16])
        return ObjectMap<String, SkinData>(2).apply {
            put("default", SkinData(default, 3f, 4f, 19f, 2f))
            put("cjk", SkinData(cjk, -1f, 4f, 7f, 4f))
        }
    }

    private fun makeCommonSkin(skinFont: BitmapFont): Skin =
        skin(game.assets[TextureAtlasAssets.Ui]) { skin ->
            color("light", 242f / 255f, 204f / 255f, 143f / 255f, 1f)
            color("dark", 76f / 255f, 56f / 255f, 77f / 255f, 1f)
            label {
                font = skinFont
                fontColor = skin["dark"]
            }
            label("light", extend = defaultStyle)
            label("dark", extend = defaultStyle) {
                fontColor = skin["light"]
            }
            button {
                up = skin["buttonUp"]
                down = skin["buttonDown"]
                disabled = skin["buttonDisabled"]
                pressedOffsetY = -1f
            }
            button("light", extend = defaultStyle)
            button("dark", extend = defaultStyle) {
                up = skin["buttonUpDark"]
                down = skin["buttonDownDark"]
                disabled = skin["buttonDisabledDark"]
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
                up = skin["buttonUpDark"]
                down = skin["buttonDownDark"]
                disabled = skin["buttonDisabledDark"]
                fontColor = skin["light"]
            }
            window {
                titleFont = skinFont
                titleFontColor = skin["dark"]
                background = skin["window"]
            }
            window("light", extend = defaultStyle)
            window("dark", extend = defaultStyle) {
                titleFontColor = skin["light"]
                background = skin["windowDark"]
            }
            checkBox {
                checkboxOn = skin["checkboxOnLight"]
                checkboxOff = skin["checkboxOffLight"]
                font = skinFont
                fontColor = skin["dark"]
            }
            checkBox("light", extend = defaultStyle)
            checkBox(name = "dark", extend = defaultStyle) {
                checkboxOn = skin["checkboxOnDark"]
                checkboxOff = skin["checkboxOffDark"]
                fontColor = skin["light"]
            }
        }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }
}
