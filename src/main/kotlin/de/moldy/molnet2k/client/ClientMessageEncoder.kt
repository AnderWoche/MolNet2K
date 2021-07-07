package de.moldy.molnet2k.client

import de.moldy.molnet2k.exchange.Message
import de.moldy.molnet2k.utils.ByteBufferUtils
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class ClientMessageEncoder(private val translator: ClientMessageTranslator) : MessageToByteEncoder<Message>() {

    override fun encode(ctx: ChannelHandlerContext, msg: Message, out: ByteBuf) {
        val buffer = ctx.alloc().buffer()

        val trafficID: Int? = this.translator.getValue(msg.trafficID)
        if(trafficID != null) {
            buffer.writeByte(1)
            buffer.writeInt(trafficID)
        } else {
            buffer.writeByte(0)
            ByteBufferUtils.writeUTF8String(buffer, msg.trafficID)
        }

        msg.send.forEach { (valueName, valueByteArray) ->
            val varName: Int? = this.translator.getValue(valueName)
            if(varName != null) {
                buffer.writeByte(1)
                buffer.writeInt(varName)
            } else {
                buffer.writeByte(0)
                ByteBufferUtils.writeUTF8String(buffer, valueName)
            }
            buffer.writeInt(valueByteArray.size)
            buffer.writeBytes(valueByteArray)
        }
        msg.send.clear()

        out.writeInt(buffer.readableBytes())
        out.writeBytes(buffer)
        buffer.release()
    }
}