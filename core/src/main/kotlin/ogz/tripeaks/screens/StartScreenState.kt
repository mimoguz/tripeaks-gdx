package ogz.tripeaks.screens

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.app.clearScreen
import ogz.tripeaks.graphics.BlurredRenderer
import ogz.tripeaks.graphics.Renderer
import ogz.tripeaks.graphics.SimpleRenderer
import ogz.tripeaks.services.SettingsService

interface StartScreenState {

    fun update()
    fun render(deltaTime: Float)

}

class StartScreenSwitch : StartScreenState, Disposable {

    private var current: StartScreenState = NoopStartScreenState()
    private val states: MutableMap<String, StartScreenState> = mutableMapOf()

    fun <T>addState(cls: Class<T>, state: T) where T: StartScreenState{
        states[cls.simpleName] = state
    }

    fun <T> switch(cls: Class<T>) {
        current = states[cls.simpleName] ?: NoopStartScreenState()
    }

    override fun update() {
        for (st in states.values) {
            st.update()
        }
    }

    override fun render(deltaTime: Float) {
        current.render(deltaTime)
    }

    override fun dispose() {
        for (st in states.values) {
            if (st is Disposable) st.dispose()
        }
    }

}

class NoopStartScreenState : StartScreenState {

    override fun update() {
        TODO("Not yet implemented")
    }

    override fun render(deltaTime: Float) {
        clearScreen(0f, 0f, 0f, 1f)
    }

}

abstract class StartScreenStateBase(
    private val batch: SpriteBatch,
    private val viewport: Viewport,
    private val settings: SettingsService,
    private val renderer: Renderer
) : StartScreenState, Disposable {

    override fun update() {
        renderer.resize(viewport.worldWidth.toInt(), viewport.worldHeight.toInt())
    }

    override fun render(deltaTime: Float) {
        val currentSettings = settings.get()
        val titleTexture = currentSettings.spriteSet.title
        val x = MathUtils.floor(titleTexture.width * -0.5f).toFloat()
        val y = MathUtils.floor(titleTexture.height * -0.5f).toFloat()
        renderer.draw(batch, viewport, currentSettings.spriteSet.background) { batch ->
            batch.draw(
                titleTexture,
                x,
                y,
                titleTexture.width.toFloat(),
                titleTexture.height.toFloat()
            )
        }
    }

    override fun dispose() {
        renderer.dispose()
    }
}

class PausedStartScreen(
    batch: SpriteBatch,
    viewport: Viewport,
    settings: SettingsService,
    assets: AssetManager,
): StartScreenStateBase(batch, viewport, settings, BlurredRenderer(assets))


class PlayingStartScreen(
    batch: SpriteBatch,
    viewport: Viewport,
    settings: SettingsService,
): StartScreenStateBase(batch, viewport, settings, SimpleRenderer())