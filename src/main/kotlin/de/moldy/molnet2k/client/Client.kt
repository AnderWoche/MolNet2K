package de.moldy.molnet2k.client

import de.moldy.molnet2k.MessageDecoder
import de.moldy.molnet2k.exchange.Message
import de.moldy.molnet2k.exchange.NetworkInterface
import de.moldy.molnet2k.exchange.file.FileDownloadProcessor
import de.moldy.molnet2k.exchange.file.FileProviderReaderExchanger
import io.netty.bootstrap.Bootstrap
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import java.nio.file.Path

open class Client(private var host: String, private var port: Int) : NetworkInterface() {

    private val bootstrap = Bootstrap()

    private lateinit var channel: Channel

    constructor(): this("", 0)

    init {
        this.bootstrap.group(NioEventLoopGroup())
        this.bootstrap.channel(NioSocketChannel::class.java)
        this.bootstrap.option(ChannelOption.SO_KEEPALIVE, true)
        this.bootstrap.option(ChannelOption.TCP_NODELAY, true)
        this.bootstrap.handler(object : ChannelInitializer<SocketChannel>() {
            override fun initChannel(ch: SocketChannel) {
                val translator = ClientMessageTranslator()
                ch.pipeline().addFirst("defaultDecoder", MessageDecoder())
                ch.pipeline().addLast("decoder", ClientMessageDecoder(translator))
                ch.pipeline().addLast("encoder", ClientMessageEncoder(translator))
                ch.pipeline().addLast("handler", messageHandler)
            }
        })
        this.loadMessageExchanger(FileProviderReaderExchanger())
    }

    fun connect(host: String, port: Int): ChannelFuture {
        this.host = host
        this.port = port
        return this.connect()
    }

    fun connect(): ChannelFuture {
        val channelFuture = bootstrap.connect(host, port)
        this.channel = channelFuture.channel()
        return channelFuture
    }

    fun createMessage(trafficID: String): Message {
        return super.createMessage(this.channel, trafficID)
    }

    fun readFile(name: String, path: Path): FileDownloadProcessor {
        val exchanger =
            super.messageHandler.exchangerManager.getMessageExchanger(FileProviderReaderExchanger::class)
        return exchanger!!.requestFile(this, this.channel, name, path)
    }

    fun getChannel(): Channel {
        return this.channel
    }

}