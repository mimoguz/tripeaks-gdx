package ogz.tripeaks

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Sprite
import ktx.collections.GdxArray
import ogz.tripeaks.data.Rank
import ogz.tripeaks.data.Suit

class SpriteCollection(private val assets: AssetManager, useDarkTheme: Boolean) {
    val faceList = GdxArray.with(*Array(52, { Sprite() }))
    val sideFaceList = GdxArray.with(*Array(52, { Sprite() }))
    val plate = Sprite()
    val back = Sprite()
    val slicedBack = Sprite()
    init { set(useDarkTheme) }

    fun set(useDarkTheme: Boolean) {
        for (suit in Suit.values()) {
            for (rank in Rank.values()) {
                val idx = Util.getSpriteIndex(suit, rank)
                Util.setFaceSprite(assets, faceList[idx], suit, rank, useDarkTheme)
                Util.setSideFaceSprite(assets, sideFaceList[idx], suit, rank, useDarkTheme)
                Util.setPlateSprite(assets, plate, useDarkTheme)
                Util.setRegion(assets, back, Const.SPRITE_CARD_BACK)
                Util.setRegion(assets, slicedBack, Const.SPRITE_CARD_BACK_SLICE)
            }
        }
    }
}