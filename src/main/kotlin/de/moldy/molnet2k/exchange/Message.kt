package de.moldy.molnet2k.exchange

import de.moldy.molnet2k.utils.serializer.ByteObjectSerializer
import io.netty.channel.Channel
import kotlin.reflect.KClass

class Message {

    internal var trafficID = ""

    internal lateinit var sender: Channel

    internal val received = HashMap<String, ByteArray>()
    internal val send = HashMap<String, ByteArray>()

    fun getSender(): Channel {
        return this.sender
    }

    fun <T : Any> getVar(name: String, type: KClass<T>): T {
        name.hashCode()
        val bytes = this.received[name]
        requireNotNull(bytes) {"var for name: $name doesn't exits"}
        return ByteObjectSerializer.byteObjectSerializer.deSerialize(type, bytes)
    }

    fun setVar(name: String, any: Any) {
        val bytes = ByteObjectSerializer.byteObjectSerializer.serialize(any)
        this.send[name] = bytes
    }

    fun send() {
        this.sender.writeAndFlush(this)
    }

    fun send(trafficID: String) {
        this.trafficID = trafficID
        this.send()
    }

}