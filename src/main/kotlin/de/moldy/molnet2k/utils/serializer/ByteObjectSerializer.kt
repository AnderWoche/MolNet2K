package de.moldy.molnet2k.utils.serializer

import de.moldy.molnet2k.utils.ByteBufferUtils
import de.moldy.molnet2k.utils.ByteBufferUtils.Companion.toLengthAndStringByteBuf
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import java.math.BigDecimal
import java.math.BigInteger

class ByteObjectSerializer : ObjectSerializer() {

    companion object {
        val byteObjectSerializer = ByteObjectSerializer()
    }

    init {
        super.loadPattern(Short::class, {
            it.toLengthAndStringByteBuf()
        }, {
            it.readShort()
        })

        super.loadPattern(Float::class, { it.toLengthAndStringByteBuf() }, { it.readFloat() })

        super.loadPattern(Int::class, {
            it.toLengthAndStringByteBuf()
        }, {
            it.readInt()
        })

        super.loadPattern(Long::class, {
            it.toLengthAndStringByteBuf()
        }, {
            it.readLong()
        })

        super.loadPattern(String::class, {
            val byteBuf = ByteBufAllocator.DEFAULT.buffer(it.length)
            byteBuf.writeCharSequence(it, ByteBufferUtils.UTF8Charset)
            byteBuf
        }, {
            val charSequence = it.readCharSequence(it.readableBytes(), ByteBufferUtils.UTF8Charset)
            it.release()
            charSequence as String
        })

        super.loadPattern(BigInteger::class, {
            val byteBuf = ByteBufAllocator.DEFAULT.buffer()
            byteBuf.writeBytes(it.toByteArray())
        }, {
            BigInteger(it.array())
        })

        super.loadPattern(BigDecimal::class, {
            val byteBuf = ByteBufAllocator.DEFAULT.buffer()
            byteBuf.writeBytes(it.unscaledValue().toByteArray())
        }, {
            BigDecimal(BigInteger(it.array()), 0)
        })

        super.loadPattern(ByteBuf::class, { it }, { it })
//        super.loadPattern(ByteArray::class, { it }, { it })
    }

}