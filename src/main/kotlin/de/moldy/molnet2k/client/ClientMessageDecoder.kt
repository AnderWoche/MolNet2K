package de.moldy.molnet2k.client

import de.moldy.molnet2k.MessageService
import de.moldy.molnet2k.utils.ByteBufferUtils
import de.moldy.molnet2k.utils.ByteBufferUtils.Companion.readLengthAndString
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

class ClientMessageDecoder(private val translator: ClientMessageTranslator, private val messageService: MessageService) : ByteToMessageDecoder() {

    override fun decode(ctx: ChannelHandlerContext, inBytes: ByteBuf, out: MutableList<Any>) {

        val trafficIDInt = inBytes.readInt()

        if(trafficIDInt == 0) {
            val string = inBytes.readLengthAndString().toString()
//            val string = ByteBufferUtils.readUTF8String(inBytes)
            val id = inBytes.readInt()
            this.translator.put(string, id)
            return
        }

        val sender = ctx.channel()
        val trafficID = this.translator.getKey(trafficIDInt) as String

        val message = this.messageService.getMessage(sender, trafficID)

        while (inBytes.readableBytes() > 0) {
            val valueName = this.translator.getKey(inBytes.readInt()) as String
            val bytes = inBytes.readBytes(inBytes.readInt())
            message.received[valueName] = bytes
        }

        if(message.isUsed) {
            message.continueProcess()
        } else {
            out.add(message)
        }
    }
}