package de.moldy.molnet2k.utils

import java.util.*

open class BothSideHashMapWithArray<K, V>(synchronised: Boolean) {

    var map: MutableMap<K, V>
    var invertedMap: MutableMap<V, K>
    var array: MutableList<V>

    init {
        if (synchronised) {
            this.map = Collections.synchronizedMap(HashMap())
            this.invertedMap = Collections.synchronizedMap(HashMap())
            this.array = Collections.synchronizedList(ArrayList())
        } else {
            this.map = HashMap()
            this.invertedMap = HashMap()
            this.array = ArrayList()
        }
    }

    fun getValue(key: K): V? {
        return this.map[key]
    }

    fun getKey(value: V): K? {
        return this.invertedMap[value]
    }

    @Synchronized
    fun put(key: K, value: V) {
        this.map[key] = value
        this.invertedMap[value] = key
        this.array.add(value)
    }

    fun containsKey(key: K): Boolean {
        return this.map.containsKey(key)
    }

    fun containsValue(value: V): Boolean {
        return this.map.containsValue(value)
    }

    fun removeWithKey(key: K): V? {
        val value = this.map.remove(key)
        this.invertedMap.remove(value)
        this.array.remove(value)
        return value
    }

    fun removeWithValue(value: V): K? {
        val key = this.invertedMap.remove(value)
        this.map.remove(key)
        this.array.remove(value)
        return key
    }

    val isEmpty: Boolean
        get() {
            if (!this.map.isEmpty()) {
                return false
            }
            return if (!this.invertedMap.isEmpty()) {
                false
            } else this.array.size <= 0
        }

    fun clear() {
        this.map.clear()
        this.invertedMap.clear()
        this.array.clear()
    }

}