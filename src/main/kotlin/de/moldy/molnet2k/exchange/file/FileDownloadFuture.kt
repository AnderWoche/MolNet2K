package de.moldy.molnet2k.exchange.file

class FileDownloadFuture(val isSuccess: Boolean, private val cause: Throwable?) {

    val isFailure: Boolean
        get() = !this.isSuccess

    fun cause(): Throwable? {
        return this.cause
    }
}