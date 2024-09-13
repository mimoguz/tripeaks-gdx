@file:JvmName("Lwjgl3Launcher")

package ogz.tripeaks.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import ogz.tripeaks.Constants
import ogz.tripeaks.Main

/** Launches the desktop (LWJGL3) application. */
fun main() {
    // This handles macOS support and helps on Windows.
    if (StartupHelper.startNewJvmIfRequired()) return
    Lwjgl3Application(Main(false), Lwjgl3ApplicationConfiguration().apply {
        setTitle("TriPeaks")
        setWindowedMode(Constants.MIN_WORLD_WIDTH.toInt() * 3, Constants.WORLD_HEIGHT.toInt() * 3)
        setWindowIcon(*(arrayOf(256, 128, 64, 32, 16).map { "tripeaks-gdx$it.png" }.toTypedArray()))
    })
}
