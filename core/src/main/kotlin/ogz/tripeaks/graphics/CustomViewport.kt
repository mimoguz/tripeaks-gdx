package ogz.tripeaks.graphics

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.utils.viewport.Viewport
import kotlin.math.max
import kotlin.math.min

class CustomViewport(
    private val minimumWorldWidth: Int,
    private val maximumWorldWidth: Int,
    private val constantWorldHeight: Int,
    camera: Camera
) : Viewport() {

    init {
        setWorldSize(minimumWorldWidth.toFloat(), constantWorldHeight.toFloat())
        setCamera(camera)
    }

    override fun update(screenWidth: Int, screenHeight: Int, centerCamera: Boolean) {
        val minRatio = min(
            screenWidth / minimumWorldWidth.toFloat(),
            screenHeight / constantWorldHeight.toFloat()
        )
        val scale = max(minRatio.toInt(), 1)
        val viewportHeight = constantWorldHeight * scale
        var width = min(screenWidth / scale, maximumWorldWidth)
        // Make sure width is even. It may pass the maximum width.
        if (width % 2 != 0) {
            width = if (width - 1 >= minimumWorldWidth) width - 1 else width + 1
        }
        val viewportWidth = width * scale
        worldWidth = width.toFloat()
        worldHeight = constantWorldHeight.toFloat()
        setScreenBounds(
            (screenWidth - viewportWidth) / 2,
            (screenHeight - viewportHeight) / 2,
            viewportWidth,
            viewportHeight
        )
        apply(centerCamera)
    }

}