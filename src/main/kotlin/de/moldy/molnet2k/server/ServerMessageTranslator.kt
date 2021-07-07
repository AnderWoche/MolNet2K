package de.moldy.molnet2k.server

import de.moldy.molnet2k.utils.ByteBufferUtils
import de.moldy.molnet2k.utils.IDFactory
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator

class ServerMessageTranslator : IDFactory<String>(false) {

    init {
        val id = super.getOrCreateID("stringToID")
        if (id != 0) {
            throw RuntimeException("ID 0 need to be reserved for the StringToId transfer")
        }
    }

    fun getIntFromString(string: String, out: ByteBuf): Int {
        var id = super.getID(string)
        if (id == null) {
            id = super.getOrCreateID(string)
            val buffer = ByteBufAllocator.DEFAULT.buffer()
            buffer.writeInt(0) // trafficID
            ByteBufferUtils.writeUTF8String(buffer, string) // string
            buffer.writeInt(id) // id
            out.writeInt(buffer.readableBytes())
            out.writeBytes(buffer)
        }
        return id
    }

}