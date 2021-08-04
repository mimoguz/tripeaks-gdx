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
    val joker = Sprite()

    init {
        set(useDarkTheme)
    }

    fun set(useDarkTheme: Boolean) {
        for (cardIndex in 0 until 52) {
            setFaceSprite(cardIndex, useDarkTheme)
            setSideFaceSprite(cardIndex, useDarkTheme)
        }
        setRegion(back, "card_back")
        setCardSprite(useDarkTheme)
        setJokerSprite(useDarkTheme)
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
    val key =  "${select(useDarkTheme)}_card"
        setRegion(card, key)
    }

    private fun setFaceSprite(cardIndex: Int, useDarkTheme: Boolean) {
        val key = "${select(useDarkTheme)}_card_$cardIndex"
        setRegion(faces[cardIndex], key)
    }

    private fun setSideFaceSprite(cardIndex: Int, useDarkTheme: Boolean) {
        val key = "small_${select(useDarkTheme)}_card_$cardIndex"
        setRegion(smallFaces[cardIndex], key)
    }

    private  fun setJokerSprite(useDarkTheme: Boolean) {
        val key = "${select(useDarkTheme)}_joker"
        setRegion(joker, key)
    }

    private fun select(useDarkTheme: Boolean) = if (useDarkTheme) "dark" else "light"
}