package de.moldy.molnet2k.exchange

import de.moldy.molnet2k.ChannelIdentifierManager
import de.moldy.molnet2k.MessageHandler

open class NetworkInterface(private val messageHandler: MessageHandler) {

    val rightIDFactory = RightIDFactory(false)

    val channelIdentifierManager = ChannelIdentifierManager()

    constructor() {

    }

}