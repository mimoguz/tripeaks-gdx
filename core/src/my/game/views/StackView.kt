package my.game.views

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import my.game.Constants
import my.game.View
import my.game.TextureAtlasAssets
import my.game.data.Card
import my.game.get

class StackView(private val stack: java.util.Stack<Card>, assets: AssetManager) : View {
    private val sprite = assets[TextureAtlasAssets.Cards].createSprite(Constants.SPRITE_CARD_BACK_KEY)

    override fun draw(batch: SpriteBatch) {
        for (shift in 0 until stack.size) {
            batch.draw(sprite, Constants.STACK_POSITION.x - shift * 5f, Constants.STACK_POSITION.y)
        }
    }
}