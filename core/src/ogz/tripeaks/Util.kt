package ogz.tripeaks

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Sprite
import ktx.collections.GdxArray
import ogz.tripeaks.data.Card
import ogz.tripeaks.data.Rank
import ogz.tripeaks.data.Source
import ogz.tripeaks.data.Suit
import java.util.*

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

    fun readDarkThemePreference() = Gdx.app.getPreferences(Const.PREFERENCES_NAME).getBoolean(Const.PREFERENCES_DARK_THEME, false)

    fun setPlateSprite(assets: AssetManager, sprite: Sprite, useDarkTheme: Boolean): Sprite {
        val key =  if (useDarkTheme) Const.SPRITE_PLATE_DARK else Const.SPRITE_PLATE_LIGHT
        setRegion(assets, sprite, key)
        return sprite
    }

    inline fun setFaceSprite(assets: AssetManager, sprite: Sprite, card: Card, useDarkTheme: Boolean = false): Sprite =
            setFaceSprite(assets, sprite, card.suit, card.rank, useDarkTheme)

    fun setFaceSprite(assets: AssetManager, sprite: Sprite, suit: Suit, rank: Rank, useDarkTheme: Boolean = false): Sprite {
        val key = (if (useDarkTheme) Const.SPRITE_DARK_PREFIX else Const.SPRITE_LIGHT_PREFIX) + getSpriteName(suit, rank)
        setRegion(assets, sprite, key)
        return sprite
    }

    fun setSideFaceSprite(assets: AssetManager, sprite: Sprite, suit: Suit, rank: Rank, useDarkTheme: Boolean = false): Sprite {
        val key = Const.SPRITE_SMALL_PREFIX +
                (if (useDarkTheme) Const.SPRITE_DARK_PREFIX else Const.SPRITE_LIGHT_PREFIX) +
                getSpriteName(suit, rank)
        setRegion(assets, sprite, key)
        return sprite
    }

    inline fun setSideFaceSprite(assets: AssetManager, sprite: Sprite, card: Card, dark: Boolean = false): Sprite =
        setSideFaceSprite(assets, sprite, card.suit, card.rank, dark)

    fun setRegion(assets: AssetManager, sprite: Sprite, key: String) {
        val region = assets[TextureAtlasAssets.Cards].findRegion(key)
        sprite.setRegion(region)
        sprite.setBounds(region.regionX.toFloat(), region.regionY.toFloat(), region.regionWidth.toFloat(), region.regionHeight.toFloat())
    }

    fun getSpriteName(suit: Suit, rank: Rank) =
            suit.toString().toLowerCase(Locale.US) + "_" + (rank.ordinal + 1).toString().padStart(2, '0')

    fun getSpriteIndex(suit: Suit, rank: Rank): Int = suit.ordinal * 13 + rank.ordinal

    inline fun getSpriteIndex(card: Card): Int = getSpriteIndex(card.suit, card.rank)

}

