package de.moldy.molnet2k

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ReplayingDecoder

class MessageDecoder : ReplayingDecoder<ByteBuf>() {

    override fun decode(ctx: ChannelHandlerContext, inBytes: ByteBuf, out: MutableList<Any>) {
        val nextMessageSize = inBytes.readInt()
        out.add(inBytes.readBytes(nextMessageSize))
    }

}