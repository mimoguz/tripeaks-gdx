package ogz.tripeaks.lwjgl3

import com.badlogic.gdx.Files
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import ogz.tripeaks.Main

/** Launches the desktop (LWJGL3) application.  */
object Lwjgl3Launcher {
    @JvmStatic
    fun main(args: Array<String>) {
        if (StartupHelper.startNewJvmIfRequired()) return  // This handles macOS support and helps on Windows.
        createApplication()
    }

    private fun createApplication(): Lwjgl3Application {
        return Lwjgl3Application(Main(), defaultConfiguration)
    }

    //// Limits FPS to the refresh rate of the currently active monitor.
    //// If you remove the above line and set Vsync to false, you can get unlimited FPS, which can be
    //// useful for testing performance, but can also be very stressful to some hardware.
    //// You may also need to configure GPU drivers to fully disable Vsync; this can cause screen tearing.
    private val defaultConfiguration: Lwjgl3ApplicationConfiguration
        private get() {
            val configuration = Lwjgl3ApplicationConfiguration()
            configuration.setTitle("TriPeaks")
            configuration.useVsync(true)
            //// Limits FPS to the refresh rate of the currently active monitor.
            configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate)
            //// If you remove the above line and set Vsync to false, you can get unlimited FPS, which can be
            //// useful for testing performance, but can also be very stressful to some hardware.
            //// You may also need to configure GPU drivers to fully disable Vsync; this can cause screen tearing.
            configuration.setWindowedMode(1024, 768)
            configuration.setWindowIcon(
                "libgdx128.png",
                "libgdx64.png",
                "libgdx32.png",
                "libgdx16.png"
            )
            getConfigDirectory()?.let { dir ->
                configuration.setPreferencesConfig(dir.toAbsolutePath().toString(), Files.FileType.Absolute)
            }
            return configuration
        }
}


fun getConfigDirectory(): Path? {
    val os = System.getProperty("os.name").uppercase(Locale.US)

    val parent = when {
        os.startsWith("WINDOWS") -> Paths.get(System.getenv("AppData"))
        os.startsWith("MAC") ->  getUserHome()?.let { Paths.get(it, "Library", "Preferences") }
        else -> {
            val configHome = System.getenv("XDG_CONFIG_HOME")
            if (configHome != null && configHome.isNotBlank()) Paths.get(configHome)
            else getUserHome()?.let { Paths.get(it, ".config") }
        }
    }

    if (parent != null) {
        val path = Paths.get(parent.toAbsolutePath().toString(), "TriPeaks-GDX")
        val dir = path.toFile()
        if (!dir.exists()) {
            try {
                dir.mkdirs()
            } catch (e: Exception) {
                return null
            }
        }
        return if (dir.isDirectory) path else null
    }

    return null
}

fun getUserHome(): String? {
    val home = System.getProperty("user.home")
    return if (home != null && home.isNotBlank()) home else null
}