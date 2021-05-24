package de.moldy.molnet2k.exchange

import de.moldy.molnet2k.exchange.annotations.ClientOnly
import de.moldy.molnet2k.exchange.annotations.Rights
import de.moldy.molnet2k.exchange.annotations.ServerOnly
import de.moldy.molnet2k.exchange.annotations.TrafficID
import de.moldy.molnet2k.utils.BitVector
import java.lang.reflect.Method
import kotlin.reflect.KClass

class MessageExchangerManager(private val rightIDFactory: RightIDFactory) {

    private val messageExchangerMap = HashMap<KClass<out Any>, Any>()
    private val exchangerIdMapString = HashMap<String, MolNetMethodHandle>()
    private val exchangerIdMapInteger = HashMap<Int, MolNetMethodHandle>()
    val intIdToStringIdMap = HashMap<Int, String>()
    private val stringIdToIntIdMap = HashMap<String, Int>()

    fun loadServerMessageExchanger(any: Any) {
        this.loadMessageExchanger(any, ClientOnly::class.java)
    }

    fun loadClientMessageExchanger(any: Any) {
        this.loadMessageExchanger(any, ServerOnly::class.java)
    }

    @Synchronized
    private fun loadMessageExchanger(any: Any, excludeAnnotation: Class<out Annotation>) {
        this.messageExchangerMap[any::class] = any
        val allMethods = any.javaClass.declaredMethods.asList()
        val methods = AnnotationMethodFilter(allMethods)
            .all(TrafficID::class.java)
            .exclude(excludeAnnotation)
            .filter()

        for(method in methods) {
            val trafficIDString = method.getAnnotation(TrafficID::class.java).id

            val methodHandle = MolNetMethodHandle(any, method, MethodID(trafficIDString), this.getRightsFromMethod(any, method))

            this.exchangerIdMapString[trafficIDString] = methodHandle
        }

//        val onRun = AnnotationMethodFilter(methods).all(RunOnChannelConnect::class.java).filter()

    }

    fun getMethodHandle(id: String): MolNetMethodHandle? {
        return this.exchangerIdMapString[id]
    }

    fun getMethodHandle(id: Int): MolNetMethodHandle? {
        return this.exchangerIdMapInteger[id]
    }

    fun getIntIdFromTrafficId(trafficID: String): Int? {
        return this.stringIdToIntIdMap[trafficID]
    }

    fun getTrafficIdFromIntId(intId: Int): String? {
        return this.intIdToStringIdMap[intId]
    }

    fun associateTrafficIDWithInt(id: Int, trafficID: String) {
        this.intIdToStringIdMap[id] = trafficID
        this.stringIdToIntIdMap[trafficID] = id
        val methodHandle = this.exchangerIdMapString[trafficID]
        if(methodHandle != null) this.exchangerIdMapInteger[id] = methodHandle
    }

    private fun getRightsFromMethod(any: Any, method: Method): BitVector? {
        var rightBits: BitVector? = null
        val allMethodRights: Rights? = any.javaClass.getAnnotation(Rights::class.java)
        if (allMethodRights != null) {
            if(rightBits == null) rightBits = BitVector()
            this.rightIDFactory.addRightBits(rightBits, allMethodRights.rights)
        }
        val methodRights: Rights? = method.getAnnotation(Rights::class.java)
        if (methodRights != null) {
            if(rightBits == null) rightBits = BitVector()
            this.rightIDFactory.addRightBits(rightBits, methodRights.rights)
        }
        return rightBits
    }

}