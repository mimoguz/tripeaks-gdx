package ogz.tripeaks.android

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import ogz.tripeaks.Main

/** Launches the Android application. */
class AndroidLauncher : AndroidApplication() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val layout = GridLayout(this).also {
//            it.columnCount = 1
//            it.rowCount = 1
//        }
//        val view = initializeForView(Main(), AndroidApplicationConfiguration().apply {
//            useImmersiveMode = false
//        }).also {
//            it.layoutParams = GridLayout.LayoutParams().also { it.setGravity(10) }
//        }
//        layout.addView(view)
//        setContentView(layout)
        initialize(Main(), AndroidApplicationConfiguration().apply {
            // Configure your application here.
            useImmersiveMode = true // Recommended, but not required.
        })
    }
}
