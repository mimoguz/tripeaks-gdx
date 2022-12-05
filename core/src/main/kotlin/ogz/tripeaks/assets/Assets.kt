package ogz.tripeaks.assets

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import ktx.assets.getAsset
import ktx.assets.load

enum class TextureAssets(val path: String) {
    LightTitle("images/light_title.png"),
    DarkTitle("images/dark_title.png"),
}

fun AssetManager.load(asset: TextureAssets) = load<Texture>(asset.path)
operator fun AssetManager.get(asset: TextureAssets) = getAsset<Texture>(asset.path)

enum class TextureAtlasAssets(val path: String) {
    Cards("images/cards.atlas"),
    Ui("ui/ui.atlas")
}

fun AssetManager.load(asset: TextureAtlasAssets) = load<TextureAtlas>(asset.path)
operator fun AssetManager.get(asset: TextureAtlasAssets) = getAsset<TextureAtlas>(asset.path)

enum class FontAssets(val path: String) {
    GamePixels("ui/gamepixels.fnt")
}

fun AssetManager.load(asset: FontAssets) = load<BitmapFont>(asset.path)
operator fun AssetManager.get(asset: FontAssets) =  getAsset<BitmapFont>(asset.path)