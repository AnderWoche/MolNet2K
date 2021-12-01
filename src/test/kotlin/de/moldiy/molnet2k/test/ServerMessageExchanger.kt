package de.moldiy.molnet2k.test

import de.moldy.molnet2k.exchange.Message
import de.moldy.molnet2k.exchange.annotations.Rights
import de.moldy.molnet2k.exchange.annotations.TrafficID

class ServerMessageExchanger {

//    @Rights(["test", "rights"])
    @TrafficID("login")
    fun test(message: Message) {
        val name = message.getVar("name", String::class)

        println("server: $name")

        message.setVar("retr", 23).send("sdf")
        message.setVar("test", "lol das ist ein string")
        message.send("test1")

        val name2 = message.getVar("secondName", String::class)

        println("server: $name2")

        message.setVar("test", "lol noch mal einfach")
        message.send("testClient")
    }
}