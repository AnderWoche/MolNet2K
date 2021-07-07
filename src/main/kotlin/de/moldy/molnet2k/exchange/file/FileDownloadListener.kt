package de.moldy.molnet2k.exchange.file

import java.io.File

interface FileDownloadListener {

    fun newFile(file: File)
    fun bytesReceived(currentSize: Long, totalSize: Long)
    fun done(fileDownloadFuture: FileDownloadFuture)
}