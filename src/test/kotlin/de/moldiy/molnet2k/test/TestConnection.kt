package de.moldiy.molnet2k.test

import de.moldy.molnet2k.client.Client
import de.moldy.molnet2k.exchange.Message
import de.moldy.molnet2k.exchange.MessageExchangerManager
import de.moldy.molnet2k.exchange.annotations.TrafficID
import de.moldy.molnet2k.server.Server

fun main() {
    val server = Server(3458)
    server.loadMessageExchanger(ServerMessageExchanger())
//    server.provideFile("test", Paths.get("C:\\Users\\david\\Desktop\\test.txt"))

    server.bind()


    val client = Client("localhost", 3458)
    client.loadMessageExchanger(object : MessageExchangerManager() {
        @TrafficID("testClient")
        fun a(message: Message) {
            println("antwoet ist da: ${message.getVar("test", String::class)}")
        }
        @TrafficID("test1")
        fun b(message: Message) {
            println("[Client] message: ${message.getVar("test", String::class)}")
            message.setVar("secondName", "secondnameYeah!").send("login")
        }
    })


    client.connect().addListener {
        if (it.isDone) {
            println("channel connected!")

//            client.requestFile("test", Paths.get("C:\\Users\\david\\Desktop\\lol123"))
//                .addListener(object : FileDownloadListener {
//                override fun newFile(file: File) {
//                    println("new file: $file")
//                }
//
//                override fun bytesReceived(currentSize: Long, totalSize: Long) {
//                }
//
//                override fun done(fileDownloadFuture: FileDownloadFuture) {
//                    println("new File: ${fileDownloadFuture.isSuccess}")
//                }
//
//            })


            for(i in 0 until 20) {
                val fistMessage = client.createMessage("login")
                fistMessage.setVar("cool", 2345)
                fistMessage.setVar("cool2", "das ist ein string")
                fistMessage.setVar("name", "firstNameyeah!")
                fistMessage.send()
            }

//            fistMessage.setVar("secondName", "secondNameYeah!").send()
//
//            val channel = client.getChannel()
//            var message = Message()
//            message.trafficID = ""
//            message.setVar("test", 234)
//            message.setVar("test2", "mein name ist cool")
//            message.setVar("cordX", 4385F)
//            channel.writeAndFlush(message)
//
//            Thread {
//                val startTime = System.currentTimeMillis()
//                for (i in 0 until 2) {
//                    message = Message()
//                    message.sender = channel
//                    message.trafficID = "test"
//                    message.setVar("test", 345)
//                    message.setVar("test2", "mein name ist cool")
//                    message.setVar("cordX", 4385F)
//                    message.send("test")
//                }
//                println("executionTime: " + (System.currentTimeMillis() - startTime))
//            }.start()

        }
    }
}