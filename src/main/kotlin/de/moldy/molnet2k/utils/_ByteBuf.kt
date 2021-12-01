package de.moldy.molnet2k.utils

import io.netty.buffer.ByteBuf

fun ByteBuf.writeBytesAndRelease(byteBuf: ByteBuf) {
    this.writeBytes(byteBuf)
    byteBuf.release()
}