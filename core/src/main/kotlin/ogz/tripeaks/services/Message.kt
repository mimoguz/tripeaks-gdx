package ogz.tripeaks.services

sealed interface Message {

    companion object {

        class TouchDown(val screenX: Int, val screenY: Int, val pointer: Int, val button: Int) :
            Message

        class TouchUp(val screenX: Int, val screenY: Int, val pointer: Int, val button: Int) :
            Message

        data object AnimationSetChanged : Message
        data object SpriteSetChanged : Message
        data object ShowAllChanged : Message
        data object SkinChanged : Message
        data object Won : Message
        data object Stalled: Message

    }

}