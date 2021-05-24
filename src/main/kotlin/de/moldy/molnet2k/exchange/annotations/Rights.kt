package de.moldy.molnet2k.exchange.annotations



@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@kotlin.annotation.Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
annotation class Rights(val rights: Array<String>)
