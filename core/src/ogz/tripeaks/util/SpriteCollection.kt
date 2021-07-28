package ogz.tripeaks.util

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Sprite
import ktx.collections.GdxArray
import ogz.tripeaks.Const
import ogz.tripeaks.TextureAtlasAssets
import ogz.tripeaks.get

class SpriteCollection(private val assets: AssetManager, useDarkTheme: Boolean) {
    val faces: GdxArray<Sprite> = GdxArray.with(*Array(52) { Sprite() })
    val smallFaces: GdxArray<Sprite> = GdxArray.with(*Array(52) { Sprite() })
    val plate = Sprite()
    val back = Sprite()

    init {
        set(useDarkTheme)
    }

    fun set(useDarkTheme: Boolean) {
        for (cardIndex in 0 until 52) {
            setFaceSprite(cardIndex, useDarkTheme)
            setSideFaceSprite(cardIndex, useDarkTheme)
        }
        setRegion(back, Const.SPRITE_CARD_BACK)
        setPlateSprite(useDarkTheme)
    }

    private fun setRegion(sprite: Sprite, key: String) {
        val region = assets[TextureAtlasAssets.Cards].findRegion(key)
        sprite.setRegion(region)
        sprite.setBounds(
            region.regionX.toFloat(),
            region.regionY.toFloat(),
            region.regionWidth.toFloat(),
            region.regionHeight.toFloat()
        )
    }

    private fun setPlateSprite(useDarkTheme: Boolean) {
        val key =  if (useDarkTheme) Const.SPRITE_PLATE_DARK else Const.SPRITE_PLATE_LIGHT
        setRegion(plate, key)
    }

    private fun setFaceSprite(cardIndex: Int, useDarkTheme: Boolean) {
        val prefix = if (useDarkTheme) Const.SPRITE_DARK_PREFIX else Const.SPRITE_LIGHT_PREFIX
        val key = "${prefix}_card_${(cardIndex + 1).toString().padStart(2, '0')}"
        setRegion(faces[cardIndex], key)
    }

    private fun setSideFaceSprite(cardIndex: Int, useDarkTheme: Boolean) {
        val prefix = if (useDarkTheme) Const.SPRITE_DARK_PREFIX else Const.SPRITE_LIGHT_PREFIX
        val key = "${Const.SPRITE_SMALL_PREFIX}_${prefix}_card_${(cardIndex + 1).toString().padStart(2, '0')}"
        setRegion(smallFaces[cardIndex], key)
    }
}