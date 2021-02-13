package my.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import ktx.app.KtxScreen
import ktx.graphics.use
import ktx.scene2d.Scene2DSkin
import my.game.Constants
import my.game.Game
import my.game.GameState

class GameScreen(val game: Game) : KtxScreen {
    private val camera = OrthographicCamera()
    private val viewport = IntegerScalingViewport(Constants.CONTENT_WIDTH.toInt(), Constants.CONTENT_HEIGHT.toInt(), camera)
    private val touchPoint3D = Vector3()
    private val touchPoint2D = Vector2()
    private val state = GameState(game.assets)
    private val stage = Stage(viewport)

    override fun render(delta: Float) {
        camera.update()
        state.update(delta)
        stage.act(delta)

        Gdx.gl.glClearColor(0.39f, 0.64f, 0.28f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.draw()

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
        stage.actors.add(TextButton("Deal", Scene2DSkin.defaultSkin).apply {
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    state.deal()
                    super.clicked(event, x, y)
                }
            })
            setSize(Constants.SPRITE_WIDTH, Constants.SPRITE_HEIGHT)
            setPosition(Constants.STACK_POSITION.x - Constants.CELL_WIDTH * 2f, Constants.STACK_POSITION.y)
        })

        stage.actors.add(TextButton("Undo", Scene2DSkin.defaultSkin).apply {
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    state.undo()
                    super.clicked(event, x, y)
                }
            })
            setSize(Constants.SPRITE_WIDTH, Constants.SPRITE_HEIGHT)
            setPosition(Constants.DISCARD_POSITION.x - Constants.CELL_WIDTH * 2f, Constants.DISCARD_POSITION.y)
        })

        Gdx.input.inputProcessor = stage
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
        stage.viewport.update(width, height)
    }
}
