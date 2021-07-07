package de.moldy.molnet2k.exchange.file

import de.moldy.molnet2k.exchange.Message
import de.moldy.molnet2k.exchange.NetworkInterface
import kotlin.jvm.Synchronized
import io.netty.channel.Channel
import java.io.File
import java.nio.file.Path
import java.util.ArrayList

class FileDownloadProcessor(
    val networkInterface: NetworkInterface,
    val channel: Channel,
    val name: String,
    val directory: Path
) {
    private val fileDownloadListeners: MutableList<FileDownloadListener> = ArrayList()
    var filesToDownloadAmount: Int? = null
    var bytesToDownload: Long? = null
    var bytesRead: Long = 0
        private set

    fun addBytesRead(amount: Long): Long {
        return amount.let { bytesRead += it; bytesRead }
    }

    fun addListener(fileDownloadListener: FileDownloadListener) {
        fileDownloadListeners.add(fileDownloadListener)
    }

    fun removeListener(fileDownloadListener: FileDownloadListener) {
        fileDownloadListeners.remove(fileDownloadListener)
    }

    @Synchronized
    fun notifyNewFileListener(file: File?) {
        for (listener in fileDownloadListeners) {
            listener.newFile(file!!)
        }
    }

    @Synchronized
    fun notifyBytesReceived() {
        for (listener in fileDownloadListeners) {
            listener.bytesReceived(bytesRead, bytesToDownload!!)
        }
    }

//    @Synchronized
//    fun notifyDone(fileDownloadFuture: FileDownloadFuture?) {
//        for (listener in fileDownloadListeners) {
//            listener.done(fileDownloadFuture!!)
//        }
//        this.notifyAll()
//        Thread.
//    }

    fun createMessage(trafficID: String?): Message {
        return networkInterface.createMessage(channel, trafficID!!)
    }

//    @Synchronized
//    fun sync() {
//        try {
//            this.wait()
//        } catch (e: InterruptedException) {
//            e.printStackTrace()
//        }
//    }
}