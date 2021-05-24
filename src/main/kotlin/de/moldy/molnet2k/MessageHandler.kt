package de.moldy.molnet2k

import de.moldy.molnet2k.exchange.Message
import de.moldy.molnet2k.exchange.MessageExchangerManager
import de.moldy.molnet2k.exchange.RightIDFactory
import de.moldy.molnet2k.utils.ByteBufferUtils
import de.moldy.molnet2k.utils.IDFactory
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

@ChannelHandler.Sharable
class MessageHandler : SimpleChannelInboundHandler<ByteBuf>() {

    private val messageExchangerManager = MessageExchangerManager(RightIDFactory(false))

    private val stringToIntID = IDFactory<String>(false)

    init {
        val setIDString = "messageHandler.set.method.id"
        val id = this.stringToIntID.getOrCreateID(setIDString)
        this.messageExchangerManager.associateTrafficIDWithInt(id, setIDString)
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: ByteBuf) {
        val intTrafficID = msg.readInt()
        val handle = this.messageExchangerManager.getMethodHandle(intTrafficID)
        if (handle != null) {

        }

        val trafficIDString = ByteBufferUtils.readUTF8String(msg)
        this.messageExchangerManager.getMethodHandle(trafficIDString)
        this.messageExchangerManager.associateTrafficIDWithInt(
            this.stringToIntID.getOrCreateID(trafficIDString), trafficIDString
        )
    }

    override fun channelActive(ctx: ChannelHandlerContext) {
        for(entry in this.messageExchangerManager.intIdToStringIdMap) {
            val message = this.obtainMessage(ctx, "messageHandler.set.method.id")
            message.setVar("trafficID", entry.value)
            message.setVar("id", entry.key)
        }
        super.channelActive(ctx)
    }

    fun obtainMessage(ctx: ChannelHandlerContext, trafficID: String): Message {
        val message = Message(this.messageExchangerManager)
        message.ctx = ctx
        message.trafficID = trafficID
        return message
    }

    fun sendMessage(message: Message) {

    }


}