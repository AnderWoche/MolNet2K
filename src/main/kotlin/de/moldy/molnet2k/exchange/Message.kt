package de.moldy.molnet2k.exchange

import de.moldy.molnet2k.MessageService
import de.moldy.molnet2k.utils.serializer.ByteObjectSerializer
import io.netty.buffer.ByteBuf
import io.netty.channel.Channel
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.reflect.KClass

class Message(private val messageService: MessageService) {

    internal var trafficID = ""

    internal lateinit var sender: Channel

    internal val received = HashMap<String, ByteBuf>()
    internal val send = HashMap<String, ByteBuf>()

    val lock = ReentrantLock()
    val condition = lock.newCondition()

    var isUsed = false

    fun <T : Any> getVar(name: String, type: KClass<T>): T {
        var bytes = this.received[name]
        if(bytes == null) {
            this.lock.lock()
            println("wait! object: $this")
            this.condition.await()
            println("continue!")
            this.lock.unlock()
            bytes = this.received[name]
        }
        requireNotNull(bytes) {"var for name: <$name> doesn't exits"}
        return ByteObjectSerializer.byteObjectSerializer.deSerialize(type, bytes)
    }

    fun setVar(name: String, any: Any): Message {
        val bytes = ByteObjectSerializer.byteObjectSerializer.serialize(any)
        this.send[name] = bytes
        return this
    }

    fun send() {
        this.sender.writeAndFlush(this)
    }

    fun send(trafficID: String) {
        this.trafficID = trafficID
        this.send()
    }

    fun release() {
        this.messageService.releaseMessage(this)
    }

    fun continueProcess() {
        this.lock.lock()
        this.condition.signalAll()
        this.lock.unlock()
    }
}