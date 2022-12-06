package ogz.tripeaks.graphics

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import ogz.tripeaks.assets.TextureAssets
import ogz.tripeaks.assets.TextureAtlasAssets
import ogz.tripeaks.assets.get

class SpriteSet(
    val isDark: Boolean,
    backIndex: Int,
    assets: AssetManager
) {
    val card: TextureRegion
    val back: TextureRegion
    val face: IndexedSprite
    val smallFace: IndexedSprite
    val home: TextureRegion
    val background: Color

    init {
        val prefix = if (isDark) "dark" else "light"
        face = IndexedSprite("${prefix}_card", assets)
        smallFace = IndexedSprite("small_${prefix}_card", assets)
        card = assets[TextureAtlasAssets.Cards].findRegion("${prefix}_card")
        back = assets[TextureAtlasAssets.Cards].findRegion("card_back_$backIndex")
        home = Sprite(if (isDark) assets[TextureAssets.DarkTitle] else assets[TextureAssets.LightTitle])
        background =
            if (isDark) Color.valueOf("232433ff")
            else Color.valueOf("63a347ff")
    }
}

class IndexedSprite(private val prefix: String, private val assets: AssetManager) {
    operator fun get(index: Int): TextureRegion = assets[TextureAtlasAssets.Cards].findRegion("${prefix}_$index")
}

sealed interface SpriteType {
    fun get(spriteSet: SpriteSet): TextureRegion
}

class FaceSprite(private val index: Int) : SpriteType {
    override fun get(spriteSet: SpriteSet): TextureRegion = spriteSet.face[index]
}

class SmallFaceSprite(private val index: Int) : SpriteType {
    override fun get(spriteSet: SpriteSet): TextureRegion = spriteSet.smallFace[index]
}

object CardSprite : SpriteType {
    override fun get(spriteSet: SpriteSet): TextureRegion = spriteSet.card
}

object BackSprite : SpriteType {
    override fun get(spriteSet: SpriteSet): TextureRegion = spriteSet.back
}

object HomeSprite : SpriteType {
    override fun get(spriteSet: SpriteSet): TextureRegion = spriteSet.home
}