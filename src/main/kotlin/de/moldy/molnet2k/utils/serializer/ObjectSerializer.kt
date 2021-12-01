package de.moldy.molnet2k.utils.serializer

import io.netty.buffer.ByteBuf
import kotlin.reflect.KClass

open class ObjectSerializer {

    private val deSerialisationPatterns = HashMap<KClass<*>, (ByteBuf) -> Any>()
    private val serializationPatterns = HashMap<KClass<*>, (Any) -> ByteBuf>()

    fun <T : Any> loadDeSerializationPattern(classToSerialize: KClass<T>, function: (ByteBuf) -> T) {
        this.deSerialisationPatterns[classToSerialize] = function
    }

    fun <T : Any> loadSerializationPattern(classToSerialize: KClass<T>, function: (Any) -> ByteBuf) {
        this.serializationPatterns[classToSerialize] = function
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> loadPattern(classToSerialize: KClass<T>, serialization: (T) -> ByteBuf, deSerialization: (ByteBuf) -> T) {
        this.serializationPatterns[classToSerialize] = serialization as (Any) -> ByteBuf
        this.deSerialisationPatterns[classToSerialize] = deSerialization as (ByteBuf) -> Any
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> deSerialize(classToSerialize: KClass<T>, arguments: ByteBuf): T {
        val function = this.deSerialisationPatterns[classToSerialize] as (ByteBuf) -> Any
        return function(arguments) as T
    }

    fun <T : Any> serialize(t: T): ByteBuf {
        val function = this.serializationPatterns[t::class] as (Any) -> ByteBuf
        return function(t)
    }

}