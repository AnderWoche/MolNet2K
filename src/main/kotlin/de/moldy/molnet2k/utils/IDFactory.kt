package de.moldy.molnet2k.utils

import java.util.*

open class IDFactory<T>(val identity: Boolean) {

    private val ids: MutableMap<T, Int> = if (identity) {
        IdentityHashMap()
    } else {
        HashMap<T, Int>()
    }
    private val valueFromID = IdentityHashMap<Int, T>()
    private var nextID = 0
    private val freeIDs: IntDeque = IntDeque()

    /**
     * @param identity if identity true, the Map is a IdentityMap if false, the Map is a HashMap
     */

    fun getAll(): Map.Entry<T, Int> {
        return this.ids.entries as Map.Entry<T, Int>
    }

    fun getOrCreateID(any: T): Int {
        return this.ids[any] ?: createID(any)
    }

    fun getValueFromID(id: Int): T? {
        return this.valueFromID[id]
    }

    fun getID(any: T): Int? {
        return this.ids[any]
    }

    fun containsID(any: T): Boolean {
        return this.ids.containsKey(any)
    }

    @Synchronized
    private fun createID(c: T): Int {
        val id = if (!this.freeIDs.isEmpty()) {
            this.freeIDs.popFirst()
        } else {
            this.nextID++
        }
        this.ids[c] = id
        this.valueFromID[id] = c
        return id
    }

    /**
     * @param any
     * @return the id that gets resettes
     */
    @Synchronized
    fun freeID(any: T): Int? {
        val id = this.ids.remove(any)
        this.valueFromID.remove(id)
        return if (id != null) {
            this.freeIDs.add(id)
            id
        } else {
            null
        }
    }

    fun clear() {
        this.freeIDs.clear()
        this.ids.clear()
        this.nextID = 0
    }

}