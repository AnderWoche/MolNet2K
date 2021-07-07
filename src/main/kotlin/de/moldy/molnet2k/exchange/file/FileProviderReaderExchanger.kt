package de.moldy.molnet2k.exchange.file

import de.moldy.molnet2k.exchange.Message
import de.moldy.molnet2k.exchange.NetworkInterface
import de.moldy.molnet2k.exchange.annotations.TrafficID
import de.moldy.molnet2k.utils.ByteBufferUtils
import io.netty.buffer.ByteBuf
import io.netty.buffer.PooledByteBufAllocator
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.ArrayList

class FileProviderReaderExchanger {

    private val processors: MutableList<FileDownloadProcessor> = ArrayList()
    private var activeProcessor: FileDownloadProcessor? = null
    private var fileOutputStream: FileOutputStream? = null

    fun requestFile(
        networkInterface: NetworkInterface,
        channel: Channel,
        name: String,
        directory: Path
    ): FileDownloadProcessor {
        val fileDownloadProcessor = FileDownloadProcessor(networkInterface, channel, name, directory)
        processors.add(fileDownloadProcessor)
        requestNewProcessor()
        return fileDownloadProcessor
    }

    private fun requestNewProcessor() {
        if (this.activeProcessor == null && this.processors.size > 0) {
            this.activeProcessor = processors[0]
            val message = this.activeProcessor!!.createMessage(FileExchangerConstants.ACTIVE_FILE_PACKET_PROVIDER_REQUEST)
            message.setVar("fileName", this.activeProcessor!!.name)
            message.send()
        }
    }

    private fun removeOldProcessor() {
        if (this.activeProcessor != null) {
            this.processors.remove(this.activeProcessor)
            this.activeProcessor = null
        }
    }

    @TrafficID(id = FileExchangerConstants.ACTIVE_FILE_PACKET_NEW_PACKET)
    private fun net_newFilePacket(message: Message) {
        if (this.activeProcessor != null) {
            this.activeProcessor!!.filesToDownloadAmount = message.getVar("filesToDownload", Int::class)
            this.activeProcessor!!.bytesToDownload = message.getVar("bytesToDownload", Long::class)
        }
    }

    @TrafficID(id = FileExchangerConstants.ACTIVE_FILE_PACKET_NEW_FILE)
    fun net_newFile(message: Message) {
        if (activeProcessor != null) {
            try {
                val file = getFileLocation(message.getVar("fileLocation", String::class))
                closeFileInputStream()
                fileOutputStream = FileOutputStream(file)
                activeProcessor!!.notifyNewFileListener(file)
            } catch (e: FileNotFoundException) {
                System.err.println(e.message)
                fileOutputStream = null
            } catch (e: IOException) {
                fileDownloadDoneWithException(e)
            }
        }
    }

    @TrafficID(id = FileExchangerConstants.ACTIVE_FILE_PACKET_WRITE)
    private fun net_writeFile(message: Message) {
        if (this.activeProcessor != null && fileOutputStream != null) {
            val readBytes = message.getVar("byteArray", ByteArray::class)
            val readableBytes = message.getVar("readableBytes", Int::class)
            this.activeProcessor!!.addBytesRead(readableBytes.toLong())
            this.activeProcessor!!.notifyBytesReceived()
            try {
                fileOutputStream!!.write(readBytes, 0, readableBytes)
            } catch (e: IOException) {
                fileDownloadDoneWithException(e)
            }
        }
    }

    @TrafficID(id = FileExchangerConstants.ACTIVE_FILE_PACKET_DONE)
    fun net_done(message: Message) {
        if (activeProcessor != null && fileOutputStream != null) {
            try {
                closeFileInputStream()
                fileDownloadSuccessfullyDone()
            } catch (e: IOException) {
                fileDownloadDoneWithException(e)
            }
        }
    }

    @TrafficID(id = FileExchangerConstants.ACTIVE_FILE_PACKET_DO_NOT_EXISTS)
    private fun net_FilePacketNameNotExists(message: Message) {
        fileDownloadDoneWithException(PacketNameDoNotExists("The " + activeProcessor!!.name + " FilePacket name do not exists on the Server."))
    }

    private fun getFileLocation(path: String): File {
        val p: Path = Paths.get(activeProcessor!!.directory.toString(), path)
        val folders: Path = p.resolveSibling("")
        Files.createDirectories(folders)
        return p.toFile()
    }

    private fun fileDownloadSuccessfullyDone() {
//        activeProcessor!!.notifyDone(FileDownloadFuture(true, null))
        removeOldProcessor()
        requestNewProcessor()
    }

    private fun fileDownloadDoneWithException(throwable: Throwable) {
//        activeProcessor!!.notifyDone(FileDownloadFuture(false, throwable))
        removeOldProcessor()
        requestNewProcessor()
    }

    @Throws(IOException::class)
    private fun closeFileInputStream() {
        if (this.fileOutputStream != null) {
            this.fileOutputStream!!.close()
            this.fileOutputStream = null
        }
    }

    class PacketNameDoNotExists(message: String?) : RuntimeException(message)
}