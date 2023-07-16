package ogz.tripeaks.services

sealed interface Message {
    companion object {
        class TouchDown(val screenX: Int, val screenY: Int, val pointer: Int, val button: Int) :
            Message

        class TouchUp(val screenX: Int, val screenY: Int, val pointer: Int, val button: Int) :
            Message

        object AnimationSetChanged : Message
        object SpriteSetChanged : Message
        object ShowAllChanged : Message
        object SkinChanged : Message
    }
}