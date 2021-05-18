package de.moldy.molnet2k

import io.netty.channel.Channel
import java.util.*

class ChannelIdentifier {

    val valueIdentifier: MutableMap<Channel, Any> = Collections.synchronizedMap(HashMap())
    val identifierValue: MutableMap<Any, Channel> = Collections.synchronizedMap(HashMap())

    fun addChannel(ctx: Channel, value: Any) {
        if (!this.valueIdentifier.containsKey(ctx)) {
            this.valueIdentifier[ctx] = value
            this.identifierValue[value] = ctx
        }
    }

    fun removeChannel(ctx: Channel) {
        val value = this.valueIdentifier.remove(ctx)
        this.identifierValue.remove(value)
    }

    fun getChannel(value: Any): Channel? {
        return this.identifierValue[value]
    }

    fun <T> getIdentifier(ctx: Channel): T? {
        return this.valueIdentifier[ctx] as T?
    }
}