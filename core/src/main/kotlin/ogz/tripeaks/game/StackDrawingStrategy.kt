package ogz.tripeaks.game

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import ogz.tripeaks.graphics.SpriteSet
import ogz.tripeaks.models.Card
import ogz.tripeaks.screens.Constants.CARD_HEIGHT
import ogz.tripeaks.screens.Constants.CARD_WIDTH
import ogz.tripeaks.screens.Constants.FACE_X
import ogz.tripeaks.screens.Constants.FACE_Y
import ogz.tripeaks.screens.Constants.SMALL_10_HEIGHT
import ogz.tripeaks.screens.Constants.SMALL_FACE_HEIGHT
import ogz.tripeaks.screens.Constants.SMALL_FACE_H_PADDING
import ogz.tripeaks.screens.Constants.SMALL_FACE_V_PADDING
import ogz.tripeaks.screens.Constants.SMALL_FACE_WIDTH

sealed interface StackDrawingStrategy {
    fun drawBack(batch: SpriteBatch, card: Card, sprites: SpriteSet, position: Vector2)

    companion object Strategies {
        class BackVisible : ogz.tripeaks.game.StackDrawingStrategy {
            override fun drawBack(
                batch: SpriteBatch,
                card: Card,
                sprites: SpriteSet,
                position: Vector2
            ) {
                batch.draw(sprites.card, position.x, position.y)
                batch.draw(
                    sprites.smallFace[card],
                    position.x + CARD_WIDTH - SMALL_FACE_WIDTH - SMALL_FACE_H_PADDING,
                    position.y + SMALL_FACE_V_PADDING
                )
            }
        }

        class BackHidden : ogz.tripeaks.game.StackDrawingStrategy {
            override fun drawBack(
                batch: SpriteBatch,
                card: Card,
                sprites: SpriteSet,
                position: Vector2
            ) {
                batch.draw(sprites.card, position.x, position.y)
                batch.draw(sprites.face[card], position.x + FACE_X, position.y + FACE_Y)

                val spriteHeight = if (card % 13 == 9) SMALL_10_HEIGHT else SMALL_FACE_HEIGHT
                batch.draw(
                    sprites.smallFace[card],
                    position.x + CARD_WIDTH - SMALL_FACE_WIDTH - SMALL_FACE_H_PADDING,
                    position.y + CARD_HEIGHT - spriteHeight - SMALL_FACE_V_PADDING
                )
            }
        }
    }
}