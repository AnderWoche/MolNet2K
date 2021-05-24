package de.moldy.molnet2k.utils

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class ByteBufferUtils {

    companion object {
        val byteBufferPool2 = object : Pool<ByteBuffer>(100) {
            override fun newObject(): ByteBuffer {
                return ByteBuffer.allocate(2)
            }
        }
        val byteBufferPool4 = object : Pool<ByteBuffer>(100) {
            override fun newObject(): ByteBuffer {
                return ByteBuffer.allocate(4)
            }
        }
        val byteBufferPool8 = object : Pool<ByteBuffer>(100) {
            override fun newObject(): ByteBuffer {
                return ByteBuffer.allocate(8)
            }
        }

        val UTF8Charset: Charset = StandardCharsets.UTF_8

        fun writeUTF8String(byteBuf: ByteBuf, s: String) {
            val stringByteBuffer = ByteBufAllocator.DEFAULT.buffer()
            stringByteBuffer.writeCharSequence(s, UTF8Charset)
            byteBuf.writeInt(stringByteBuffer.readableBytes())
            byteBuf.writeBytes(stringByteBuffer)
            stringByteBuffer.release()
        }

        fun readUTF8String(byteBuf: ByteBuf): String {
            val length = byteBuf.readInt()
            return byteBuf.readCharSequence(length, UTF8Charset) as String
        }

        fun addStringBeforeMassage(string: String, byteBuf: ByteBuf): ByteBuf {
            val stringByteBuffer = ByteBufAllocator.DEFAULT.buffer()
            writeUTF8String(stringByteBuffer, string)
            stringByteBuffer.writeBytes(byteBuf)
            byteBuf.clear()
            byteBuf.writeBytes(stringByteBuffer)
            stringByteBuffer.release()
            return byteBuf
        }

        fun Short.bytes(): ByteArray {
            val buffer = byteBufferPool2.obtain()
            buffer.rewind()
            buffer.putShort(this)
            buffer.rewind()
            return buffer.array()
        }

        fun byteToShort(byteArray: ByteArray): Short {
            val buffer = byteBufferPool8.obtain()
            buffer.rewind()
            buffer.put(byteArray)
            buffer.rewind()
            return buffer.getShort()
        }

        fun Int.bytes(): ByteArray {
            val buffer = byteBufferPool4.obtain()
            buffer.rewind()
            buffer.putInt(this)
            buffer.rewind()
            return buffer.array()
        }

        fun byteToInt(byteArray: ByteArray): Int {
            val buffer = byteBufferPool4.obtain()
            buffer.rewind()
            buffer.put(byteArray)
            buffer.rewind()
            return buffer.getInt()
        }

        fun Long.bytes(): ByteArray {
            val buffer = byteBufferPool8.obtain()
            buffer.rewind()
            buffer.putLong(this)
            buffer.rewind()
            return buffer.array()
        }

        fun byteToLong(byteArray: ByteArray): Long {
            val buffer = byteBufferPool8.obtain()
            buffer.rewind()
            buffer.put(byteArray)
            buffer.rewind()
            return buffer.getLong()
        }
    }

}