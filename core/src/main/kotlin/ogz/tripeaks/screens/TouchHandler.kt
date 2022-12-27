package ogz.tripeaks.screens

import com.badlogic.gdx.InputAdapter
import ogz.tripeaks.services.MessageBox
import ogz.tripeaks.services.Messages

class TouchHandler(private val messageBox: MessageBox) : InputAdapter() {

    var slient = false

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (slient) {
            return false
        }
        messageBox.send(Messages.TouchDown(screenX, screenY, pointer, button))
        return true
    }
}