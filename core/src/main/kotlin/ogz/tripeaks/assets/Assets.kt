package ogz.tripeaks.assets

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.utils.I18NBundle
import ktx.assets.getAsset
import ktx.assets.load

enum class TextureAssets(val path: String) {
    LightTitle("images/light_title.png"),
    DarkTitle("images/dark_title.png"),
    BlackTitle("images/black_title.png"),
}

fun AssetManager.load(asset: TextureAssets) = load<Texture>(asset.path)
operator fun AssetManager.get(asset: TextureAssets) = getAsset<Texture>(asset.path)

enum class TextureAtlasAssets(val path: String) {
    Images("images/images.atlas")
}

fun AssetManager.load(asset: TextureAtlasAssets) = load<TextureAtlas>(asset.path)
operator fun AssetManager.get(asset: TextureAtlasAssets) = getAsset<TextureAtlas>(asset.path)

enum class FontAssets(val path: String) {
    GamePixels("fonts/gamepixels.fnt"),
    UnifontCJK("fonts/gnu_unifont_cjk16_2.fnt")
}

fun AssetManager.load(asset: FontAssets) = load<BitmapFont>(asset.path)
operator fun AssetManager.get(asset: FontAssets) =  getAsset<BitmapFont>(asset.path)


enum class BundleAssets(val path: String) {
    Bundle("i8n/Bundle")
}

fun AssetManager.load(asset: BundleAssets) = load<I18NBundle>(asset.path)
operator fun AssetManager.get(asset: BundleAssets) = getAsset<I18NBundle>(asset.path)

enum class ShaderSourceAssets(val path: String) {
    Vert("shaders/basic.vert"),
    Dissolve("shaders/dissolve.frag"),
    Pixelate("shaders/pixelate.frag"),
    Blink("shaders/blink.frag"),
    FadeOut("shaders/fadeout.frag"),
}

fun AssetManager.load(asset: ShaderSourceAssets) = load<Text>(asset.path)
operator fun AssetManager.get(asset: ShaderSourceAssets) = getAsset<Text>(asset.path)

