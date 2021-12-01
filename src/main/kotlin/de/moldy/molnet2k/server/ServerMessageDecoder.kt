package de.moldy.molnet2k.server

import de.moldy.molnet2k.MessageService
import de.moldy.molnet2k.utils.ByteBufferUtils
import de.moldy.molnet2k.utils.ByteBufferUtils.Companion.readLengthAndString
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

class ServerMessageDecoder(private val translator: ServerMessageTranslator, private val messageService: MessageService) : ByteToMessageDecoder() {

    override fun decode(ctx: ChannelHandlerContext, inBytes: ByteBuf, out: MutableList<Any>) {
        val isString = inBytes.readByte() == 0.toByte()
        val trafficID: String = if (isString) {
//            val trafficIDString = ByteBufferUtils.readUTF8String(inBytes)
            val trafficIDString = inBytes.readLengthAndString().toString()
            val buffer = ctx.alloc().buffer()
            this.translator.getIntFromString(trafficIDString, buffer)
            ctx.writeAndFlush(buffer)
            trafficIDString
        } else {
            val trafficIDInt = inBytes.readInt()
            this.translator.getValueFromID(trafficIDInt) as String
        }

        val message = this.messageService.getMessage(ctx.channel(), trafficID)

        while (inBytes.readableBytes() > 0) {
            val valueName = if (inBytes.readByte() == 0.toByte()) {
//                val valueNameString = ByteBufferUtils.readUTF8String(inBytes)
                val valueNameString = inBytes.readLengthAndString().toString()
                val buffer = ctx.alloc().buffer()
                this.translator.getIntFromString(valueNameString, buffer)
                ctx.writeAndFlush(buffer)
                valueNameString
            } else {
                val valueNameInt = inBytes.readInt()
                this.translator.getValueFromID(valueNameInt) as String
            }
            val bytes = inBytes.readBytes(inBytes.readInt())
            message.received[valueName] = bytes
        }

        if(message.isUsed) {
            println("isUSed! object: $message")
            message.continueProcess()
        } else {
            println("is not used!")
            out.add(message)
        }
    }

}