package de.moldy.molnet2k.exchange

import de.moldy.molnet2k.utils.ByteBufferUtils
import de.moldy.molnet2k.utils.serializer.ByteObjectSerializer
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import java.lang.RuntimeException
import kotlin.reflect.KClass

class Message(private val messageExchangerManager: MessageExchangerManager) {

    internal var trafficID = ""

    internal lateinit var ctx: ChannelHandlerContext

    internal val received = HashMap<String, ByteArray>()
    internal val send = HashMap<String, ByteArray>()

    fun getSender(): ChannelHandlerContext {
        return this.ctx
    }

    fun <T : Any> getVar(name: String, type: KClass<T>): T {
        val bytes = this.received[name]
        requireNotNull(bytes) {"var for name: $name doesn't exits"}
        return ByteObjectSerializer.byteObjectSerializer.deSerialize(type, bytes)
    }

    fun setVar(name: String, any: Any) {
        val bytes = ByteObjectSerializer.byteObjectSerializer.serialize(any)
        this.send[name] = bytes
    }

    fun send() {
        val byteBuf = this.ctx.alloc().buffer()

        val intId = this.messageExchangerManager.getIntIdFromTrafficId(this.trafficID) ?: throw RuntimeException("traffic id int not found")
        byteBuf.writeInt(intId)

        this.send.forEach { (valueName, valueByteArray) ->
            ByteBufferUtils.writeUTF8String(byteBuf, valueName)
            byteBuf.writeInt(valueByteArray.size)
            byteBuf.writeBytes(valueByteArray)
        }
        this.ctx.channel().writeAndFlush(byteBuf)
    }

    internal fun receive(byteBuf: ByteBuf) {
        while(byteBuf.readableBytes() > 0) {
            val valueName = ByteBufferUtils.readUTF8String(byteBuf)
            val bytes = ByteArray(byteBuf.readInt())
            byteBuf.readBytes(bytes)
            this.received[valueName] = bytes
        }
    }
}