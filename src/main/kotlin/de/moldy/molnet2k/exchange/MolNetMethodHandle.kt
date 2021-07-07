package de.moldy.molnet2k.exchange

import de.moldy.molnet2k.utils.BitVector
import java.lang.invoke.MethodHandles
import java.lang.reflect.Method

class MolNetMethodHandle<T>(private val any: T, m: Method, val id: String, private var rights: BitVector?) {

    companion object {
        private val lookup = MethodHandles.lookup()
    }

    private val methodHandle = lookup.unreflect(m)

    fun isRightRestricted(): Boolean {
        return this.rights != null
    }

    fun hasAccess(rights: BitVector?): Boolean {
        if (this.rights != null && rights != null) {
            return this.rights!!.containsAll(rights)
        }
        return false
    }

    /**
     * @return true if rights equals, false if not equals
     */
    fun invokeWithRights(rights: BitVector, message: Message): Boolean {
        return if(this.hasAccess(rights)) {
            this.invokeIgnoreRights(message)
            true
        } else {
            false
        }
    }

    fun invokeIgnoreRights(message: Message) {
        this.methodHandle.invoke(this.any, message)
    }
}