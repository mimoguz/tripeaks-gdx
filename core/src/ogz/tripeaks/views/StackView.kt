package ogz.tripeaks.views

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ogz.tripeaks.*
import ogz.tripeaks.data.Card

class StackView(private val stack: java.util.Stack<Card>, private val assets: AssetManager, useDarkTheme: Boolean)
    : View {

    private var plate = Util.setPlateSprite(assets, Sprite(), useDarkTheme)
    private val back = assets[TextureAtlasAssets.Cards].createSprite(Const.SPRITE_CARD_BACK)

    override fun draw(batch: SpriteBatch) {
        for (shift in 0 until stack.size) {
            batch.draw(plate, Const.STACK_POSITION.x - shift * 5f, Const.STACK_POSITION.y)
            batch.draw(back, Const.STACK_POSITION.x - shift * 5f, Const.STACK_POSITION.y)
        }
    }

    override fun setTheme(dark: Boolean) {
        Util.setPlateSprite(assets, plate, dark)
    }
}