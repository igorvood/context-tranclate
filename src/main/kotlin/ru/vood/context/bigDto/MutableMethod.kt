package ru.vood.context.bigDto

import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaMethod

@Serializable
data class MutableMethod(
    val methodName: String,
    @Serializable(LocalDateTimeSerializer::class)
    val time: LocalDateTime = LocalDateTime.now(),
) {
    constructor(method: KFunction<*>) : this(method.javaMethod?.declaringClass?.canonicalName + "." + method.name)
}
