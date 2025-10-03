package ru.vood.context.bigDto

import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1

data class CTXMeta<BC : IBusinessContext<BC>, T : IContextParam?, E : IEnrichError, CP : AbstractContextParam<T, E>>(
    val prop: KProperty1<BC, CP>,
    val copyFun: (BC, CP) -> (BC),
    val mustEnrichedAfter: Set<String> = setOf()
)

infix fun <BC : IBusinessContext<BC>, T : IContextParam?, E : IEnrichError, CP : AbstractContextParam<T, E>> KProperty1<BC, CP>.withCopy(
    fc: (BC, CP) -> (BC)
): CTXMeta<BC, T, E, CP> {
    return CTXMeta(this, fc)
}

infix fun <BC : IBusinessContext<BC>, T : IContextParam?, E : IEnrichError, CP : AbstractContextParam<T, E>> CTXMeta<BC, T, E, CP>.erichedAfther(
    otherProp: KProperty1<BC, *>
): CTXMeta<BC, T, E, CP> {
    require(this.prop.name != otherProp.name) { "property '${this.prop.name}' cannot be enriched after it self" }
    return this.copy(mustEnrichedAfter = this.mustEnrichedAfter.plus(otherProp.name))
}

interface IBusinessContext<BC : IBusinessContext<BC>> {

    val propsCTXMeta: List<CTXMeta<BC, *, *, *>>

    val includedContextProperty
        get() = propsCTXMeta.map { it.prop }

    val propsCTXMetaMap: Map<String, CTXMeta<BC, *, *, *>>
        get() = propsCTXMeta.associateBy { it.prop.name }

    fun <T : IContextParam, E : IEnrichError> enrichError(
        prop: KProperty1<BC, AbstractContextParam<T, E>>,
        error: E,
        method: KFunction<*>
    ): BC {
        val meta: CTXMeta<BC, T, E, AbstractContextParam<T, E>> = (propsCTXMetaMap[prop.name]
            ?: error("this error un imposible")).let { it as CTXMeta<BC, T, E, AbstractContextParam<T, E>> }
        val enrichedParam: AbstractContextParam<T, E> = meta.prop.invoke(this as BC).enrichError(error, method)
        return meta.copyFun(this as BC, enrichedParam)
    }

    fun <T : IContextParam?, E : IEnrichError> enrichOk(
        prop: KProperty1<BC, AbstractContextParam<T, E>>,
        data: T,
        method: KFunction<*>
    ): BC {
        val meta: CTXMeta<BC, T, E, AbstractContextParam<T, E>> = (propsCTXMetaMap[prop.name]
            ?: error("this error un imposible")).let { it as CTXMeta<BC, T, E, AbstractContextParam<T, E>> }
        val notRecived = notRecived(meta)
        require(notRecived.isEmpty()) { "$notRecived must be recived before '${prop.name}'" }
        val enrichedParam: AbstractContextParam<T, E> = meta.prop.invoke(this as BC).enrichOk(data, method)
        return meta.copyFun(this as BC, enrichedParam)
    }

    private fun <T : IContextParam?, E : IEnrichError> notRecived(meta: CTXMeta<BC, T, E, AbstractContextParam<T, E>>): List<String> {
        val filter = meta.mustEnrichedAfter
            .filter { nameAttribute ->
                !(propsCTXMetaMap[nameAttribute]?.prop?.invoke(this as BC)?.isReceived() ?: false)
            }

        return filter
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
            .flatMap { prop -> prop.invoke(this as BC).mutableMethods.map { v -> prop.name to v } }
            .sortedBy { it.second.time }
    }

}