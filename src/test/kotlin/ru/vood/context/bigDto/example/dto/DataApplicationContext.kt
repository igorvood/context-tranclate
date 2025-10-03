package ru.vood.context.bigDto.example.dto

import kotlinx.serialization.Serializable
import ru.vood.context.bigDto.*
import ru.vood.context.bigDto.ImmutableContextParam.Companion.pendingImmutable
import ru.vood.context.bigDto.MutableContextParam.Companion.pendingMutable
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty0

@Serializable
data class DataApplicationContext(
    val traceId: String,
    val activityId: String,
    val dealInfo: ImmutableContextParam<DealInfo, SomeError> = pendingImmutable(),
    val productInfo: MutableContextParam<ProductInfos, SomeError> = pendingMutable(),
    val participantInfo: ImmutableContextParam<ParticipantInfo?, SomeError> = pendingImmutable(),
    val riskInfo: MutableContextParam<RiskInfo?, SomeError> = pendingMutable(),
) : IBusinessContext<DataApplicationContext> {

    override val propsCTXMeta: List<CTXMeta<DataApplicationContext, *, *, *>>
        get() {
            return listOf(
                DataApplicationContext::dealInfo withCopy { q, w -> q.copy(dealInfo = w) } ,
                (DataApplicationContext::productInfo withCopy { q, w -> q.copy(productInfo = w) }) erichedAfther DataApplicationContext::dealInfo,
                DataApplicationContext::participantInfo withCopy { q, w -> q.copy(participantInfo = w) } erichedAfther DataApplicationContext::productInfo,
                DataApplicationContext::riskInfo withCopy { q, w -> q.copy(riskInfo = w) } erichedAfther DataApplicationContext::participantInfo erichedAfther DataApplicationContext::dealInfo
            )
        }

//    fun <T : IContextParam, E : IEnrichError> enrichError1(
//        error: E,
//        prop: KProperty1<DataApplicationContext, AbstractContextParam<T, E>>,
//        method: KFunction<*>
//    ): DataApplicationContext {
//        val enrichError = prop(this).enrichError(error, method)
//        val context = when (prop) {
//            DataApplicationContext::dealInfo -> copy(dealInfo = enrichError as ImmutableContextParam<DealInfo, SomeError>)
//            DataApplicationContext::productInfo -> copy(productInfo = enrichError as MutableContextParam<ProductInfos, SomeError>)
//            DataApplicationContext::participantInfo -> copy(participantInfo = enrichError as ImmutableNullableContextParam<ParticipantInfo, SomeError>)
//            DataApplicationContext::riskInfo -> copy(riskInfo = enrichError as MutableNullableContextParam<RiskInfo, SomeError>)
//            else -> error("Unknown property: $prop")
//        }
//
//        return context
//    }

/*
    fun <T : IContextParam, E : IEnrichError> enrich(
        data: T?,
        prop: KProperty1<DataApplicationContext, AbstractContextParam<T, E>>,
        method: KFunction<*>
    ): DataApplicationContext {
        val prop1 = prop(this)
        val param = when (prop1) {
            is ImmutableContextParam<T, E> -> {
//                assertImmutable()
                prop1.copy(
                    result = data?.right(),
                    mutableMethods = prop1.mutableMethods.plus(MutableMethod(method))
                )
            }

            is ImmutableNullableContextParam<T, E> -> {
//                assertImmutable()
                prop1.copy(
                    result = data?.right(),
                    mutableMethods = prop1.mutableMethods.plus(MutableMethod(method))
                )
            }

            is MutableContextParam<T, E> -> prop1.copy(
                result = data?.right(),
                mutableMethods = prop1.mutableMethods.plus(MutableMethod(method))
            )

            is MutableNullableContextParam<T, E> -> prop1.copy(
                result = data?.right(),
                mutableMethods = prop1.mutableMethods.plus(MutableMethod(method))
            )
        }



        TODO()
//        return this.dealInfo.success(dealInfo, method)
//            .let { this.copy(dealInfo = it) }
    }
*/

    fun enrich(dealInfo: DealInfo, method: KFunction<*>): DataApplicationContext {
        val property: KProperty0<ImmutableContextParam<DealInfo, SomeError>> = this::dealInfo
        return this.dealInfo.enrichOk(dealInfo, method)
            .let { this.copy(dealInfo = it) }
    }

    fun enrich(productInfo: Set<ProductInfo>, method: KFunction<*>): DataApplicationContext {
        require(dealInfo.isReceived()) { "не могу принять ProductInfo, он должен быть принят после dealInfo" }
        return this.productInfo.success(ProductInfos(productInfo), method)
            .let { this.copy(productInfo = it) }
    }

    fun enrich(participantInfo: ParticipantInfo, method: KFunction<*>): DataApplicationContext {
        return this.participantInfo.enrichOk(participantInfo, method)
            .let { this.copy(participantInfo = it) }
    }

    fun enrich(riskInfo: RiskInfo, method: KFunction<*>): DataApplicationContext {
        return this.riskInfo.success(riskInfo, method)
            .let { this.copy(riskInfo = it) }
    }

    val mutationInfo by lazy { mutableMethods() }

}