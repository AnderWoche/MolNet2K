package de.moldy.molnet2k

import de.moldy.molnet2k.exchange.Message
import de.moldy.molnet2k.exchange.MessageExchangerManager
import de.moldy.molnet2k.exchange.MolNetMethodHandle
import de.moldy.molnet2k.exchange.file.FilePacket
import de.moldy.molnet2k.utils.BitVector
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

abstract class MessageHandler : SimpleChannelInboundHandler<Message>() {

    internal val exchangerManager = MessageExchangerManager()

    val noAccessListener =
        ArrayList<(ctx: ChannelHandlerContext, handle: MolNetMethodHandle<*>, channelBits: BitVector?, msg: Message) -> Unit>()

    override fun channelRead0(ctx: ChannelHandlerContext, msg: Message) {
        val handle = this.exchangerManager.getMethodHandle(msg.trafficID)

        if (handle == null) {
            println("[MESSAGE HANDLER] the <${msg.trafficID}> TrafficID has not a method")
            return
        }

        if (handle.isRightRestricted()) {
            val rightBits = this.getRightBitsFromChannel(ctx.channel())
            if (handle.hasAccess(rightBits)) {
                handle.invokeIgnoreRights(msg)
            } else {
                this.noAccessListener.forEach {
                    it(ctx, handle, rightBits, msg)
                }
            }
        } else {
            handle.invokeIgnoreRights(msg)
        }
    }

    abstract fun getRightBitsFromChannel(channel: Channel): BitVector?

    override fun channelActive(ctx: ChannelHandlerContext) {
        super.channelActive(ctx)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
        cause?.printStackTrace()
        super.exceptionCaught(ctx, cause)
    }


}