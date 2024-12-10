package ogz.tripeaks.views

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import ogz.tripeaks.graphics.SpriteSet
import ogz.tripeaks.models.Card
import ogz.tripeaks.Constants.CARD_HEIGHT
import ogz.tripeaks.Constants.CARD_WIDTH
import ogz.tripeaks.Constants.FACE_X
import ogz.tripeaks.Constants.FACE_Y
import ogz.tripeaks.Constants.SMALL_10_HEIGHT
import ogz.tripeaks.Constants.SMALL_FACE_HEIGHT
import ogz.tripeaks.Constants.SMALL_FACE_WIDTH

sealed interface CardDrawingStrategy {

    fun drawBack(batch: SpriteBatch, card: Card, sprites: SpriteSet, position: Vector2)
    fun drawFront(batch: SpriteBatch, card: Card, sprites: SpriteSet, position: Vector2)
    fun drawStacked(batch: SpriteBatch, card: Card, sprites: SpriteSet, position: Vector2)

    companion object Strategies {

        data object BackVisible : CardDrawingStrategy {

            override fun drawBack(
                batch: SpriteBatch,
                card: Card,
                sprites: SpriteSet,
                position: Vector2
            ) {
                batch.draw(sprites.card, position.x, position.y)
                batch.draw(sprites.back, position.x, position.y)
                batch.draw(
                    sprites.smallFace[card],
                    position.x + 1f,
                    position.y + 3f
                )
                val h = if (card % 13 == 9) SMALL_10_HEIGHT else SMALL_FACE_HEIGHT
                batch.draw(
                    sprites.smallFace[card],
                    position.x + CARD_WIDTH - SMALL_FACE_WIDTH - 1f,
                    position.y + CARD_HEIGHT - h - 1f
                )
            }

            override fun drawFront(
                batch: SpriteBatch,
                card: Card,
                sprites: SpriteSet,
                position: Vector2
            ) {
                batch.draw(sprites.card, position.x, position.y)
                batch.draw(sprites.face[card], position.x + FACE_X, position.y + FACE_Y)
            }

            override fun drawStacked(
                batch: SpriteBatch,
                card: Card,
                sprites: SpriteSet,
                position: Vector2
            ) {
                batch.draw(sprites.card, position.x, position.y)
                batch.draw(sprites.back, position.x, position.y)
                val h = if (card % 13 == 9) SMALL_10_HEIGHT else SMALL_FACE_HEIGHT
                batch.draw(
                    sprites.smallFace[card],
                    position.x + CARD_WIDTH - SMALL_FACE_WIDTH - 1f,
                    position.y + CARD_HEIGHT - h - 1f
                )
            }

        }

        data object BackHidden : CardDrawingStrategy {

            override fun drawBack(
                batch: SpriteBatch,
                card: Card,
                sprites: SpriteSet,
                position: Vector2
            ) {
                batch.draw(sprites.card, position.x, position.y)
                batch.draw(sprites.back, position.x, position.y)
            }

            override fun drawFront(
                batch: SpriteBatch,
                card: Card,
                sprites: SpriteSet,
                position: Vector2
            ) {
                batch.draw(sprites.card, position.x, position.y)
                batch.draw(sprites.face[card], position.x + FACE_X, position.y + FACE_Y)
            }

            override fun drawStacked(
                batch: SpriteBatch,
                card: Card,
                sprites: SpriteSet,
                position: Vector2
            ) {
                batch.draw(sprites.card, position.x, position.y)
                batch.draw(sprites.back, position.x, position.y)
            }

        }

    }

}

