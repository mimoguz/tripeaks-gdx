package ogz.tripeaks.desktop

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import ogz.tripeaks.Game

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        val config = Lwjgl3ApplicationConfiguration()
        config.setTitle("TriPeaks")
        config.setWindowIcon(
            "images/icon16.png",
            "images/icon24.png",
            "images/icon32.png",
            "images/icon48.png",
        )
        Lwjgl3Application(Game(), config)
    }
}