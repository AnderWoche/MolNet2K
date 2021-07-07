package de.moldy.molnet2k.server

import de.moldy.molnet2k.MessageDecoder
import de.moldy.molnet2k.MessageHandler
import de.moldy.molnet2k.exchange.NetworkInterface
import de.moldy.molnet2k.exchange.file.FileProviderExchanger
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.*
import io.netty.channel.group.DefaultChannelGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.util.concurrent.GlobalEventExecutor
import java.nio.file.Path
import java.util.*

open class Server(private var port: Int) : NetworkInterface() {

    private val serverBootstrap = ServerBootstrap()

    private val allClients = DefaultChannelGroup(GlobalEventExecutor.INSTANCE)

    constructor(): this(0)

    init {
        val bossGroup: EventLoopGroup = NioEventLoopGroup()
        val workerGroup: EventLoopGroup = NioEventLoopGroup()
        this.serverBootstrap.group(bossGroup, workerGroup)
        this.serverBootstrap.channel(NioServerSocketChannel::class.java)
        this.serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024)
        this.serverBootstrap.childHandler(object : ChannelInitializer<NioSocketChannel>() {
            override fun initChannel(ch: NioSocketChannel) {
                val translator = ServerMessageTranslator()
                ch.pipeline().addFirst("defaultDecoder", MessageDecoder())
                ch.pipeline().addLast("decoder", ServerMessageDecoder(translator))
                ch.pipeline().addLast("encoder", ServerMessageEncoder(translator))
                ch.pipeline().addLast("handler", messageHandler)
                allClients.add(ch)
            }

            override fun channelInactive(ctx: ChannelHandlerContext) {
                allClients.remove(ctx.channel())
                super.channelInactive(ctx)
            }
        })
        this.serverBootstrap.option(ChannelOption.SO_BACKLOG, 128)
        this.serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true)

        this.loadMessageExchanger(FileProviderExchanger())
    }

    /**
     * Set the new port and Connect
     * @param port sets the new Port
     * @return returns the ChannelFuture
     */
    fun bind(port: Int): ChannelFuture {
        this.port = port
        return this.bind()
    }

    fun bind(): ChannelFuture {
        return serverBootstrap.bind(port)
    }

    fun provideFile(name: String, path: Path) {
        val exchanger = this.messageHandler.exchangerManager.getMessageExchanger(FileProviderExchanger::class)
        exchanger!!.provide(name, path)
    }

}