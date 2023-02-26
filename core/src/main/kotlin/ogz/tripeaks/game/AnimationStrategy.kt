package ogz.tripeaks.game

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import ogz.tripeaks.screens.Constants.DISSOLVE_TIME

sealed interface AnimationStrategy {
    val shaderProgram: ShaderProgram?
    var param: Float
    fun cardRemoved(time: Float, vertexColor: Color, position: Vector2, scale: Vector2): Boolean
    fun screenTransition(time: Float, vertexColor: Color): Boolean
    fun setTheme(dark: Boolean)

    companion object Strategies  {
        class Dissolve : AnimationStrategy {
            override var param = 0f
            override val shaderProgram = ShaderProgram(
                javaClass.classLoader.getResource("shaders/basic.vert")?.readText(),
                javaClass.classLoader.getResource("shaders/dissolve.frag")?.readText()
            )

            override fun cardRemoved(
                time: Float,
                vertexColor: Color,
                position: Vector2,
                scale: Vector2
            ): Boolean {
                if (time > DISSOLVE_TIME) {
                    return true
                }
                val normalizedTime = time / DISSOLVE_TIME
                position.y = normalizedTime * 400f
                scale.set(
                    1f - normalizedTime * 0.25f,
                    1f + normalizedTime * 6f
                )
                vertexColor.set(0.8f, 1f - normalizedTime, param, 1f)
                return false
            }

            override fun screenTransition(time: Float, vertexColor: Color): Boolean {
                if (time > DISSOLVE_TIME) {
                    return true
                }
                vertexColor.set(0.2f, 1f - time / DISSOLVE_TIME, param, 1f)
                return false
            }

            override fun setTheme(dark: Boolean) {
                param = if (dark) 1f else 0f
            }
        }

        class Blink : AnimationStrategy {
            override var param = 0f

            override val shaderProgram = ShaderProgram(
                javaClass.classLoader.getResource("shaders/basic.vert")?.readText(),
                javaClass.classLoader.getResource("shaders/blink.frag")?.readText()
            )

            override fun cardRemoved(
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

            private fun step(time: Float, vertexColor: Color): Boolean {
                if (time > DISSOLVE_TIME) {
                    return true
                }
                val normalizedTime = time / DISSOLVE_TIME
                vertexColor.set(1f, 1f - normalizedTime, param, 1f)
                return false
            }
        }
    }
}
