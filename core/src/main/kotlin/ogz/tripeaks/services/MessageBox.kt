package ogz.tripeaks.services

import com.badlogic.gdx.utils.Disposable
import kotlin.reflect.KClass

interface Message

fun interface Receiver<M> {
    fun receive(message: M)
}

class MessageBox : Disposable {
    val receivers: MutableMap<KClass<out Message>, MutableSet<Receiver<Message>>> = hashMapOf()

    @Suppress("UNCHECKED_CAST")
    inline fun <reified M : Message> register(receiver: Receiver<M>) {
        val cls = M::class
        val rs = receivers.getOrPut(cls) { mutableSetOf() }
        rs.add(receiver as Receiver<Message>)
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified M : Message> unregister(receiver: Receiver<M>) {
        receivers[M::class]?.remove(receiver as Receiver<Message>)
    }

    inline fun <reified M : Message> send(message: M) {
        receivers[M::class]?.forEach { it.receive(message) }
    }

    override fun dispose() {
        receivers.clear()
    }
}
