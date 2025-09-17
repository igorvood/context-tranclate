package ru.vood.context.bigDto

import java.time.LocalDateTime
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaMethod

data class MutableMethod(
    val methodName: String,
    val time: LocalDateTime = LocalDateTime.now(),
) {
    constructor(method: KFunction<*>) : this(method.javaMethod?.declaringClass?.canonicalName + "." + method.name)
}
