package ogz.tripeaks

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.freetype.registerFreeTypeFontLoaders
import ogz.tripeaks.screens.LoadingScreen
import ogz.tripeaks.screens.MainMenuScreen

open class Game : KtxGame<KtxScreen>() {
    val batch by lazy { SpriteBatch() }
    val font by lazy { BitmapFont() }
    val assets = AssetManager()

    override fun create() {
        addScreen(LoadingScreen(this))
        setScreen<LoadingScreen>()
        super.create()
    }

    override fun dispose() {
        batch.dispose()
        font.dispose()
        super.dispose()
    }
}