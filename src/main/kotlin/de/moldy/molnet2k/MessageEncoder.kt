package de.moldy.molnet2k

import de.moldy.molnet2k.exchange.Message
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class MessageEncoder : MessageToByteEncoder<Message>() {

    override fun encode(ctx: ChannelHandlerContext, msg: Message, out: ByteBuf) {
        val buffer = msg.toBytes(ctx.alloc().buffer())
        out.writeInt(buffer.readableBytes())
        out.writeBytes(buffer)
    }

}