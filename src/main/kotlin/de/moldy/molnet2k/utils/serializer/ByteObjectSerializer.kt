package de.moldy.molnet2k.utils.serializer

import de.moldy.molnet2k.utils.ByteBufferUtils
import de.moldy.molnet2k.utils.ByteBufferUtils.Companion.bytes
import java.math.BigDecimal
import java.math.BigInteger

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

        super.loadPattern(Int::class, {
            it.bytes()
        }, {
            ByteBufferUtils.byteToInt(it)
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

    }

}