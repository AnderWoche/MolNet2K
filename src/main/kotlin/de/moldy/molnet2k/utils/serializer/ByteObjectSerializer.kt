package de.moldy.molnet2k.utils.serializer

import de.moldy.molnet2k.utils.ByteBufferUtils
import de.moldy.molnet2k.utils.ByteBufferUtils.Companion.bytes
import de.moldy.molnet2k.utils.ByteBufferUtils.Companion.toFloat
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import java.math.BigDecimal
import java.math.BigInteger
import java.nio.ByteBuffer

class ByteObjectSerializer : ObjectSerializer() {

    companion object {
        val byteObjectSerializer = ByteObjectSerializer()
    }

    init {
        super.loadPattern(Short::class, {
            it.bytes()
        }, {
            ByteBufferUtils.byteToShort(it)
        })

        super.loadPattern(Float::class, { it.bytes() }, { it.toFloat() })

        super.loadPattern(Int::class, {
            it.bytes()
        }, {
            ByteBufferUtils.byteToInt(it)
        })

        super.loadPattern(Long::class, {
            it.bytes()
        }, {
            ByteBufferUtils.byteToLong(it)
        })

        super.loadPattern(String::class, {
            it.toByteArray()
        }, {
            String(it)
        })

        super.loadPattern(BigInteger::class, {
            it.toByteArray()
        }, {
            BigInteger(it)
        })

        super.loadPattern(BigDecimal::class, {
            it.unscaledValue().toByteArray()
        }, {
            BigDecimal(BigInteger(it), 0)
        })

        super.loadPattern(ByteBuf::class, {
            it.release()
            it.array()
        }, {
            ByteBufAllocator.DEFAULT.buffer().writeBytes(it)
        })

        super.loadPattern(ByteArray::class, { it }, { it })

    }

}