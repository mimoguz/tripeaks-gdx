package ogz.tripeaks

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import ktx.assets.getAsset
import ktx.assets.load

enum class TextureAtlasAssets(val path: String) {
    Cards("images/cards.atlas"),
    Ui("images/ui.atlas")
}

fun AssetManager.loadSavedGame(asset: TextureAtlasAssets) = load<TextureAtlas>(asset.path)
operator fun AssetManager.get(asset: TextureAtlasAssets) = getAsset<TextureAtlas>(asset.path)

enum class FontAssets(val path: String) {
    GameFont("fonts/gamefont.fnt")
}

fun AssetManager.loadSavedGame(asset: FontAssets) = load<BitmapFont>(asset.path)
operator fun AssetManager.get(asset: FontAssets) = getAsset<BitmapFont>(asset.path)

enum class TextureAssets(val path: String) {
    LightTitle("images/light_title.png"),
    DarkTitle("images/dark_title.png"),
}

fun AssetManager.loadSavedGame(asset: TextureAssets) = load<Texture>(asset.path)
operator fun AssetManager.get(asset: TextureAssets) = getAsset<Texture>(asset.path)
