package de.moldy.molnet2k.exchange.file

import de.moldy.molnet2k.exchange.Message
import java.util.HashMap
import kotlin.Throws
import java.io.IOException
import java.lang.NullPointerException
import de.moldy.molnet2k.exchange.annotations.TrafficID
import io.netty.buffer.ByteBuf
import java.io.FileInputStream
import java.nio.file.Path

class FileProviderExchanger {

    private val providedFiles = HashMap<String, FilePacket>()

    fun provide(name: String, path: Path) {
        providedFiles[name] = FilePacket(path)
    }

    //    @Threaded
    @TrafficID(id = FileExchangerConstants.ACTIVE_FILE_PACKET_PROVIDER_REQUEST)
    private fun net_fileRequest(message: Message) {
        val name = message.getVar("fileName", String::class)
        val filePacket = providedFiles[name]
        if (filePacket == null) {
            message.send(FileExchangerConstants.ACTIVE_FILE_PACKET_DO_NOT_EXISTS)
            return
        }

        message.setVar("filesToDownload", filePacket.getFiles().size)
        message.setVar("bytesToDownload", filePacket.totalTransferSize)
        message.send(FileExchangerConstants.ACTIVE_FILE_PACKET_NEW_PACKET)

        val filesToSend = filePacket.getFiles()
        val filePathsToSend = filePacket.getRelativeFilePath()
        for (i in filesToSend.indices.reversed()) {
            val file = filesToSend[i]
            val relativePath = filePathsToSend[i]
            val fileSize = file.length()

            message.setVar("fileLocation", relativePath)
            message.send(FileExchangerConstants.ACTIVE_FILE_PACKET_NEW_FILE)
//            fileMessageByteBuf.writeLong(fileSize)

            val fileInputStream = FileInputStream(file)
            val read = ByteArray(FileExchangerConstants.SAVED_FILE_BYTES_IN_RAM)
            var readSize: Int
            while (fileInputStream.read(read).also { readSize = it } != -1) {

                while (!message.sender.isWritable) {
                    Thread.yield()
                }
                message.setVar("readableBytes", readSize)
                message.setVar("byteArray", read)
                message.send(FileExchangerConstants.ACTIVE_FILE_PACKET_WRITE)
            }
        }
        message.send(FileExchangerConstants.ACTIVE_FILE_PACKET_DONE)
    }
}