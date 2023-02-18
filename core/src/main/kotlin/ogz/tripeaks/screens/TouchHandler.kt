package ogz.tripeaks.screens

import com.badlogic.gdx.InputAdapter
import com.ray3k.stripe.PopTable
import ogz.tripeaks.services.MessageBox
import ogz.tripeaks.services.Message.Companion as Msg

class TouchHandler(private val messageBox: MessageBox) : InputAdapter() {
    var silent = false
    // For the touch events outside of the viewport:
    var dialog: PopTable? = null

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (silent) {
            dialog?.hide()
            return false
        }
        messageBox.send(Msg.TouchDown(screenX, screenY, pointer, button))
        return true
    }
}