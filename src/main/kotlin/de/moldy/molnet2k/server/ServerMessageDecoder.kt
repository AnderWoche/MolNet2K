package de.moldy.molnet2k.server

import de.moldy.molnet2k.exchange.Message
import de.moldy.molnet2k.utils.ByteBufferUtils
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import io.netty.handler.codec.ReplayingDecoder

class ServerMessageDecoder(private val translator: ServerMessageTranslator) : ByteToMessageDecoder() {

    override fun decode(ctx: ChannelHandlerContext, inBytes: ByteBuf, out: MutableList<Any>) {

        val message = Message()
        message.sender = ctx.channel()

        val isString = inBytes.readByte() == 0.toByte()
        val trafficID: String = if (isString) {
            val trafficIDString = ByteBufferUtils.readUTF8String(inBytes)
            val buffer = ctx.alloc().buffer()
            this.translator.getIntFromString(trafficIDString, buffer)
            ctx.writeAndFlush(buffer)
            trafficIDString
        } else {
            val trafficIDInt = inBytes.readInt()
            this.translator.getValueFromID(trafficIDInt) as String
        }

        while (inBytes.readableBytes() > 0) {
            val valueName = if (inBytes.readByte() == 0.toByte()) {
                val valueNameString = ByteBufferUtils.readUTF8String(inBytes)
                val buffer = ctx.alloc().buffer()
                this.translator.getIntFromString(valueNameString, buffer)
                ctx.writeAndFlush(buffer)
                valueNameString
            } else {
                val valueNameInt = inBytes.readInt()
                this.translator.getValueFromID(valueNameInt) as String
            }
            val bytes = ByteArray(inBytes.readInt())
            inBytes.readBytes(bytes)
            message.received[valueName] = bytes
        }
        message.trafficID = trafficID

        out.add(message)
    }

}