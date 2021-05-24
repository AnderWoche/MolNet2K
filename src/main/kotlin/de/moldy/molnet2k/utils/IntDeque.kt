package de.moldy.molnet2k.utils

import java.util.*

class IntDeque {

    private var elements: IntArray
    private var beginIndex = 0
    protected var size = 0

    /**
     * Constructs an empty Bag with an initial capacity of 64.
     */
    constructor(): this(64) {
    }

    /**
     * Constructs an empty Bag with the specified initial capacity.
     *
     * @param capacity the initial capacity of Bag
     */
    constructor(capacity: Int) {
        elements = IntArray(capacity)
    }


    /**
     * Check if bag contains this element.
     *
     * @param e element to check
     * @return `true` if the bag contains this element
     */
    operator fun contains(e: Int): Boolean {
        var i = 0
        while (size > i) {
            if (e == elements[index(i)]) {
                return true
            }
            i++
        }
        return false
    }

    /**
     * Returns the element at the specified position in Bag.
     *
     * @param index index of the element to return
     * @return the element at the specified position in bag
     * @throws ArrayIndexOutOfBoundsException if the index is out of range
     * (`index < 0 || index >= size()`)
     */
    operator fun get(index: Int): Int {
        return elements[index(index)]
    }

    /**
     * Returns the number of elements in this bag.
     *
     * @return the number of elements in this bag
     */
    fun size(): Int {
        return size
    }

    /**
     * Returns the number of elements the bag can hold without growing.
     *
     * @return the number of elements the bag can hold without growing
     */
    fun getCapacity(): Int {
        return elements.size
    }

    /**
     * Returns true if this bag contains no elements.
     *
     * @return `true` if this bag contains no elements
     */
    fun isEmpty(): Boolean {
        return size == 0
    }

    /**
     * Adds the specified element to the end of this bag.
     *
     *
     * If required, it also increases the capacity of the bag.
     *
     *
     * @param e element to be added to this list
     */
    fun add(e: Int) {
        if (size == elements.size) grow(elements.size * 7 / 4 + 1)
        elements[index(size++)] = e
    }

    private fun index(relativeIndex: Int): Int {
        return (beginIndex + relativeIndex) % elements.size
    }

    /**
     * Set element at specified index in the bag.
     *
     * @param index position of element
     * @param e     the element
     */
    operator fun set(index: Int, e: Int) {
        if (index >= elements.size) {
            grow(index * 7 / 4 + 1)
        }
        size = Math.max(size, index + 1)
        elements[index(index)] = e
    }

    /**
     * Increase the capacity of the bag.
     *
     * @param newCapacity new capacity to grow to
     * @throws ArrayIndexOutOfBoundsException if new capacity is smaller than old
     */
    private fun grow(newCapacity: Int) {
        val newElements = IntArray(newCapacity)
        for (i in 0 until size) newElements[i] = get(i)
        elements = newElements
        beginIndex = 0
    }

    /**
     * Check if an item, if added at the given item will fit into the bag.
     *
     *
     * If not, the bag capacity will be increased to hold an item at the index.
     *
     *
     * @param index index to check
     */
    fun ensureCapacity(index: Int) {
        if (index >= elements.size) {
            grow(index)
        }
    }

    /**
     * Removes all of the elements from this bag.
     *
     *
     * The bag will be empty after this call returns.
     *
     */
    fun clear() {
        Arrays.fill(elements, 0, size, 0)
        size = 0
        beginIndex = 0
    }

    /**
     * Set the size.
     *
     *
     * This will not resize the bag, nor will it clean up contents beyond the
     * given size. Use with caution.
     *
     *
     * @param size the size to set
     */
    @JvmName("setSize1")
    fun setSize(size: Int) {
        this.size = size
    }

    fun popLast(): Int {
        assertNotEmpty()
        val index = index(--size)
        return elements[index]
    }

    fun popFirst(): Int {
        assertNotEmpty()
        val value = elements[beginIndex]
        beginIndex = (beginIndex + 1) % elements.size
        size--
        return value
    }


    private fun assertNotEmpty() {
        if (size == 0) throw RuntimeException("Deque is empty.")
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val other: IntDeque = o as IntDeque
        if (size != other.size) return false
        var i = 0
        while (size > i) {
            if (get(i) != other.get(i)) return false
            i++
        }
        return true
    }

    override fun hashCode(): Int {
        var hash = 0
        var i = 0
        val s = size
        while (s > i) {
            hash = 127 * hash + elements[i]
            i++
        }
        return hash
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("IntDeque(")
        var i = 0
        while (size > i) {
            if (i > 0) sb.append(", ")
            sb.append(elements[index(i)])
            i++
        }
        sb.append(')')
        return sb.toString()
    }

}