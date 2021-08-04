package ogz.tripeaks.util

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Sprite
import ktx.collections.GdxArray
import ogz.tripeaks.TextureAtlasAssets
import ogz.tripeaks.get

class SpriteCollection(private val assets: AssetManager, useDarkTheme: Boolean) {
    val faces: GdxArray<Sprite> = GdxArray.with(*Array(52) { Sprite() })
    val smallFaces: GdxArray<Sprite> = GdxArray.with(*Array(52) { Sprite() })
    val card = Sprite()
    val back = Sprite()

    init {
        set(useDarkTheme)
    }

    fun set(useDarkTheme: Boolean) {
        for (cardIndex in 0 until 52) {
            setFaceSprite(cardIndex, useDarkTheme)
            setSideFaceSprite(cardIndex, useDarkTheme)
        }
        setRegion(back, BACK)
        setCardSprite(useDarkTheme)
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

    private fun setCardSprite(useDarkTheme: Boolean) {
    val key =  "${if (useDarkTheme) DARK else LIGHT}_$CARD"
        setRegion(card, key)
    }

    private fun setFaceSprite(cardIndex: Int, useDarkTheme: Boolean) {
        val prefix = if (useDarkTheme) DARK else LIGHT
        val key = "${prefix}_${CARD}_$cardIndex"
        setRegion(faces[cardIndex], key)
    }

    private fun setSideFaceSprite(cardIndex: Int, useDarkTheme: Boolean) {
        val prefix = if (useDarkTheme) DARK else LIGHT
        val key = "${SMALL}_${prefix}_${CARD}_$cardIndex"
        setRegion(smallFaces[cardIndex], key)
    }

    companion object {
        const val BACK = "card_back"
        const val DARK = "dark"
        const val LIGHT = "light"
        const val CARD = "card"
        const val SMALL = "small"
    }
}