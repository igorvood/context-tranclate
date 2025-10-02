package ru.vood.context.bigDto

import kotlin.reflect.KFunction
import kotlin.reflect.KProperty0
import kotlin.reflect.KProperty1

data class CTXMeta<BC : IBusinessContext<BC>, T : IContextParam, E : IEnrichError, CP : AbstractContextParam<T, E>>(
    val prop: KProperty0<CP>,
    val copyFun: (BC, CP) -> (BC)
)

infix fun <BC : IBusinessContext<BC>, T : IContextParam, E : IEnrichError, CP : AbstractContextParam<T, E>> KProperty0<CP>.withCopy(
    fc: (BC, CP) -> (BC)
): CTXMeta<BC, T, E, CP> {
    return CTXMeta(this, fc)
}

interface IBusinessContext<BC : IBusinessContext<BC>> {

    val propsCTXMeta: List<CTXMeta<BC, *, *, *>>

    val includedContextProperty: List<KProperty0<AbstractContextParam<IContextParam, out IEnrichError>>>
        get() = propsCTXMeta.map { it.prop }

    val propsCTXMetaMap: Map<String, CTXMeta<BC, *, *, *>>
        get() = propsCTXMeta.associateBy { it.prop.name }

    fun <T : IContextParam, E : IEnrichError> enrichError(
        prop: KProperty1<BC, AbstractContextParam<T, E>>,
        error: E,
        method: KFunction<*>
    ): BC {
        val meta: CTXMeta<BC, T, E, AbstractContextParam<T, E>> = (propsCTXMetaMap[prop.name] ?: error("this error un imposible")).let { it as CTXMeta<BC, T, E, AbstractContextParam<T, E>> }
        val enrichedParam: AbstractContextParam<T, E> = meta.prop.invoke().enrichError(error,method)
        return meta.copyFun(this as BC, enrichedParam)
    }

//    fun <T : IContextParam, E : IEnrichError> enrichOk(
//        prop: KProperty1<BC, AbstractContextParam<T, E>>,
//        data: T,
//        method: KFunction<*>
//    ): BC {
//        val meta: CTXMeta<BC, T, E, AbstractContextParam<T, E>> = (propsCTXMetaMap[prop.name] ?: error("this error un imposible")).let { it as CTXMeta<BC, T, E, AbstractContextParam<T, E>> }
//        val enrichedParam: AbstractContextParam<T, E> = meta.prop.invoke().enrichError(data,method)
//        return meta.copyFun(this as BC, enrichedParam)
//    }

    fun mutableMethods(): List<Pair<String, MutableMethod>> {
        return includedContextProperty
            .flatMap { prop -> prop.invoke().mutableMethods.map { v -> prop.name to v } }
            .sortedBy { it.second.time }
    }

}