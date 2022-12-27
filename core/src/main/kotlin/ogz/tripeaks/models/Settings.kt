package ogz.tripeaks.models

import ogz.tripeaks.services.Message
import ogz.tripeaks.services.Receiver
import ogz.tripeaks.services.Message.Companion as Msg

class Settings(var v: Int) : Receiver<Msg.SettingsQuery> {
    constructor() : this(0)

    fun newGame() : GameState = GameState.startNew(IntArray(52) { it }, this)

    override fun receive(message: Message.Companion.SettingsQuery): Any? = Msg.Settings(v)

    // TODO: Handle a message to update settings. Receiving method should re-bind the animation and sprite sets, change the default skin.
}