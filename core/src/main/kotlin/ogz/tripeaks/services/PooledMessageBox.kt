package ogz.tripeaks.services

import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.Pool.Poolable
import kotlin.reflect.KClass

class PooledMessageBox : Disposable {

    val pools: MutableMap<KClass<out Message>, Pool<Message>> = hashMapOf()
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

    @Suppress("UNCHECKED_CAST")
    inline fun <reified M : Message> addPool(pool: Pool<M>): Boolean {
        val cls = M::class
        if (pools.containsKey(cls)) {
            return false
        }
        pools[cls] = pool as Pool<Message>
        return true
    }

    inline fun <reified M : Message> getMessage(): M? = pools[M::class]?.let { it as? M }

    inline fun <reified M : Message> returnMessage(message: Message) {
        pools[M::class]?.free(message)
    }

    override fun dispose() {
        receivers.clear()
    }
}

interface Message : Poolable

interface Receiver<M> {
    fun receive(message: M)
}

class TouchDown(var screenX: Int, var screenY: Int, var pointer: Int, var button: Int) : Message {

    constructor() : this(Int.MIN_VALUE, Int.MIN_VALUE, Int.MIN_VALUE, Int.MIN_VALUE)

    override fun reset() {
        screenX = Int.MIN_VALUE
        screenY = Int.MIN_VALUE
        pointer = Int.MIN_VALUE
        button = Int.MIN_VALUE
    }

    fun set( x: Int, y: Int, pointer: Int, button: Int) {
        this.screenX = x
        this.screenY = y
        this.pointer = pointer
        this.button = button
    }
}