package ru.vood.context.bigDto

import kotlin.reflect.KProperty0

interface IBusinessContext {

    val includedContextProperty: List<KProperty0<AbstractContextParam<*, *>>>

    val v: Map<String, KProperty0<AbstractContextParam<*, *>>>
        get() = includedContextProperty.associateBy { it.name }



    fun mutableMethods(): List<Pair<String, MutableMethod>> {
        return includedContextProperty
            .flatMap { prop -> prop.invoke().mutableMethods.map { v -> prop.name to v } }
            .sortedBy { it.second.time }
    }

}