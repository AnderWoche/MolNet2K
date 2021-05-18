package de.moldy.molnet2k.exchange

import java.lang.reflect.Method
import kotlin.reflect.KClass

class MessageExchangerManager {

    private val messageExchangerMap = HashMap<KClass<out Any>, Any>()

    fun loadServerMessageExchanger(any: Any) {
        this.messageExchangerMap[any::class] = any
        val allMethods = any.javaClass.declaredMethods.asList()
        val methods = AnnotationMethodFilter(allMethods).all(TrafficID::class.java).exclude(ClientOnly::class.java).filter()
        this.loadMessageExchanger(methods)
    }

    fun loadClientMessageExchanger(any: Any) {
        this.messageExchangerMap[any::class] = any
        val allMethods = any.javaClass.declaredMethods.asList()
        val methods = AnnotationMethodFilter(allMethods).all(TrafficID::class.java).exclude(ServerOnly::class.java).filter()
        this.loadMessageExchanger(methods)
    }

    fun temp(any: Any, annotation: KClass<out Annotation>) {

    }

    @Synchronized
    fun loadMessageExchanger(methods: List<Method>) {
        for(method in methods) {

        }
    }

}