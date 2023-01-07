@file:JvmName("Lwjgl3Launcher")

package ogz.tripeaks.lwjgl3

import com.badlogic.gdx.Files
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import ogz.tripeaks.Main
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

/** Launches the desktop (LWJGL3) application. */
fun main() {
    Lwjgl3Application(Main(), Lwjgl3ApplicationConfiguration().apply {
        setTitle("TriPeaks")
        setWindowedMode(640, 480)
        setWindowIcon(*(arrayOf(128, 64, 32, 16).map { "libgdx$it.png" }.toTypedArray()))
        // LibGDX dumps preferences files directly under user home.
        // Let's try to get the proper config directory for OS.
        getConfigDirectory()?.let {
            setPreferencesConfig(it.toAbsolutePath().toString(), Files.FileType.Absolute)
        }
    })
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