package my.game

import com.badlogic.gdx.math.Interpolation
import kotlin.math.min

class Tween(
        private val interpolation: Interpolation,
        private val duration: Float,
        private val action: ((progress: Float) -> Unit)? = null
) {
    private var elapsed = 0f
    private var current = 0f
    private var active = false

    val value: Float get() = current

    fun reset() {
        elapsed = 0f
        current = 0f
        active = false
    }

    fun start() {
        active = true
    }

    fun stop() {
        active = false
    }

    fun update(delta: Float) {
        if (active) {
            elapsed += delta
            val progress = min(1f, elapsed / duration)
            current = interpolation.apply(progress)
            action?.invoke(progress)
            active = progress < 1f
        }
    }
}