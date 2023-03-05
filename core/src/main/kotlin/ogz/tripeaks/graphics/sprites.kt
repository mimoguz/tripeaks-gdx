package ogz.tripeaks.graphics

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import ogz.tripeaks.assets.TextureAssets
import ogz.tripeaks.assets.TextureAtlasAssets
import ogz.tripeaks.assets.get
import ogz.tripeaks.screens.Constants

class SpriteSet(val isDark: Boolean, backIndex: Int, assets: AssetManager) {
    val back: TextureRegion
    val background: Color
    val buttonDisabled: NinePatch
    val buttonDown: NinePatch
    val buttonUp: NinePatch
    val card: TextureRegion
    val dealIcon: TextureRegion
    val empty: TextureRegion
    val face: IndexedSprite
    val home: TextureRegion
    val menuIcon: TextureRegion
    val smallFace: IndexedSprite
    val undoIcon: TextureRegion

    init {
        val prefix = if (isDark) "dark" else "light"
        val cards = assets[TextureAtlasAssets.Cards]
        back = cards.findRegion("card_back", backIndex)
        background = if (isDark) Constants.DARK_BG else Constants.LIGHT_BG
        buttonDisabled = cards.createPatch("${prefix}_buttonDisabled")
        buttonDown = cards.createPatch("${prefix}_buttonDown")
        buttonUp =  cards.createPatch("${prefix}_buttonUp")
        card = cards.findRegion("${prefix}_card")
        dealIcon = cards.findRegion("${prefix}_deal")
        empty = cards.findRegion("${prefix}_empty")
        face = IndexedSprite("${prefix}_face", assets)
        home = Sprite(assets[if (isDark) TextureAssets.DarkTitle else TextureAssets.LightTitle])
        menuIcon = cards.findRegion("${prefix}_menu")
        smallFace = IndexedSprite("${prefix}_small_face", assets)
        undoIcon = cards.findRegion("${prefix}_undo")
    }
}

class IndexedSprite(name: String, assets: AssetManager) {
    private val sprites = (0 until 52).map { index ->
        assets[TextureAtlasAssets.Cards].findRegion(name, index)
    }

    operator fun get(index: Int): TextureRegion = sprites[index]
}

sealed interface Icon {
    fun get(sprites: SpriteSet): TextureRegion

    object Deal : Icon {
        override fun get(sprites: SpriteSet): TextureRegion = sprites.dealIcon
    }

    object Menu : Icon {
        override fun get(sprites: SpriteSet): TextureRegion = sprites.menuIcon
    }

    object Undo : Icon {
        override fun get(sprites: SpriteSet): TextureRegion = sprites.undoIcon
    }
}
