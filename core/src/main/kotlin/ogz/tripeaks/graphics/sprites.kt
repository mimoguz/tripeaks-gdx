package ogz.tripeaks.graphics

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import ogz.tripeaks.assets.TextureAssets
import ogz.tripeaks.assets.TextureAtlasAssets
import ogz.tripeaks.assets.get
import ogz.tripeaks.screens.Constants

class SpriteSet(val isDark: Boolean, backIndex: Int, assets: AssetManager) {
    val card: TextureRegion
    val back: TextureRegion
    val empty: TextureRegion
    val buttonUp: TextureRegion
    val buttonDown: TextureRegion
    val buttonDisabled: TextureRegion
    val face: IndexedSprite
    val smallFace: IndexedSprite
    val home: TextureRegion
    val background: Color

    init {
        val prefix = if (isDark) "dark" else "light"
        val cards = assets[TextureAtlasAssets.Cards]
        back = cards.findRegion("card_back_$backIndex")
        card = cards.findRegion("${prefix}_card")
        empty = cards.findRegion("${prefix}_empty")
        buttonUp = cards.findRegion("${prefix}_buttonUp")
        buttonDown = cards.findRegion("${prefix}_buttonDown")
        buttonDisabled = cards.findRegion("${prefix}_buttonDisabled")
        face = IndexedSprite("${prefix}_face", assets)
        smallFace = IndexedSprite("${prefix}_small_face", assets)
        home = Sprite(assets[if (isDark) TextureAssets.DarkTitle else TextureAssets.LightTitle])
        background = if (isDark) Constants.DARK_BG else Constants.LIGHT_BG
    }
}

class IndexedSprite(name: String, assets: AssetManager) {
    private val sprites = (0 until 52).map { index ->
        assets[TextureAtlasAssets.Cards].findRegion("${name}_$index")
    }

    operator fun get(index: Int): TextureRegion = sprites[index]
}
