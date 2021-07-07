package de.moldy.molnet2k.exchange.file

object FileExchangerConstants {
    const val SAVED_FILE_BYTES_IN_RAM = 1_048_576 // 2^20 = 1_048_576 | 2^21 = 2_097_152

    //Receive
    const val ACTIVE_FILE_PACKET_NEW_FILE = "molnet.file.active.new.file"
    const val ACTIVE_FILE_PACKET_NEW_PACKET = "molnet.file.active.new.packet"
    const val ACTIVE_FILE_PACKET_DONE = "molnet.file.active.done"
    const val ACTIVE_FILE_PACKET_WRITE = "molnet.file.active.write"
    const val ACTIVE_FILE_PACKET_DO_NOT_EXISTS = "molnet.file.active.packet.not.exists"

    //Send
    const val ACTIVE_FILE_PACKET_PROVIDER_REQUEST = "molnet.file.active.request"
}