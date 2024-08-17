package ogz.tripeaks.android

import android.os.Bundle

import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import ogz.tripeaks.Main

/** Launches the Android application. */
class AndroidLauncher : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // val layout = RelativeLayout(this)
        // layout.addView(initializeForView(Main(), AndroidApplicationConfiguration().apply {
        //     useImmersiveMode = false
        // }))
        // setContentView(layout)
        initialize(Main(), AndroidApplicationConfiguration().apply {
            // Configure your application here.
            useImmersiveMode = true // Recommended, but not required.
        })
    }
}
