package ogz.tripeaks.screens

import com.badlogic.gdx.InputAdapter
import ogz.tripeaks.services.PooledMessageBox
import ogz.tripeaks.services.TouchDown

class TouchHandler(private val messageBox: PooledMessageBox) : InputAdapter() {

    var slient = false

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (slient) {
            return false
        }
        val message = messageBox.getMessage() ?: TouchDown()
        message.set(screenX, screenY, pointer, button)
        messageBox.send(message)
        return true
    }
}