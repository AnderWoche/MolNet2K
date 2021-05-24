package de.moldy.molnet2k

import de.moldy.molnet2k.exchange.Message
import de.moldy.molnet2k.exchange.MessageExchangerManager
import de.moldy.molnet2k.exchange.MolNetMethodHandle
import de.moldy.molnet2k.exchange.RightIDFactory
import de.moldy.molnet2k.exchange.annotations.ClientOnly
import de.moldy.molnet2k.exchange.annotations.TrafficID
import de.moldy.molnet2k.utils.ByteBufferUtils
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

class ClientMessageHandler : SimpleChannelInboundHandler<ByteBuf>() {

    private val messageExchangerManager = MessageExchangerManager(RightIDFactory(false))

    override fun channelRead0(ctx: ChannelHandlerContext, msg: ByteBuf) {
        val intTrafficID = msg.readInt()
        val handle = this.messageExchangerManager.getMethodHandle(intTrafficID)
        if(handle != null) {

        }
    }

    @ClientOnly
    @TrafficID("messageHandler.set.method.id")
    private fun sendIntCode(message: Message) {
        val trafficID = message.getVar("trafficID", String::class)
        val intId = message.getVar("id", Int::class)
        this.messageExchangerManager.associateTrafficIDWithInt(intId, trafficID)
    }

}