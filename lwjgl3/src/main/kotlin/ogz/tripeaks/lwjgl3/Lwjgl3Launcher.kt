@file:JvmName("Lwjgl3Launcher")

package ogz.tripeaks.lwjgl3

import com.badlogic.gdx.Files
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import ogz.tripeaks.Constants
import ogz.tripeaks.Main
import java.nio.file.Paths
import java.util.Locale

/** Launches the desktop (LWJGL3) application. */
fun main(args: Array<String>) {
    // This handles macOS support and helps on Windows.
    if (StartupHelper.startNewJvmIfRequired()) {
        return
    }
    Lwjgl3Application(Main(false), Lwjgl3ApplicationConfiguration().apply {
        setTitle("TriPeaks")
        setWindowedMode(Constants.MIN_WORLD_WIDTH.toInt() * 3, Constants.WORLD_HEIGHT.toInt() * 3)
        setWindowIcon(*(arrayOf(256, 128, 64, 32, 16).map { "tripeaksgdx$it.png" }.toTypedArray()))
        if (args.contains("--fullscreen")) {
            setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode())
        }
        this.setPreferencesConfig(getConfigDirectory("ogz.tripeaks"), Files.FileType.Absolute)
    })
}

fun getConfigDirectory(appName: String): String {
    val osName = System.getProperty("os.name").lowercase(Locale.US)
    val userConfigPath = when {
        osName.startsWith("windows") ->
            System.getenv("LOCALAPPDATA")
                ?: System.getenv("APPDATA")
                ?: Paths.get(System.getProperty("user.home"), ".preferences").toString()

        osName.startsWith("mac os") ->
            Paths.get(System.getProperty("user.home"), "Library", "Preferences").toString()

        else ->
            System.getenv("XDG_CONFIG_HOME")
                ?: Paths.get(System.getProperty("user.home"), ".config").toString()
    }
    return Paths.get(userConfigPath, appName).toString()
}

