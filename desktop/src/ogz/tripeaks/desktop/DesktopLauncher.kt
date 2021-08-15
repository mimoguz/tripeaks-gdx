package ogz.tripeaks.desktop

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import ogz.tripeaks.Const
import ogz.tripeaks.Game

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        val config = Lwjgl3ApplicationConfiguration().apply {
            setTitle("TriPeaks")
            setWindowIcon(
                "images/icon16.png",
                "images/icon24.png",
                "images/icon32.png",
                "images/icon48.png",
            )
            setWindowedMode(Const.CONTENT_WIDTH.toInt() * 7, Const.CONTENT_HEIGHT.toInt() * 7)
        }
        Lwjgl3Application(Game(), config)
    }
}