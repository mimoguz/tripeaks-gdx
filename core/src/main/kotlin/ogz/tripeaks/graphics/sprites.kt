package ogz.tripeaks.graphics

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import ogz.tripeaks.Constants
import ogz.tripeaks.assets.TextureAssets
import ogz.tripeaks.assets.TextureAtlasAssets
import ogz.tripeaks.assets.get
import ogz.tripeaks.models.ThemeMode

class SpriteSet(val theme: ThemeMode, darkSystem: Boolean, backIndex: Int, assets: AssetManager) {

    val back: TextureRegion
    val background: Color
    val buttonDisabled: NinePatch
    val buttonDown: NinePatch
    val buttonUp: NinePatch
    val card: TextureRegion
    val dealIcon: TextureRegion
    val empty: TextureRegion
    val face: IndexedSprite
    val menuIcon: TextureRegion
    val smallFace: IndexedSprite
    val undoIcon: TextureRegion
    val title: Texture

    init {
        val prefix = when (theme) {
            ThemeMode.Dark -> "dark"
            ThemeMode.Light -> "light"
            ThemeMode.Black -> "black"
            ThemeMode.System -> if (darkSystem) "dark" else "light"
        }
        val isDark =
            theme == ThemeMode.Dark || theme == ThemeMode.Black || (theme == ThemeMode.System && darkSystem)
        val cards = assets[TextureAtlasAssets.Images]
        back = cards.findRegion("card_back", backIndex)
        background = when (theme) {
            ThemeMode.Dark -> Constants.DARK_BG
            ThemeMode.Light -> Constants.LIGHT_BG
            ThemeMode.Black -> Constants.BLACK_BG
            ThemeMode.System -> if (darkSystem) Constants.DARK_BG else Constants.LIGHT_BG
        }
        buttonDisabled = cards.createPatch("${prefix}_button_disabled")
        buttonDown = cards.createPatch("${prefix}_button_down")
        buttonUp = cards.createPatch("${prefix}_button_up")
        card = cards.findRegion("${prefix}_card")
        dealIcon = cards.findRegion("${prefix}_icon_deal")
        empty = cards.findRegion("${prefix}_empty")
        face = IndexedSprite("${prefix}_face", assets)
        menuIcon = cards.findRegion("${prefix}_icon_menu")
        smallFace = IndexedSprite("${prefix}_small_face", assets)
        undoIcon = cards.findRegion("${prefix}_icon_undo")
        title = if (isDark) assets[TextureAssets.DarkTitle] else assets[TextureAssets.LightTitle]
    }

}

class IndexedSprite(name: String, assets: AssetManager) {

    private val sprites = (0 until 52).map { index ->
        assets[TextureAtlasAssets.Images].findRegion(name, index)
    }

    operator fun get(index: Int): TextureRegion = sprites[index]

}

sealed interface Icon {

    fun get(sprites: SpriteSet): TextureRegion

    data object Deal : Icon {

        override fun get(sprites: SpriteSet): TextureRegion = sprites.dealIcon
    }

    data object Menu : Icon {

        override fun get(sprites: SpriteSet): TextureRegion = sprites.menuIcon
    }

    data object Undo : Icon {

        override fun get(sprites: SpriteSet): TextureRegion = sprites.undoIcon
    }

}
