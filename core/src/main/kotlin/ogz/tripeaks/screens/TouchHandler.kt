package ogz.tripeaks.screens

import com.badlogic.gdx.InputAdapter
import ogz.tripeaks.services.MessageBox
import ogz.tripeaks.services.Message.Companion as Msg

class TouchHandler(private val messageBox: MessageBox) : InputAdapter() {

    var slient = false

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (slient) {
            return false
        }
        messageBox.send(Msg.TouchDown(screenX, screenY, pointer, button))
        return true
    }
}