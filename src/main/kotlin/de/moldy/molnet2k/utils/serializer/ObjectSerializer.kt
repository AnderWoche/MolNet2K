package de.moldy.molnet2k.utils.serializer

import kotlin.reflect.KClass

open class ObjectSerializer {

    private val deSerialisationPatterns = HashMap<KClass<*>, (ByteArray) -> Any>()
    private val serializationPatterns = HashMap<KClass<*>, (Any) -> ByteArray>()

    fun <T : Any> loadDeSerializationPattern(classToSerialize: KClass<T>, function: (ByteArray) -> T) {
        this.deSerialisationPatterns[classToSerialize] = function
    }

    fun <T : Any> loadSerializationPattern(classToSerialize: KClass<T>, function: (Any) -> ByteArray) {
        this.serializationPatterns[classToSerialize] = function
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> loadPattern(classToSerialize: KClass<T>, serialization: (T) -> ByteArray, deSerialization: (ByteArray) -> T) {
        this.serializationPatterns[classToSerialize] = serialization as (Any) -> ByteArray
        this.deSerialisationPatterns[classToSerialize] = deSerialization as (ByteArray) -> Any
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> deSerialize(classToSerialize: KClass<T>, arguments: ByteArray): T {
        return this.deSerialisationPatterns[classToSerialize]?.invoke(arguments) as T
    }

    fun <T : Any> serialize(t: T): ByteArray {
        return this.serializationPatterns[t::class]?.invoke(t) as ByteArray
    }

}