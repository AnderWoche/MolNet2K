package de.moldy.molnet2k

import de.moldy.molnet2k.exchange.Message
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

class MessageToMessageDecoder : ChannelInboundHandlerAdapter() {

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        msg as Message

        for(lol in msg.received) {

        }

        super.channelRead(ctx, msg)
    }

}