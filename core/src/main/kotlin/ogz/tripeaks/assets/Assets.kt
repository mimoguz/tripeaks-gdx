package ogz.tripeaks.assets

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import ktx.assets.getAsset
import ktx.assets.load

enum class TextureAtlasAssets(val path: String) {
    Cards("images/cards.atlas"),
    Ui("images/ui.atlas")
}

fun AssetManager.load(asset: TextureAtlasAssets) = load<TextureAtlas>(asset.path)
operator fun AssetManager.get(asset: TextureAtlasAssets) = getAsset<TextureAtlas>(asset.path)

enum class FontAssets(val path: String) {
    GamePixels("ui/gamepixels.fnt")
}

fun AssetManager.load(asset: FontAssets) = load<BitmapFont>(asset.path)
operator fun AssetManager.get(asset: FontAssets) =  getAsset<BitmapFont>(asset.path)