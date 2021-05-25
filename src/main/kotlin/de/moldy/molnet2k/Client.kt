package de.moldy.molnet2k

import de.moldy.molnet2k.exchange.NetworkInterface
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel


class Client(private var host: String, private var port: Int) : NetworkInterface() {

    private val messageHandler = ClientMessageHandler()

    private val bootstrap = Bootstrap()

    private lateinit var channel: Channel

    init {
        this.bootstrap.group(NioEventLoopGroup())
        this.bootstrap.channel(NioSocketChannel::class.java)
        this.bootstrap.option(ChannelOption.SO_KEEPALIVE, true)
        this.bootstrap.option(ChannelOption.TCP_NODELAY, true)
        bootstrap.handler(object : ChannelInitializer<SocketChannel>() {
            override fun initChannel(ch: SocketChannel) {
                ch.pipeline().addFirst("decoder", MessageDecoder())
                ch.pipeline().addFirst("encoder", MessageEncoder())
                ch.pipeline().addLast("handler", messageHandler)
            }
        })

        getChannel().writeAndFlush()
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

    fun getChannel(): Channel {
        return this.channel
    }

}