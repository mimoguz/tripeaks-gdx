package ogz.tripeaks

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Sprite
import ktx.collections.GdxArray
import ogz.tripeaks.data.Card
import ogz.tripeaks.data.Rank
import ogz.tripeaks.data.Source
import ogz.tripeaks.data.Suit

object Util {
    data class CardInfo(val suit: Suit, val rank: Rank)

    fun makeDeck(): GdxArray<CardInfo> {
        val deck = GdxArray<CardInfo>(52)
        for (suit in Suit.values()) {
            for (rank in Rank.values()) {
                deck.add(CardInfo(suit, rank))
            }
        }
        deck.shuffle()
        return deck
    }

    fun getIndex(column: Int, row: Int): Int = row * Const.COLUMN_COUNT + column

    fun getIndex(cell: Source.Cell): Int = getIndex(cell.column, cell.row)

    fun getColumn(index: Int): Int = index % Const.COLUMN_COUNT

    fun getRow(index: Int): Int = index / Const.COLUMN_COUNT

    fun getCellX(column: Int): Float = column * Const.CELL_WIDTH + 1f

    fun getCellY(row: Int): Float =
            Const.CONTENT_HEIGHT - (row + 2) * Const.CELL_HEIGHT - Const.VERTICAL_PADDING

    fun setPlateSprite(assets: AssetManager, sprite: Sprite, dark: Boolean): Sprite {
        val key =  if (dark) Const.SPRITE_PLATE_DARK else Const.SPRITE_PLATE_LIGHT
        setRegion(assets, sprite, key)
        return sprite
    }

    fun setFaceSprite(assets: AssetManager, sprite: Sprite, card: Card, dark: Boolean = false): Sprite {
        val key = (if (dark) Const.SPRITE_DARK_PREFIX else Const.SPRITE_LIGHT_PREFIX) + card.getSpriteName()
        setRegion(assets, sprite, key)
        return sprite
    }

    fun setRegion(assets: AssetManager, sprite: Sprite, key: String) {
        val region = assets[TextureAtlasAssets.Cards].findRegion(key)
        sprite.setRegion(region)
        sprite.setBounds(region.regionX.toFloat(), region.regionY.toFloat(), region.regionWidth.toFloat(), region.regionHeight.toFloat())
    }
}

