package de.moldy.molnet2k.utils

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class ByteBufferUtils {

    companion object {
        val UTF8Charset: Charset = StandardCharsets.UTF_8

//        fun writeUTF8String(byteBuf: ByteBuf, s: String) {
//            val stringByteBuffer = ByteBufAllocator.DEFAULT.buffer()
//            stringByteBuffer.writeCharSequence(s, UTF8Charset)
//            byteBuf.writeInt(stringByteBuffer.readableBytes())
//            byteBuf.writeBytes(stringByteBuffer)
//            stringByteBuffer.release()
//        }

        fun String.toLengthAndStringByteBuf(): ByteBuf {
            val byteBuf = ByteBufAllocator.DEFAULT.buffer(this.length)
            byteBuf.writeInt(this.length)
            byteBuf.writeCharSequence(this, UTF8Charset)
            return byteBuf
        }

        fun ByteBuf.readLengthAndString(): CharSequence {
            val readable = this.readInt()
            return this.readCharSequence(readable, UTF8Charset)
        }

//        fun readUTF8String(byteBuf: ByteBuf): String {
//            val length = byteBuf.readInt()
//            return byteBuf.readCharSequence(length, UTF8Charset) as String
//        }

//        fun addStringBeforeMassage(string: String, byteBuf: ByteBuf): ByteBuf {
//            val stringByteBuffer = ByteBufAllocator.DEFAULT.buffer()
//            stringByteBuffer.writeBytes(string.toByteBuf())
//            stringByteBuffer.writeBytes(byteBuf)
//            byteBuf.clear()
//            byteBuf.writeBytes(stringByteBuffer)
//            stringByteBuffer.release()
//            return byteBuf
//        }

        fun Short.toLengthAndStringByteBuf(): ByteBuf {
            val buffer = ByteBufAllocator.DEFAULT.buffer(2)
            buffer.clear()
            buffer.writeShort(this.toInt())
            return buffer
        }

        fun Float.toLengthAndStringByteBuf(): ByteBuf {
            val buffer = ByteBufAllocator.DEFAULT.buffer(4)
            buffer.clear()
            buffer.writeFloat(this)
            return buffer
        }

        fun Int.toLengthAndStringByteBuf(): ByteBuf {
            val buffer = ByteBufAllocator.DEFAULT.buffer(4)
            buffer.clear()
            buffer.writeInt(this)
            return buffer
        }

        fun Long.toLengthAndStringByteBuf(): ByteBuf {
            val buffer = ByteBufAllocator.DEFAULT.buffer(8)
            buffer.clear()
            buffer.writeLong(this)
            return buffer
        }
    }

}