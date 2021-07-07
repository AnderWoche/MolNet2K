package de.moldy.molnet2k.server

import de.moldy.molnet2k.exchange.Message
import de.moldy.molnet2k.utils.ByteBufferUtils
import de.moldy.molnet2k.utils.IDFactory
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class ServerMessageEncoder(private val translator: ServerMessageTranslator) : MessageToByteEncoder<Message>() {

    override fun encode(ctx: ChannelHandlerContext, msg: Message, out: ByteBuf) {
        val buffer = ctx.alloc().buffer()

        val trafficIDInt = this.translator.getIntFromString(msg.trafficID, out)
        buffer.writeInt(trafficIDInt)

        msg.send.forEach { (valueName, valueByteArray) ->
            buffer.writeInt(this.translator.getIntFromString(valueName, out)) // valueName to Int
            buffer.writeInt(valueByteArray.size)
            buffer.writeBytes(valueByteArray)
        }
        msg.send.clear()

        out.writeInt(buffer.readableBytes())
        out.writeBytes(buffer)
        buffer.release()
    }

}