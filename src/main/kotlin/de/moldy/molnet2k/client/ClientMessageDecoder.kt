package de.moldy.molnet2k.client

import de.moldy.molnet2k.exchange.Message
import de.moldy.molnet2k.utils.ByteBufferUtils
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import io.netty.handler.codec.ReplayingDecoder

class ClientMessageDecoder(private val translator: ClientMessageTranslator) : ByteToMessageDecoder() {

    override fun decode(ctx: ChannelHandlerContext, inBytes: ByteBuf, out: MutableList<Any>) {

        val trafficIDInt = inBytes.readInt()

        if(trafficIDInt == 0) {
            val string = ByteBufferUtils.readUTF8String(inBytes)
            val id = inBytes.readInt()
            this.translator.put(string, id)
            return
        }

        val message = Message()
        message.sender = ctx.channel()
        message.trafficID = this.translator.getKey(trafficIDInt) as String

        while (inBytes.readableBytes() > 0) {
            val valueName = this.translator.getKey(inBytes.readInt()) as String
            val bytes = ByteArray(inBytes.readInt())
            inBytes.readBytes(bytes)
            message.received[valueName] = bytes
        }

        out.add(message)
    }
}