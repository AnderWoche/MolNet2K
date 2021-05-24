package de.moldy.molnet2k.exchange

import de.moldy.molnet2k.utils.BitVector
import de.moldy.molnet2k.utils.IDFactory

/**
 * @param identity if identity true, the Map is a IdentityMap if false, the Map is a HashMap
 */
class RightIDFactory(identity: Boolean) : IDFactory<String>(identity) {

    fun addRightBits(bitVector: BitVector, rights: Array<String>): BitVector {
        for(s in rights) {
            val stringRight = s.lowercase()
            val id = super.getOrCreateID(stringRight)
            bitVector.set(id)
        }
        return bitVector
    }

    fun removeRightsBits(bitVector: BitVector, rights: Array<String>): BitVector {
        for(s in rights) {
            val stringRight = s.lowercase()
            val id = super.getOrCreateID(stringRight)
            bitVector.clear(id)
        }
        return bitVector
    }

}