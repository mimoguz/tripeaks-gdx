package my.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import ktx.app.KtxScreen
import ktx.graphics.use
import my.game.*
import my.game.logic.*

class GameScreen(val game: Game) : KtxScreen {
    private val camera = OrthographicCamera()
    private val viewport = IntegerScalingViewport(Constants.CONTENT_WIDTH.toInt(), Constants.CONTENT_HEIGHT.toInt(), camera)
    private val touchPoint3D = Vector3()
    private val touchPoint2D = Vector2()
    private val state = GameState(game.assets)

    override fun render(delta: Float) {
        state.update(delta)
        camera.update()

        Gdx.gl.glClearColor(0.39f, 0.64f, 0.28f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.batch.enableBlending()
        game.batch.use(camera) { batch ->
            state.draw(batch)
        }
        game.batch.disableBlending()

        if (Gdx.input.justTouched()) {
            touchPoint3D.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            viewport.unproject(touchPoint3D)
            touchPoint2D.set(touchPoint3D.x, touchPoint3D.y)
            state.touch(touchPoint2D)
        }
    }

    override fun show() {
        state.init()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }
}
