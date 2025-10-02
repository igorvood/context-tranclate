package ru.vood.context.bigDto.example.dto

import arrow.core.right
import kotlinx.serialization.Serializable
import ru.vood.context.bigDto.*
import ru.vood.context.bigDto.ImmutableNotNullContextParam.Companion.pendingImmutableNotNull
import ru.vood.context.bigDto.ImmutableNullableContextParam.Companion.pendingImmutableNullable
import ru.vood.context.bigDto.MutableNotNullContextParam.Companion.pendingMutableNotNull
import ru.vood.context.bigDto.MutableNullableContextParam.Companion.pendingMutableNullable
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty0
import kotlin.reflect.KProperty1

@Serializable
data class DataApplicationContext(
    val traceId: String,
    val activityId: String,
    val dealInfo: ImmutableNotNullContextParam<DealInfo, SomeError> = pendingImmutableNotNull(),
    val productInfo: MutableNotNullContextParam<ProductInfos, SomeError> = pendingMutableNotNull(),
    val participantInfo: ImmutableNullableContextParam<ParticipantInfo, SomeError> = pendingImmutableNullable(),
    val riskInfo: MutableNullableContextParam<RiskInfo, SomeError> = pendingMutableNullable(),
) : IBusinessContext {

    override val includedContextProperty: List<KProperty0<AbstractContextParam<*, *>>>
        get() = listOf(
            this::dealInfo,
            this::productInfo,
            this::participantInfo,
            this::riskInfo,
        )

    fun <T : IContextParam, E : IEnrichError> enrichError(
        error: E,
        prop: KProperty1<DataApplicationContext, AbstractContextParam<T, E>>,
        method: KFunction<*>
    ): DataApplicationContext {
        val enrichError = prop(this).enrichError(error, method)
        val context = when(prop){
            DataApplicationContext::dealInfo -> copy(dealInfo = enrichError as ImmutableNotNullContextParam<DealInfo, SomeError>)
            DataApplicationContext::productInfo -> copy(productInfo = enrichError as MutableNotNullContextParam<ProductInfos, SomeError>)
            DataApplicationContext::participantInfo -> copy(participantInfo = enrichError as ImmutableNullableContextParam<ParticipantInfo, SomeError>)
            DataApplicationContext::riskInfo -> copy(riskInfo = enrichError as MutableNullableContextParam<RiskInfo, SomeError>)
            else -> error("Unknown property: $prop")
        }

        return context
    }



    fun <T : IContextParam, E : IEnrichError> enrich(
        data: T?,
        prop: KProperty1<DataApplicationContext, AbstractContextParam<T, E>>,
        method: KFunction<*>
    ): DataApplicationContext {
        val prop1 = prop(this)
        val param = when (prop1) {
            is ImmutableNotNullContextParam<T, E> -> {
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

            is MutableNotNullContextParam<T, E> -> prop1.copy(
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


    fun enrich(dealInfo: DealInfo, method: KFunction<*>): DataApplicationContext {
        val property: KProperty0<ImmutableNotNullContextParam<DealInfo, SomeError>> = this::dealInfo
        return this.dealInfo.success(dealInfo, method)
            .let { this.copy(dealInfo = it) }
    }

    fun enrich(productInfo: Set<ProductInfo>, method: KFunction<*>): DataApplicationContext {
        require(dealInfo.isReceived()) { "не могу принять ProductInfo, он должен быть принят после dealInfo" }
        return this.productInfo.success(ProductInfos(productInfo), method)
            .let { this.copy(productInfo = it) }
    }

    fun enrich(participantInfo: ParticipantInfo, method: KFunction<*>): DataApplicationContext {
        return this.participantInfo.success(participantInfo, method)
            .let { this.copy(participantInfo = it) }
    }

    fun enrich(riskInfo: RiskInfo, method: KFunction<*>): DataApplicationContext {
        return this.riskInfo.success(riskInfo, method)
            .let { this.copy(riskInfo = it) }
    }

    val mutationInfo by lazy { mutableMethods() }

}