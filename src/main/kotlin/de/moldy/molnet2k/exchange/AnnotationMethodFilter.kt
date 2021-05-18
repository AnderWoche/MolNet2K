package de.moldy.molnet2k.exchange

import java.lang.reflect.Method
import kotlin.reflect.KClass

class AnnotationMethodFilter(private val methods: List<Method>) {

    internal val all = ArrayList<Class<out Annotation>>(2)
    internal val one = ArrayList<Class<out Annotation>>(1)
    internal val exclude = ArrayList<Class<out Annotation>>(1)


    fun all(vararg annotation: Class<out Annotation>): AnnotationMethodFilter {
        this.all.addAll(annotation)
        return this
    }

    fun one(vararg annotation: Class<out Annotation>): AnnotationMethodFilter {
        this.one.addAll(annotation)
        return this
    }

    fun exclude(vararg annotation: Class<out Annotation>): AnnotationMethodFilter {
        this.exclude.addAll(annotation)
        return this
    }

    fun filter(): List<Method> {
        val returnList = ArrayList<Method>()

        for (method in this.methods) {
            method.isAccessible = true
            if(this.isAllPresent(method)) {
                if(this.isOnePresent(method)) {
                    if(!this.isExcludePresent(method)) {
                        returnList.add(method)
                    }
                }
            }
        }
        return returnList
    }



    private fun isAllPresent(method: Method): Boolean {
        for(annotation in this.all) {
            if(!method.isAnnotationPresent(annotation)) return false
        }
        return true
    }

    private fun isOnePresent(method: Method): Boolean {
        if(one.isEmpty()) return true
        for(annotation in this.one) {
            if(method.isAnnotationPresent(annotation)) return true
        }
        return false
    }

    private fun isExcludePresent(method: Method): Boolean {
        for(annotation in this.exclude) {
            if(method.isAnnotationPresent(annotation)) return true
        }
        return false
    }

}