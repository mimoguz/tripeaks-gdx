package ogz.tripeaks.android

import android.content.res.Configuration
import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import ogz.tripeaks.Main

/** Launches the Android application. */
class AndroidLauncher : AndroidApplication() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val modeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val darkMode = modeFlags == Configuration.UI_MODE_NIGHT_YES
        initialize(Main(darkMode), AndroidApplicationConfiguration().apply {
            // Configure your application here.
            useImmersiveMode = true // Recommended, but not required.
        })
    }
}
