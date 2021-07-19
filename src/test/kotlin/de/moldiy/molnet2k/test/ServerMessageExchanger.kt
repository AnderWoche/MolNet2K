package de.moldiy.molnet2k.test

import de.moldy.molnet2k.exchange.Message
import de.moldy.molnet2k.exchange.annotations.Rights
import de.moldy.molnet2k.exchange.annotations.TrafficID

class ServerMessageExchanger {

    @Rights(["test", "rights"])
    @TrafficID("test")
    fun test(message: Message) {
//        val name = message.getVar("name", String::class)

        message.setVar("retr", 23).send("sdf")

        message.setVar("test", "lol das ist ein string")
        message.send("testClient")

//        println("[SERVER] server has received test call from client")
    }
}