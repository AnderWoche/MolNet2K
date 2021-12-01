package de.moldy.molnet2k

import com.google.common.collect.HashBasedTable
import de.moldy.molnet2k.exchange.Message
import io.netty.channel.Channel

class PooledMessageService : MessageService {

    private val messages = HashBasedTable.create<Channel, String, Message>()

    override fun getMessage(channel: Channel, trafficID: String): Message {
        var message = this.messages[channel, trafficID]
        if(message == null) {
            message = Message(this)
            message.sender = channel
            message.trafficID = trafficID
            message.isUsed = false
            this.messages.put(channel, trafficID, message)
        } else {
            message.isUsed = true
        }
        return message
    }

    override fun releaseMessage(channel: Channel, trafficID: String) {
        this.messages.remove(channel, trafficID)
    }

    override fun releaseMessage(message: Message) {
        this.messages.remove(message.sender, message.trafficID)
    }

    override fun containsMessage(message: Message): Boolean {
        return this.messages.containsValue(message)
    }
}