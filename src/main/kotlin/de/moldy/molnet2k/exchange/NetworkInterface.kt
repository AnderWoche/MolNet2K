package de.moldy.molnet2k.exchange

import de.moldy.molnet2k.ChannelIdentifierManager
import de.moldy.molnet2k.MessageHandler
import de.moldy.molnet2k.client.Client
import de.moldy.molnet2k.utils.BitVector
import io.netty.channel.Channel

open class NetworkInterface {

    val rightIDFactory = RightIDFactory(false)

    val channelIdentifierManager = ChannelIdentifierManager()

    val messageHandler = object : MessageHandler() {
        override fun getRightBitsFromChannel(channel: Channel): BitVector? {
            return channelIdentifierManager.getIdentifier("rights", channel)
        }
    }

    fun getChannel(identifierName: String, value: Any): Channel? {
        return channelIdentifierManager.getChannel(identifierName, value)
    }

    fun createMessage(channel: Channel, trafficID: String): Message {
        val message = Message()
        message.trafficID = trafficID
        message.sender = channel
        return message
    }

    fun createMessage(identifierName: String, value: Any, trafficID: String): Message {
        val channel = channelIdentifierManager.getChannel(identifierName, value)
        requireNotNull(channel) {"no channel has this identifier <$identifierName> <$value>"}
        return this.createMessage(channel, trafficID)
    }

    fun loadMessageExchanger(any: Any) {
        if(this is Client) {
            this.messageHandler.exchangerManager.loadClientMessageExchanger(any)
        } else {
            this.messageHandler.exchangerManager.loadServerMessageExchanger(any)
        }
    }
}