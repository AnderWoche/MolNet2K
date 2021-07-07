package de.moldy.molnet2k.exchange

import de.moldy.molnet2k.exchange.annotations.ClientOnly
import de.moldy.molnet2k.exchange.annotations.Rights
import de.moldy.molnet2k.exchange.annotations.ServerOnly
import de.moldy.molnet2k.exchange.annotations.TrafficID
import de.moldy.molnet2k.utils.BitVector
import java.lang.reflect.Method
import kotlin.reflect.KClass

open class MessageExchangerManager {

    private val rightIDFactory = RightIDFactory(false)

    private val messageExchangerMap = HashMap<KClass<out Any>, Any>()
    private val exchangerIdMap = HashMap<String, MolNetMethodHandle<*>>()

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
            val trafficID = method.getAnnotation(TrafficID::class.java).id

            val methodHandle = MolNetMethodHandle(any, method, trafficID, this.getRightsFromMethod(any, method))

            this.exchangerIdMap[trafficID] = methodHandle
        }

//        val onRun = AnnotationMethodFilter(methods).all(RunOnChannelConnect::class.java).filter()

    }

    fun getMethodHandle(id: String): MolNetMethodHandle<*>? {
        return this.exchangerIdMap[id]
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

    fun <T : Any> getMessageExchanger(exchangerClass: KClass<out T>): T? {
        return this.messageExchangerMap[exchangerClass] as T?
    }

}