package ogz.tripeaks

import ktx.app.KtxGame
import ktx.app.KtxScreen

class Main : KtxGame<KtxScreen>() {
    override fun create() {
        // Gdx.graphics.isContinuousRendering = false
        addScreen(LoadingScreen(this))
        setScreen<LoadingScreen>()
    }
}

