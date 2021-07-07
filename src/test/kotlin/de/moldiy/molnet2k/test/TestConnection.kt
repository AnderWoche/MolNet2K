package de.moldiy.molnet2k.test

import de.moldy.molnet2k.client.Client
import de.moldy.molnet2k.exchange.Message
import de.moldy.molnet2k.exchange.MessageExchangerManager
import de.moldy.molnet2k.exchange.annotations.TrafficID
import de.moldy.molnet2k.exchange.file.FileDownloadFuture
import de.moldy.molnet2k.exchange.file.FileDownloadListener
import de.moldy.molnet2k.exchange.file.FileProviderExchanger
import de.moldy.molnet2k.exchange.file.FileProviderReaderExchanger
import de.moldy.molnet2k.server.Server
import java.io.File
import java.nio.file.Paths

fun main() {
    val server = Server(3458)
    server.loadMessageExchanger(ServerMessageExchanger())

    val fileProvider = FileProviderExchanger()
    fileProvider.provide("test", Paths.get("C:\\Users\\david\\Desktop\\test.txt"))
    server.loadMessageExchanger(fileProvider)

    server.bind()


    val client = Client("localhost", 3458)
    val fileProviderReaderExchanger = FileProviderReaderExchanger()
    client.loadMessageExchanger(fileProviderReaderExchanger)

    client.loadMessageExchanger(object : MessageExchangerManager() {
        @TrafficID("testClient")
        fun a(message: Message) {
            println("antwoet ist da")
        }
    })


    client.connect().addListener {
        if (it.isDone) {
            println("channel connected!")

            fileProviderReaderExchanger.requestFile(
                client,
                client.getChannel(),
                "test",
                Paths.get("C:\\Users\\david\\Desktop\\lol123")
            ).addListener(object : FileDownloadListener {
                override fun newFile(file: File) {
                    println("new file: $file")
                }
                override fun bytesReceived(currentSize: Long, totalSize: Long) {
                }
                override fun done(fileDownloadFuture: FileDownloadFuture) {
                    println("new File: ${fileDownloadFuture.isSuccess}")
                }

            })

            val fistMessage = client.createMessage("test")
            fistMessage.setVar("cool", 2345)
            fistMessage.setVar("cool2", "das ist ein string")
            fistMessage.send()
//
            val channel = client.getChannel()
            var message = Message()
            message.trafficID = ""
            message.setVar("test", 234)
            message.setVar("test2", "mein name ist cool")
            message.setVar("cordX", 4385F)
            channel.writeAndFlush(message)

            Thread {
                val startTime = System.currentTimeMillis()
                for (i in 0 until 2) {
                    message = Message()
                    message.sender = channel
                    message.trafficID = "test"
                    message.setVar("test", 345)
                    message.setVar("test2", "mein name ist cool")
                    message.setVar("cordX", 4385F)
                    message.send("test")
                }
                println("executionTime: " + (System.currentTimeMillis() - startTime))
            }.start()

        }
    }
}

class TestConnection {

    init {
        val server = Server(3458)
        server.bind()


        val client = Client("localhost", 3458)

        client.connect().addListener {
            if (it.isDone) {

            }
        }


    }

}