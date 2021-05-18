package de.moldy.molnet2k

import io.netty.channel.Channel
import java.util.*

class ChannelIdentifierManager {

    val identifier: MutableMap<Any, ChannelIdentifier> = Collections.synchronizedMap(HashMap())

    fun <T : Any> getChannel(identifierName: String, value: T): Channel? {
        return getChannelIdentifier(identifierName).getChannel(value)
    }

    fun <T : Any> getIdentifier(identifierName: String, ctx: Channel): T? {
        val channelIdentifier = this.getChannelIdentifier(identifierName)
        return channelIdentifier.getIdentifier(ctx) as T?
    }

    fun <T : Any> setIdentifier(ctx: Channel, identifierName: String, value: T) {
        val channelIdentifier = this.getChannelIdentifier(identifierName)
        channelIdentifier.addChannel(ctx, value)
    }

    fun removeIdentifier(identifierName: String) {
        this.identifier.remove(identifierName)
    }

    private fun getChannelIdentifier(identifierName: String): ChannelIdentifier {
        return identifier[identifierName] ?: this.registerChannelIdentifier(identifierName)
    }

    private fun registerChannelIdentifier(identifierName: String): ChannelIdentifier {
        val channelIdentifier = ChannelIdentifier()
        identifier[identifierName] = channelIdentifier
        return channelIdentifier
    }

}