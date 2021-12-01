package de.moldy.molnet2k

import de.moldy.molnet2k.exchange.Message
import io.netty.channel.Channel

interface MessageService {
    fun getMessage(channel: Channel, trafficID: String): Message

    fun releaseMessage(channel: Channel, trafficID: String)
    fun releaseMessage(message: Message)

    fun containsMessage(message: Message): Boolean
}