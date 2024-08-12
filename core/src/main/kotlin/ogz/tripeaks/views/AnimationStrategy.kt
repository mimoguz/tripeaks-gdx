package ogz.tripeaks.views

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable
import ogz.tripeaks.Constants
import ogz.tripeaks.Constants.DISSOLVE_TIME
import ogz.tripeaks.Constants.WORLD_HEIGHT
import ogz.tripeaks.assets.ShaderSourceAssets
import ogz.tripeaks.assets.get

sealed interface AnimationStrategy {

    val shaderProgram: ShaderProgram?

    var param: Float

    fun cardRemoved(
        deltaTime: Float,
        time: Float,
        vertexColor: Color,
        position: Vector2,
        scale: Vector2
    ): Boolean

    fun screenTransition(time: Float, vertexColor: Color): Boolean

    fun setTheme(dark: Boolean)

    fun resize(wordWidth: Float, worldHeight: Float)

    companion object Strategies {

        class Dissolve(assets: AssetManager) : AnimationStrategy, Disposable {

            override var param = Constants.MIN_WORLD_WIDTH / Constants.WORLD_HEIGHT
            override val shaderProgram: ShaderProgram

            init {
                val isDesktop = Gdx.app.type == Application.ApplicationType.Desktop
                val fragAsset =
                    if (isDesktop) ShaderSourceAssets.Dissolve
                    else ShaderSourceAssets.DissolveMobile
                val vert = assets[ShaderSourceAssets.Vert].string
                val frag = assets[fragAsset].string
                shaderProgram = ShaderProgram(vert, frag)
            }

            override fun cardRemoved(
                deltaTime: Float,
                time: Float,
                vertexColor: Color,
                position: Vector2,
                scale: Vector2
            ): Boolean {
                val normalizedTime = (time / DISSOLVE_TIME).coerceAtMost(1f)
                vertexColor.set(param, 1f - normalizedTime, 1f, 1f)
                return time > DISSOLVE_TIME
            }

            override fun screenTransition(time: Float, vertexColor: Color): Boolean {
                vertexColor.set(param, 1f - time / DISSOLVE_TIME, 1f, 1f)
                return time > DISSOLVE_TIME
            }

            override fun setTheme(dark: Boolean) {
                // Pass
            }

            override fun resize(wordWidth: Float, worldHeight: Float) {
                param = wordWidth / worldHeight
            }

            override fun dispose() {
                shaderProgram.dispose()
            }

        }

        class Blink(assets: AssetManager) : AnimationStrategy, Disposable {

            override var param = 0f

            override val shaderProgram: ShaderProgram

            init {
                val vert = assets[ShaderSourceAssets.Vert].string
                val frag = assets[ShaderSourceAssets.Blink].string
                shaderProgram = ShaderProgram(vert, frag)
            }

            override fun cardRemoved(
                deltaTime: Float,
                time: Float,
                vertexColor: Color,
                position: Vector2,
                scale: Vector2
            ): Boolean = step(time, vertexColor)

            override fun screenTransition(time: Float, vertexColor: Color): Boolean =
                step(time, vertexColor)

            override fun setTheme(dark: Boolean) {
                param = if (dark) 1f else 0f
            }

            override fun resize(wordWidth: Float, worldHeight: Float) {
                // Pass
            }

            override fun dispose() {
                shaderProgram.dispose()
            }

            private fun step(time: Float, vertexColor: Color): Boolean {
                val normalizedTime = (time / DISSOLVE_TIME).coerceAtMost(1f)
                vertexColor.set(1f, 1f - normalizedTime, param, 1f)
                return time > DISSOLVE_TIME
            }

        }

        class FadeOut(assets: AssetManager) : AnimationStrategy, Disposable {

            // Unused
            override var param = 0f

            override val shaderProgram: ShaderProgram

            init {
                val vert = assets[ShaderSourceAssets.Vert].string
                val frag = assets[ShaderSourceAssets.FadeOut].string
                shaderProgram = ShaderProgram(vert, frag)
            }

            override fun cardRemoved(
                deltaTime: Float,
                time: Float,
                vertexColor: Color,
                position: Vector2,
                scale: Vector2
            ): Boolean {
                position.y -= 0.5f * deltaTime * WORLD_HEIGHT
                return step(time, vertexColor)
            }

            override fun screenTransition(time: Float, vertexColor: Color): Boolean =
                step(time, vertexColor)

            override fun setTheme(dark: Boolean) {
                // Pass
            }

            override fun resize(wordWidth: Float, worldHeight: Float) {
                // Pass
            }

            override fun dispose() {
                shaderProgram.dispose()
            }

            private fun step(time: Float, vertexColor: Color): Boolean {
                val normalizedTime = (time / DISSOLVE_TIME).coerceAtMost(1f)
                vertexColor.set(1f, 1f - normalizedTime, 0f, 1f)
                return time > DISSOLVE_TIME
            }

        }

    }

}
