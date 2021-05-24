package de.moldy.molnet2k.utils

import java.util.*

class BitVector() {

    var words = longArrayOf(0)

    /** Creates a bit set whose initial size is large enough to explicitly represent bits with indices in the range 0 through
     * nbits-1.
     * @param nbits the initial size of the bit set
     */
    constructor(nbits: Int) : this() {
        checkCapacity(nbits ushr 6)
    }

    /** Creates a bit set based off another bit vector.
     * @param copyFrom
     */
    constructor(copyFrom: BitVector) : this() {
        words = copyFrom.words.copyOf(copyFrom.words.size)
    }

    /** @param index the index of the bit
     * @return whether the bit is set
     * @throws ArrayIndexOutOfBoundsException if index < 0
     */
    operator fun get(index: Int): Boolean {
        val word = index ushr 6
        return word < words.size &&
                words[word] and (1L shl index) != 0L
    }

    /** @param index the index of the bit to set
     * @throws ArrayIndexOutOfBoundsException if index < 0
     */
    fun set(index: Int) {
        val word = index ushr 6
        checkCapacity(word)
        words[word] = words[word] or (1L shl index)
    }

    /** @param index the index of the bit to set
     * @throws ArrayIndexOutOfBoundsException if index < 0
     */
    operator fun set(index: Int, value: Boolean) {
        if (value) {
            set(index)
        } else {
            clear(index)
        }
    }

    /** @param index the index of the bit
     * @return whether the bit is set
     * @throws ArrayIndexOutOfBoundsException if index < 0 or index >= words.length>>
     */
    fun unsafeGet(index: Int): Boolean {
        return words[index ushr 6] and (1L shl index) != 0L
    }

    /** @param index the index of the bit to set
     * @throws ArrayIndexOutOfBoundsException if index < 0 or index >= words.length
     */
    fun unsafeSet(index: Int) {
        words[index ushr 6] = words[index ushr 6] or (1L shl index)
    }

    /** @param index the index of the bit to set
     * @throws ArrayIndexOutOfBoundsException if index < 0 or index >= words.length
     */
    fun unsafeSet(index: Int, value: Boolean) {
        if (value) {
            unsafeSet(index)
        } else {
            unsafeClear(index)
        }
    }

    /** @param index the index of the bit to flip
     */
    fun flip(index: Int) {
        val word = index ushr 6
        checkCapacity(word)
        words[word] = words[word] xor (1L shl index)
    }

    /**
     * Grows the backing array (`long[]`) so that it can hold the requested
     * bits. Mostly applicable when relying on the `unsafe` methods,
     * including [.unsafeGet] and [.unsafeClear].
     *
     * @param bits number of bits to accomodate
     */
    fun ensureCapacity(bits: Int) {
        checkCapacity(bits ushr 6)
    }

    private fun checkCapacity(len: Int) {
        if (len >= words.size) {
            val newBits = LongArray(len + 1)
            System.arraycopy(words, 0, newBits, 0, words.size)
            words = newBits
        }
    }

    /** @param index the index of the bit to clear
     * @throws ArrayIndexOutOfBoundsException if index < 0
     */
    fun clear(index: Int) {
        val word = index ushr 6
        if (word >= words.size) return
        words[word] = words[word] and (1L shl index).inv()
    }

    /** @param index the index of the bit to clear
     * @throws ArrayIndexOutOfBoundsException if index < 0 or index >= words.length
     */
    fun unsafeClear(index: Int) {
        words[index ushr 6] = words[index ushr 6] and (1L shl index).inv()
    }

    /** Clears the entire bitset  */
    fun clear() {
        Arrays.fill(words, 0L)
    }

    /** Returns the "logical size" of this bitset: the index of the highest set bit in the bitset plus one. Returns zero if the
     * bitset contains no set bits.
     *
     * @return the logical size of this bitset
     */
    fun length(): Int {
        val bits = words
        for (word in bits.indices.reversed()) {
            val bitsAtWord = bits[word]
            if (bitsAtWord != 0L) return (word shl 6) + 64 - java.lang.Long.numberOfLeadingZeros(bitsAtWord)
        }
        return 0
    }

    /** @return true if this bitset contains no bits that are set to true
     */
    fun isEmpty(): Boolean {
        val bits = words
        val length = bits.size
        for (i in 0 until length) {
            if (bits[i] != 0L) {
                return false
            }
        }
        return true
    }

    /** Returns the index of the first bit that is set to true that occurs on or after the specified starting index. If no such bit
     * exists then -1 is returned.  */
    fun nextSetBit(fromIndex: Int): Int {
        val word = fromIndex ushr 6
        if (word >= words.size) return -1
        var bitmap = words[word] ushr fromIndex
        if (bitmap != 0L) return fromIndex + java.lang.Long.numberOfTrailingZeros(bitmap)
        for (i in 1 + word until words.size) {
            bitmap = words[i]
            if (bitmap != 0L) {
                return i * 64 + java.lang.Long.numberOfTrailingZeros(bitmap)
            }
        }
        return -1
    }

    /** Returns the index of the first bit that is set to false that occurs on or after the specified starting index.  */
    fun nextClearBit(fromIndex: Int): Int {
        val word = fromIndex ushr 6
        if (word >= words.size) return Math.min(fromIndex, words.size shl 6)
        var bitmap = (words[word] ushr fromIndex).inv()
        if (bitmap != 0L) return fromIndex + java.lang.Long.numberOfTrailingZeros(bitmap)
        for (i in 1 + word until words.size) {
            bitmap = words[i].inv()
            if (bitmap != 0L) {
                return i * 64 + java.lang.Long.numberOfTrailingZeros(bitmap)
            }
        }
        return Math.min(fromIndex, words.size shl 6)
    }

    /** Performs a logical **AND** of this target bit set with the argument bit set. This bit set is modified so that each bit in
     * it has the value true if and only if it both initially had the value true and the corresponding bit in the bit set argument
     * also had the value true.
     * @param other a bit set
     */
    fun and(other: BitVector) {
        val commonWords = Math.min(words.size, other.words.size)
        var i = 0
        while (commonWords > i) {
            words[i] = words[i] and other.words.get(i)
            i++
        }
        if (words.size > commonWords) {
            var i = commonWords
            val s = words.size
            while (s > i) {
                words[i] = 0L
                i++
            }
        }
    }

    /** Clears all of the bits in this bit set whose corresponding bit is set in the specified bit set.
     *
     * @param other a bit set
     */
    fun andNot(other: BitVector) {
        val commonWords = Math.min(words.size, other.words.size)
        var i = 0
        while (commonWords > i) {
            words[i] = words[i] and other.words.get(i).inv()
            i++
        }
    }

    /** Performs a logical **OR** of this bit set with the bit set argument. This bit set is modified so that a bit in it has the
     * value true if and only if it either already had the value true or the corresponding bit in the bit set argument has the
     * value true.
     * @param other a bit set
     */
    fun or(other: BitVector) {
        val commonWords = Math.min(words.size, other.words.size)
        var i = 0
        while (commonWords > i) {
            words[i] = words[i] or other.words.get(i)
            i++
        }
        if (commonWords < other.words.size) {
            checkCapacity(other.words.size)
            var i = commonWords
            val s: Int = other.words.size
            while (s > i) {
                words[i] = other.words.get(i)
                i++
            }
        }
    }

    /** Performs a logical **XOR** of this bit set with the bit set argument. This bit set is modified so that a bit in it has
     * the value true if and only if one of the following statements holds:
     *
     *  * The bit initially has the value true, and the corresponding bit in the argument has the value false.
     *  * The bit initially has the value false, and the corresponding bit in the argument has the value true.
     *
     * @param other
     */
    fun xor(other: BitVector) {
        val commonWords = Math.min(words.size, other.words.size)
        var i = 0
        while (commonWords > i) {
            words[i] = words[i] xor other.words.get(i)
            i++
        }
        if (commonWords < other.words.size) {
            checkCapacity(other.words.size)
            var i = commonWords
            val s: Int = other.words.size
            while (s > i) {
                words[i] = other.words.get(i)
                i++
            }
        }
    }

    /** Returns true if the specified BitVector has any bits set to true that are also set to true in this BitVector.
     *
     * @param other a bit set
     * @return boolean indicating whether this bit set intersects the specified bit set
     */
    fun intersects(other: BitVector): Boolean {
        val bits = words
        val otherBits: LongArray = other.words
        var i = 0
        val s = Math.min(bits.size, otherBits.size)
        while (s > i) {
            if (bits[i] and otherBits[i] != 0L) {
                return true
            }
            i++
        }
        return false
    }

    /** Returns true if this bit set is a super set of the specified set,
     * i.e. it has all bits set to true that are also set to true
     * in the specified BitVector.
     *
     * @param other a bit set
     * @return boolean indicating whether this bit set is a super set of the specified set
     */
    fun containsAll(other: BitVector): Boolean {
        val bits = words
        val otherBits: LongArray = other.words
        val otherBitsLength = otherBits.size
        val bitsLength = bits.size
        for (i in bitsLength until otherBitsLength) {
            if (otherBits[i] != 0L) {
                return false
            }
        }
        var i = 0
        val s = Math.min(bitsLength, otherBitsLength)
        while (s > i) {
            if (bits[i] and otherBits[i] != otherBits[i]) {
                return false
            }
            i++
        }
        return true
    }

    fun cardinality(): Int {
        var count = 0
        for (i in words.indices) count += java.lang.Long.bitCount(words[i])
        return count
    }

    override fun hashCode(): Int {
        val word = length() ushr 6
        var hash = 0
        var i = 0
        while (word >= i) {
            hash = 127 * hash + (words[i] xor (words[i] ushr 32)).toInt()
            i++
        }
        return hash
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) return true
        if (obj == null) return false
        if (javaClass != obj.javaClass) return false
        val other: BitVector = obj as BitVector
        val otherBits: LongArray = other.words
        val commonWords = Math.min(words.size, otherBits.size)
        var i = 0
        while (commonWords > i) {
            if (words[i] != otherBits[i]) return false
            i++
        }
        return if (words.size == otherBits.size) true else length() == other.length()
    }

    override fun toString(): String {
        val cardinality = cardinality()
        val end = Math.min(128, cardinality)
        var count = 0
        val sb = StringBuilder()
        sb.append("BitVector[").append(cardinality)
        if (cardinality > 0) {
            sb.append(": {")
            var i = nextSetBit(0)
            while (end > count && i != -1) {
                if (count != 0) sb.append(", ")
                sb.append(i)
                count++
                i = nextSetBit(i + 1)
            }
            if (cardinality > end) sb.append(" ...")
            sb.append("}")
        }
        sb.append("]")
        return sb.toString()
    }

}