package ru.vood.context.bigDto

import kotlin.reflect.KProperty0

interface IBusinessContext {

    val includedContextParam: List<AbstractContextParam<*, *>>

    val includedContextProperty: List<KProperty0<AbstractContextParam<*, *>>>

    fun mutableMethods(): List<Pair<String, MutableMethod>> {
        return includedContextProperty
            .flatMap { prop -> prop.invoke().mutableMethods.map { v -> prop.name to v } }
            .sortedBy { it.second.time }
    }

}